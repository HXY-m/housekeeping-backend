package com.euler.housekeepingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.euler.housekeepingservice.model.dto.OrderCompleteDTO;
import com.euler.housekeepingservice.model.dto.OrderCreateDTO;
import com.euler.housekeepingservice.model.entity.Order;

public interface OrderService extends IService<Order> {
    Order createOrder(Long customerId, OrderCreateDTO dto);

    void processOrder(Long orderId, Long professionalId, Integer actionStatus);

    void completeOrder(Long orderId, Long customerId, OrderCompleteDTO dto);

    void cancelOrder(Long orderId, Long customerId);

    void payOrder(Long orderId, Long customerId, String paymentMethod);
}
