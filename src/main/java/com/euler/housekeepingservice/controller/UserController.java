package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.OrderService;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * 1. 管理员：获取用户列表 (支持按角色过滤)
     */
    @GetMapping
    public Result<List<User>> getUserList(@RequestParam(required = false) Integer role) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 如果前端传了 role 参数，就按角色过滤；没传就查所有人
        if (role != null) {
            queryWrapper.eq(User::getRole, role);
        }

        // 【安全细节】企业级开发中，无论何时都不要把密码返回给前端！
        queryWrapper.select(User.class, info -> !info.getColumn().equals("password"));

        return Result.success(userService.list(queryWrapper));
    }

    /**
     * 2. 管理员：封禁/解封用户账号
     */
    @PatchMapping("/{id}/status")
    public Result<?> updateUserStatus(@PathVariable("id") Long userId, @RequestParam Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status.byteValue()); // status: 1正常，0封禁
        userService.updateById(user);

        String msg = status == 1 ? "账号已解封" : "账号已封禁";
        return Result.success(msg);
    }

    /**
     * 3. 管理员：物理删除用户 (注销账号)
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable("id") Long userId) {
        long orderCount = orderService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.euler.housekeepingservice.model.entity.Order>()
                        .eq(com.euler.housekeepingservice.model.entity.Order::getCustomerId, userId)
                        .or()
                        .eq(com.euler.housekeepingservice.model.entity.Order::getProfessionalId, userId)
        );

        if (orderCount > 0) {
            return Result.error(500, "注销失败：该账号名下有历史订单交易，为保证账单完整性，无法物理删除，请使用【封禁】功能！");
        }
        userService.removeById(userId);
        return Result.success("账号注销成功");
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * 修改个人密码
     */
    @PutMapping("/{id}/password")
    public Result<?> updatePassword(@PathVariable("id") Long userId,
                                    @RequestParam("oldPassword") String oldPassword,
                                    @RequestParam("newPassword") String newPassword) {
        // 1. 获取当前用户
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 2. 校验旧密码是否正确 (明文与数据库密文比对)
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error(400, "原密码输入错误，请重试！");
        }

        // 3. 对新密码进行加密
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);

        // 4. 更新到数据库
        userService.updateById(user);

        return Result.success("密码修改成功，请重新登录！");
    }
}
