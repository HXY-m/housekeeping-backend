package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.OperationLogService;
import com.euler.housekeepingservice.service.OrderService;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;
    private final OperationLogService operationLogService;

    public UserController(UserService userService, OrderService orderService, PasswordEncoder passwordEncoder, OperationLogService operationLogService) {
        this.userService = userService;
        this.orderService = orderService;
        this.passwordEncoder = passwordEncoder;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<User>> getUserList(@RequestParam(required = false) Integer role) {
        SecurityUtils.requireRole(1);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (role != null) {
            queryWrapper.eq(User::getRole, role);
        }
        queryWrapper.select(User.class, info -> !"password".equals(info.getColumn()));
        return Result.success(userService.list(queryWrapper));
    }

    @PatchMapping("/{id}/status")
    public Result<?> updateUserStatus(@PathVariable("id") Long userId, @RequestParam Integer status) {
        SecurityUtils.requireRole(1);
        User user = new User();
        user.setId(userId);
        user.setStatus(status.byteValue());
        userService.updateById(user);
        operationLogService.log("USER", "STATUS", SecurityUtils.getUserId(), SecurityUtils.getRole(), userId, "Admin updated user status to " + status);
        return Result.success(status == 1 ? "User enabled" : "User disabled");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable("id") Long userId) {
        SecurityUtils.requireRole(1);
        long orderCount = orderService.count(new LambdaQueryWrapper<Order>()
                .eq(Order::getCustomerId, userId)
                .or()
                .eq(Order::getProfessionalId, userId));
        if (orderCount > 0) {
            return Result.error(500, "User has related orders");
        }
        userService.removeById(userId);
        operationLogService.log("USER", "DELETE", SecurityUtils.getUserId(), SecurityUtils.getRole(), userId, "Admin deleted user");
        return Result.success("User deleted");
    }

    @PutMapping("/{id}/password")
    public Result<?> updatePassword(@PathVariable("id") Long userId,
                                    @RequestParam("oldPassword") String oldPassword,
                                    @RequestParam("newPassword") String newPassword) {
        Long currentUserId = SecurityUtils.getUserId();
        Integer role = SecurityUtils.getRole();
        if (!currentUserId.equals(userId) && role != 1) {
            return Result.error(403, "No permission");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "User not found");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error(400, "Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        operationLogService.log("USER", "PASSWORD", currentUserId, role, userId, "Password updated");
        return Result.success("Password updated");
    }
}
