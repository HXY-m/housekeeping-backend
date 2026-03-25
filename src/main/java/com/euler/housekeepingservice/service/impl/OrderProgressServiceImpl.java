package com.euler.housekeepingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.euler.housekeepingservice.common.BizException;
import com.euler.housekeepingservice.mapper.OrderMapper;
import com.euler.housekeepingservice.mapper.OrderProgressMapper;
import com.euler.housekeepingservice.model.dto.OrderProgressDTO;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.OrderProgress;
import com.euler.housekeepingservice.service.OrderProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderProgressServiceImpl extends ServiceImpl<OrderProgressMapper, OrderProgress> implements OrderProgressService {
    private final OrderMapper orderMapper;

    public OrderProgressServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public List<OrderProgress> getProgressList(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException(404, "璁㈠崟涓嶅瓨鍦?");
        }
        boolean allowed = userId.equals(order.getCustomerId()) || userId.equals(order.getProfessionalId());
        if (!allowed) {
            throw new BizException(403, "鏃犳潈鏌ョ湅璇ヨ鍗曡繘搴?");
        }
        return this.list(new LambdaQueryWrapper<OrderProgress>()
                .eq(OrderProgress::getOrderId, orderId)
                .orderByAsc(OrderProgress::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appendProgress(Long orderId, Long professionalId, OrderProgressDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException(404, "璁㈠崟涓嶅瓨鍦?");
        }
        if (order.getProfessionalId() == null || !order.getProfessionalId().equals(professionalId)) {
            throw new BizException(403, "鏃犳潈鏇存柊璇ヨ鍗曡繘搴?");
        }
        if (dto.getOrderStatus() != 25 && dto.getOrderStatus() != 35) {
            throw new BizException(400, "鍙敮鎸佹洿鏂颁负鏈嶅姟涓?鎴栧緟楠屾敹");
        }
        if (dto.getOrderStatus() == 25 && order.getOrderStatus() != 20) {
            throw new BizException(400, "褰撳墠璁㈠崟鐘舵€佷笉鑳藉紑濮嬫湇鍔?");
        }
        if (dto.getOrderStatus() == 35 && order.getOrderStatus() != 25) {
            throw new BizException(400, "璇峰厛鏍囪涓烘湇鍔′腑");
        }

        order.setOrderStatus(dto.getOrderStatus().byteValue());
        orderMapper.updateById(order);

        OrderProgress progress = new OrderProgress();
        progress.setOrderId(orderId);
        progress.setOrderStatus(dto.getOrderStatus());
        progress.setProgressNote(dto.getProgressNote());
        progress.setProgressImageUrl(dto.getProgressImageUrl());
        progress.setOperatorId(professionalId);
        this.save(progress);
    }
}
