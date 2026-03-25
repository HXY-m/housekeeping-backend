package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.dto.OrderCompleteDTO;
import com.euler.housekeepingservice.model.dto.OrderCreateDTO;
import com.euler.housekeepingservice.model.dto.OrderMessageCreateDTO;
import com.euler.housekeepingservice.model.dto.OrderPaymentDTO;
import com.euler.housekeepingservice.model.dto.OrderProgressDTO;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.OrderMessage;
import com.euler.housekeepingservice.model.entity.OrderProgress;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.OperationLogService;
import com.euler.housekeepingservice.service.OrderMessageService;
import com.euler.housekeepingservice.service.OrderProgressService;
import com.euler.housekeepingservice.service.OrderService;
import com.euler.housekeepingservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final OrderProgressService orderProgressService;
    private final OrderMessageService orderMessageService;
    private final OperationLogService operationLogService;

    public OrderController(OrderService orderService,
                           UserService userService,
                           OrderProgressService orderProgressService,
                           OrderMessageService orderMessageService,
                           OperationLogService operationLogService) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderProgressService = orderProgressService;
        this.orderMessageService = orderMessageService;
        this.operationLogService = operationLogService;
    }

    @PostMapping
    public Result<Order> createOrder(@Validated @RequestBody OrderCreateDTO dto) {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        Order order = orderService.createOrder(customerId, dto);
        operationLogService.log("ORDER", "CREATE", customerId, SecurityUtils.getRole(), order.getId(),
                "Customer created order " + order.getOrderNo());
        return Result.success(order);
    }

    @PatchMapping("/{id}/payment")
    public Result<?> payOrder(@PathVariable("id") Long orderId, @Valid @RequestBody OrderPaymentDTO dto) {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        orderService.payOrder(orderId, customerId, dto.getPaymentMethod());
        operationLogService.log("ORDER", "PAY", customerId, SecurityUtils.getRole(), orderId,
                "Customer paid order by " + dto.getPaymentMethod());
        return Result.success("Payment completed");
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
        operationLogService.log("ORDER", "CANCEL", customerId, SecurityUtils.getRole(), orderId, "Customer canceled order");
        return Result.success("Order canceled");
    }

    @PatchMapping("/{id}/completion")
    public Result<?> completeOrder(@PathVariable("id") Long orderId, @Validated @RequestBody OrderCompleteDTO dto) {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        orderService.completeOrder(orderId, customerId, dto);
        operationLogService.log("ORDER", "COMPLETE", customerId, SecurityUtils.getRole(), orderId, "Customer completed order");
        return Result.success("Order completed");
    }

    @GetMapping("/available")
    public Result<List<Order>> getAvailableOrders() {
        SecurityUtils.requireRole(3);
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, 10)
                .eq(Order::getPaymentStatus, 1)
                .isNull(Order::getProfessionalId)
                .orderByDesc(Order::getCreateTime));
        return Result.success(list);
    }

    @PatchMapping("/{id}/accept")
    public Result<?> acceptOrder(@PathVariable("id") Long orderId) {
        SecurityUtils.requireRole(3);
        Long professionalId = SecurityUtils.getUserId();
        orderService.processOrder(orderId, professionalId, 20);
        operationLogService.log("ORDER", "ACCEPT", professionalId, SecurityUtils.getRole(), orderId, "Professional accepted order");
        return Result.success("Order accepted");
    }

    @PatchMapping("/{id}/reject")
    public Result<?> rejectOrder(@PathVariable("id") Long orderId) {
        SecurityUtils.requireRole(3);
        Long professionalId = SecurityUtils.getUserId();
        orderService.processOrder(orderId, professionalId, 30);
        operationLogService.log("ORDER", "REJECT", professionalId, SecurityUtils.getRole(), orderId, "Professional rejected order");
        return Result.success("Order rejected");
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
        operationLogService.log("ORDER", "PROGRESS", professionalId, SecurityUtils.getRole(), orderId,
                "Professional updated order status to " + dto.getOrderStatus());
        return Result.success("Progress updated");
    }

    @GetMapping("/{id}/messages")
    public Result<List<OrderMessage>> getOrderMessages(@PathVariable("id") Long orderId) {
        Long userId = SecurityUtils.getUserId();
        Integer role = SecurityUtils.getRole();
        return Result.success(orderMessageService.listByOrder(orderId, userId, role));
    }

    @PostMapping("/{id}/messages")
    public Result<OrderMessage> createOrderMessage(@PathVariable("id") Long orderId, @Valid @RequestBody OrderMessageCreateDTO dto) {
        Long userId = SecurityUtils.getUserId();
        Integer role = SecurityUtils.getRole();
        OrderMessage message = orderMessageService.createMessage(orderId, userId, role, dto);
        operationLogService.log("ORDER_MESSAGE", "CREATE", userId, role, orderId, "Added a new order message");
        return Result.success(message);
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
            operationLogService.log("ORDER", "DELETE", SecurityUtils.getUserId(), SecurityUtils.getRole(), id, "Admin deleted order");
            return Result.success("Order deleted");
        }
        return Result.error(500, "Delete failed");
    }
}
