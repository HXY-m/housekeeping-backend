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

    /**
     * 客户：发起服务预约
     */
    @PostMapping
    public Result<Order> createOrder(@Validated @RequestBody OrderCreateDTO dto) {
        // 从安全上下文中自动获取真实客户ID
        Long customerId = SecurityUtils.getUserId();
        Order order = orderService.createOrder(customerId, dto);
        return Result.success(order);
    }

    /**
     * 客户/师傅：获取自己的订单列表
     */
    @GetMapping
    public Result<List<Order>> getMyOrders() {
        Long userId = SecurityUtils.getUserId();

        // 1. 查询当前用户的真实角色
        User currentUser = userService.getById(userId);

        // 2. 动态构建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        if (currentUser.getRole() == 2) {
            // 客户：只看自己发起的
            queryWrapper.eq(Order::getCustomerId, userId);
        } else if (currentUser.getRole() == 3) {
            // 【核心升级】：师傅能看到的单子有两种
            queryWrapper.and(wrapper -> wrapper
                    // 情况1：系统明确派给自己的，或者自己已经接下的单子
                    .eq(Order::getProfessionalId, userId)
                    // 情况2 (或者)：无主的新订单（进入抢单池）
                    .or(w -> w.isNull(Order::getProfessionalId).eq(Order::getOrderStatus, 10))
            );
        }

        // 3. 统一按创建时间倒序排列
        queryWrapper.orderByDesc(Order::getCreateTime);

        return Result.success(orderService.list(queryWrapper));
    }

    /**
     * 师傅：接单
     */
    @PatchMapping("/{id}/accept")
    public Result<?> acceptOrder(@PathVariable("id") Long orderId) {
        // 自动获取当前点击接单的真实师傅ID
        Long professionalId = SecurityUtils.getUserId();
        orderService.processOrder(orderId, professionalId, 20); // 20代表接单
        return Result.success("接单成功");
    }

    /**
     * 师傅：拒单
     */
    @PatchMapping("/{id}/reject")
    public Result<?> rejectOrder(@PathVariable("id") Long orderId) {
        Long professionalId = SecurityUtils.getUserId();
        orderService.processOrder(orderId, professionalId, 30); // 30代表拒单
        return Result.success("已拒绝该订单");
    }

    /**
     * 客户：取消订单 (增加的逆向分支)
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
     * 1. 抢单大厅：获取所有待接单 (status = 10) 的订单
     */
    @GetMapping("/available")
    public Result<List<Order>> getAvailableOrders() {
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, 10)
                .orderByDesc(Order::getCreateTime));
        return Result.success(list);
    }

    /**
     * 2. 师傅抢单：将状态改为 20，并绑定师傅ID
     */
    @PatchMapping("/{id}/take")
    public Result<?> takeOrder(@PathVariable("id") Long orderId, @RequestParam("proId") Long proId) {
        Order order = orderService.getById(orderId);
        // 乐观锁/防重校验：如果订单已经被抢走，或者被客户取消了
        if (order == null || order.getOrderStatus() != 10) {
            return Result.error(400, "手慢了，该订单已被抢走或已取消！");
        }

        order.setOrderStatus((byte) 20); // 状态变更为：服务中
        order.setProfessionalId(proId); // 绑定当前接单师傅的 ID
        orderService.updateById(order);

        return Result.success("抢单成功！请尽快联系客户提供服务。");
    }

    /**
     * 3. 我的任务：获取当前师傅自己接下的所有订单
     */
    @GetMapping("/pro/{proId}")
    public Result<List<Order>> getProOrders(@PathVariable("proId") Long proId) {
        List<Order> list = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getProfessionalId, proId)
                .orderByDesc(Order::getCreateTime));
        return Result.success(list);
    }
}