package com.euler.housekeepingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.euler.housekeepingservice.common.BizException;
import com.euler.housekeepingservice.mapper.OrderMapper;
import com.euler.housekeepingservice.mapper.OrderMessageMapper;
import com.euler.housekeepingservice.model.dto.OrderMessageCreateDTO;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.OrderMessage;
import com.euler.housekeepingservice.service.OrderMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderMessageServiceImpl extends ServiceImpl<OrderMessageMapper, OrderMessage> implements OrderMessageService {
    private final OrderMapper orderMapper;

    public OrderMessageServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public List<OrderMessage> listByOrder(Long orderId, Long userId, Integer role) {
        checkPermission(orderId, userId, role);
        return this.list(new LambdaQueryWrapper<OrderMessage>()
                .eq(OrderMessage::getOrderId, orderId)
                .orderByAsc(OrderMessage::getCreateTime));
    }

    @Override
    public OrderMessage createMessage(Long orderId, Long userId, Integer role, OrderMessageCreateDTO dto) {
        checkPermission(orderId, userId, role);
        OrderMessage message = new OrderMessage();
        message.setOrderId(orderId);
        message.setSenderId(userId);
        message.setSenderRole(role);
        message.setMessageContent(dto.getMessageContent());
        message.setAttachmentUrl(dto.getAttachmentUrl());
        this.save(message);
        return message;
    }

    private void checkPermission(Long orderId, Long userId, Integer role) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException(404, "订单不存在");
        }
        boolean allowed = role == 1
                || userId.equals(order.getCustomerId())
                || userId.equals(order.getProfessionalId());
        if (!allowed) {
            throw new BizException(403, "无权访问该订单沟通记录");
        }
    }
}
