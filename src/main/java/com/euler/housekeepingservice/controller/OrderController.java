package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.dto.OrderCompleteDTO;
import com.euler.housekeepingservice.model.dto.OrderCreateDTO;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.OrderService;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 家政服务预约订单表 前端控制器
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // ==========================================
    // 通用与客户 API
    // ==========================================

    /**
     * 客户：发起服务预约 (支持指定师傅或放入大厅)
     */
    @PostMapping
    public Result<Order> createOrder(@Validated @RequestBody OrderCreateDTO dto) {
        Long customerId = SecurityUtils.getUserId();
        Order order = orderService.createOrder(customerId, dto);
        return Result.success(order);
    }

    /**
     * 客户/师傅：获取自己的订单列表 (严格隔离)
     */
    @GetMapping
    public Result<List<Order>> getMyOrders() {
        Long userId = SecurityUtils.getUserId();
        User currentUser = userService.getById(userId);

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        if (currentUser.getRole() == 2) {
            // 客户：看自己发起的单子
            queryWrapper.eq(Order::getCustomerId, userId);
        } else if (currentUser.getRole() == 3) {
            // 师傅：只看【属于自己】的单子 (包括被客户指定给我的、我抢到的、我完成的)
            queryWrapper.eq(Order::getProfessionalId, userId);
        }

        queryWrapper.orderByDesc(Order::getCreateTime);
        return Result.success(orderService.list(queryWrapper));
    }

    /**
     * 客户：取消订单
     */
    @PatchMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable("id") Long orderId) {
        Long customerId = SecurityUtils.getUserId();
        orderService.cancelOrder(orderId, customerId);
        return Result.success("订单已成功取消");
    }

    /**
     * 客户：验收并完单评价
     */
    @PatchMapping("/{id}/completion")
    public Result<?> completeOrder(@PathVariable("id") Long orderId,
                                   @Validated @RequestBody OrderCompleteDTO dto) {
        Long customerId = SecurityUtils.getUserId();
        orderService.completeOrder(orderId, customerId, dto);
        return Result.success("订单验收完成，感谢您的评价！");
    }

    // ==========================================
    // 师傅端专属 API
    // ==========================================

    /**
     * 师傅：抢单大厅 (获取所有无主的待接单)
     */
    @GetMapping("/available")
    public Result<List<Order>> getAvailableOrders() {
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, 10)
                .isNull(Order::getProfessionalId) // 【关键修复】：必须是没有任何师傅绑定的单子
                .orderByDesc(Order::getCreateTime));
        return Result.success(list);
    }

    /**
     * 师傅：抢单 或 接受指定的单子
     */
    @PatchMapping("/{id}/accept")
    public Result<?> acceptOrder(@PathVariable("id") Long orderId) {
        // 【关键修复】：绝对信任后端的 SecurityUtils，不接受前端传 proId
        Long professionalId = SecurityUtils.getUserId();

        // 建议在 orderService.processOrder 里面做好防超卖(乐观锁)校验
        orderService.processOrder(orderId, professionalId, 20);
        return Result.success("接单成功！");
    }

    /**
     * 师傅：拒绝客户指定的单子
     */
    @PatchMapping("/{id}/reject")
    public Result<?> rejectOrder(@PathVariable("id") Long orderId) {
        Long professionalId = SecurityUtils.getUserId();
        orderService.processOrder(orderId, professionalId, 30); // 30代表拒单
        return Result.success("已拒绝该订单");
    }

    // ==========================================
    // 管理员专属 API (上帝视角)
    // ==========================================

    @GetMapping("/admin/all")
    public Result<List<Order>> getAllOrdersForAdmin() {
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreateTime));
        return Result.success(list);
    }

    @DeleteMapping("/admin/{id}")
    public Result<?> deleteOrderAsAdmin(@PathVariable("id") Long id) {
        boolean success = orderService.removeById(id);
        if (success) {
            return Result.success("订单已成功删除");
        }
        return Result.error(500, "删除失败，订单可能不存在");
    }
}