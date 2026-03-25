package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.BizException;
import com.euler.housekeepingservice.common.JwtUtils;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.dto.LoginDTO;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.OperationLogService;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final OperationLogService operationLogService;

    public AuthController(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, OperationLogService operationLogService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.operationLogService = operationLogService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO dto) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BizException("User not found");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(403, "Account disabled");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException("Wrong password");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole().intValue());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("role", user.getRole());
        data.put("username", user.getUsername());

        operationLogService.log("AUTH", "LOGIN", user.getId(), user.getRole().intValue(), user.getId(), "User logged in");
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User registerUser) {
        if (registerUser.getUsername() == null || registerUser.getPassword() == null || registerUser.getRole() == null) {
            return Result.error(400, "Username, password and role are required");
        }
        long count = userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, registerUser.getUsername()));
        if (count > 0) {
            return Result.error(400, "Username already exists");
        }
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        registerUser.setStatus((byte) 1);
        userService.save(registerUser);
        operationLogService.log("AUTH", "REGISTER", registerUser.getId(), registerUser.getRole().intValue(), registerUser.getId(), "New user registered");
        return Result.success("Register success");
    }
}
