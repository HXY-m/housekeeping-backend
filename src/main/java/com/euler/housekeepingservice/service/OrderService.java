package com.euler.housekeepingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.euler.housekeepingservice.model.dto.OrderCompleteDTO;
import com.euler.housekeepingservice.model.dto.OrderCreateDTO;
import com.euler.housekeepingservice.model.entity.Order;

/**
 * <p>
 * 家政服务预约订单表 服务类
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
public interface OrderService extends IService<Order> {

    /**
     * 1. 客户发起预约 (下单)
     *
     * @param customerId 客户ID
     * @param dto        下单参数
     * @return 订单实体
     */
    Order createOrder(Long customerId, OrderCreateDTO dto);

    /**
     * 2. 师傅接单或拒单
     *
     * @param orderId        订单ID
     * @param professionalId 师傅ID
     * @param actionStatus   动作状态 (20-接单, 30-拒单)
     */
    void processOrder(Long orderId, Long professionalId, Integer actionStatus);

    /**
     * 3. 客户验收结单并评价
     *
     * @param orderId    订单ID
     * @param customerId 客户ID
     * @param dto        完单评价参数
     */
    void completeOrder(Long orderId, Long customerId, OrderCompleteDTO dto);

    /**
     * 顾客取消订单
     * @param orderId
     * @param customerId
     */
    void cancelOrder(Long orderId, Long customerId);
}
