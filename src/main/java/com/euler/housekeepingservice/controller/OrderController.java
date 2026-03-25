package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.dto.OrderCompleteDTO;
import com.euler.housekeepingservice.model.dto.OrderCreateDTO;
import com.euler.housekeepingservice.model.dto.OrderProgressDTO;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.OrderProgress;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.OrderProgressService;
import com.euler.housekeepingservice.service.OrderService;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final OrderProgressService orderProgressService;

    public OrderController(OrderService orderService, UserService userService, OrderProgressService orderProgressService) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderProgressService = orderProgressService;
    }

    @PostMapping
    public Result<Order> createOrder(@Validated @RequestBody OrderCreateDTO dto) {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        Order order = orderService.createOrder(customerId, dto);
        return Result.success(order);
    }

    @GetMapping
    public Result<List<Order>> getMyOrders() {
        Long userId = SecurityUtils.getUserId();
        User currentUser = userService.getById(userId);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        if (currentUser.getRole() == 2) {
            queryWrapper.eq(Order::getCustomerId, userId);
        } else if (currentUser.getRole() == 3) {
            queryWrapper.eq(Order::getProfessionalId, userId);
        }
        queryWrapper.orderByDesc(Order::getCreateTime);
        return Result.success(orderService.list(queryWrapper));
    }

    @PatchMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable("id") Long orderId) {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        orderService.cancelOrder(orderId, customerId);
        return Result.success("璁㈠崟宸叉垚鍔熷彇娑?");
    }

    @PatchMapping("/{id}/completion")
    public Result<?> completeOrder(@PathVariable("id") Long orderId, @Validated @RequestBody OrderCompleteDTO dto) {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        orderService.completeOrder(orderId, customerId, dto);
        return Result.success("璁㈠崟楠屾敹瀹屾垚锛屾劅璋㈡偍鐨勮瘎浠凤紒");
    }

    @GetMapping("/available")
    public Result<List<Order>> getAvailableOrders() {
        SecurityUtils.requireRole(3);
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, 10)
                .isNull(Order::getProfessionalId)
                .orderByDesc(Order::getCreateTime));
        return Result.success(list);
    }

    @PatchMapping("/{id}/accept")
    public Result<?> acceptOrder(@PathVariable("id") Long orderId) {
        SecurityUtils.requireRole(3);
        Long professionalId = SecurityUtils.getUserId();
        orderService.processOrder(orderId, professionalId, 20);
        return Result.success("鎺ュ崟鎴愬姛锛?");
    }

    @PatchMapping("/{id}/reject")
    public Result<?> rejectOrder(@PathVariable("id") Long orderId) {
        SecurityUtils.requireRole(3);
        Long professionalId = SecurityUtils.getUserId();
        orderService.processOrder(orderId, professionalId, 30);
        return Result.success("宸叉嫆缁濊璁㈠崟");
    }

    @GetMapping("/{id}/progress")
    public Result<List<OrderProgress>> getOrderProgress(@PathVariable("id") Long orderId) {
        Long userId = SecurityUtils.getUserId();
        return Result.success(orderProgressService.getProgressList(orderId, userId));
    }

    @PostMapping("/{id}/progress")
    public Result<?> updateOrderProgress(@PathVariable("id") Long orderId, @Validated @RequestBody OrderProgressDTO dto) {
        SecurityUtils.requireRole(3);
        Long professionalId = SecurityUtils.getUserId();
        orderProgressService.appendProgress(orderId, professionalId, dto);
        return Result.success("璁㈠崟杩涘害鏇存柊鎴愬姛");
    }

    @GetMapping("/admin/all")
    public Result<List<Order>> getAllOrdersForAdmin() {
        SecurityUtils.requireRole(1);
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>().orderByDesc(Order::getCreateTime));
        return Result.success(list);
    }

    @DeleteMapping("/admin/{id}")
    public Result<?> deleteOrderAsAdmin(@PathVariable("id") Long id) {
        SecurityUtils.requireRole(1);
        boolean success = orderService.removeById(id);
        if (success) {
            return Result.success("璁㈠崟宸叉垚鍔熷垹闄?");
        }
        return Result.error(500, "鍒犻櫎澶辫触锛岃鍗曞彲鑳戒笉瀛樺湪");
    }
}
