package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.BizException;
import com.euler.housekeepingservice.common.JwtUtils;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.dto.LoginDTO;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证与授权接口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // 引入 Spring Security 提供的 BCrypt 强加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO dto) {
        // 1. 根据用户名查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BizException("用户不存在");
        }

        // 2. 校验账号状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(403, "您的账号已被平台封禁，请联系客服处理！");
        }

        // 3. 校验密码 (使用 BCrypt 的 matches 方法比对明文和数据库中的密文)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException("密码错误");
        }

        // 4. 账号密码正确，签发 JWT Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole().intValue());

        // 5. 封装返回给前端的数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("role", user.getRole());
        data.put("username", user.getUsername());

        return Result.success(data);
    }

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User registerUser) {
        // 1. 基础参数校验
        if (registerUser.getUsername() == null || registerUser.getPassword() == null || registerUser.getRole() == null) {
            return Result.error(400, "用户名、密码和角色不能为空");
        }

        // 2. 防重名校验：去数据库查一下这个名字是不是已经存在了
        long count = userService.count(
                new LambdaQueryWrapper<User>().eq(User::getUsername, registerUser.getUsername())
        );
        if (count > 0) {
            return Result.error(400, "该用户名已被注册，请换一个试试");
        }

        // 3. 核心安全：决不能把明文密码存进数据库！使用 BCrypt 加密
        String encodedPassword = passwordEncoder.encode(registerUser.getPassword());
        registerUser.setPassword(encodedPassword);

        // 4. 赋予初始状态 (1代表正常可用)
        registerUser.setStatus((byte) 1);

        // 5. 存入数据库
        userService.save(registerUser);

        return Result.success("注册成功，欢迎加入平台！");
    }
}