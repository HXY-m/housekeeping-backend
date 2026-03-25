package com.euler.housekeepingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.euler.housekeepingservice.model.dto.OrderMessageCreateDTO;
import com.euler.housekeepingservice.model.entity.OrderMessage;

import java.util.List;

public interface OrderMessageService extends IService<OrderMessage> {
    List<OrderMessage> listByOrder(Long orderId, Long userId, Integer role);

    OrderMessage createMessage(Long orderId, Long userId, Integer role, OrderMessageCreateDTO dto);
}
