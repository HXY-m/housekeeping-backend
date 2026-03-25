package com.euler.housekeepingservice.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.euler.housekeepingservice.common.BizException;
import com.euler.housekeepingservice.mapper.OrderMapper;
import com.euler.housekeepingservice.model.dto.OrderCompleteDTO;
import com.euler.housekeepingservice.model.dto.OrderCreateDTO;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long customerId, OrderCreateDTO dto) {
        Order order = new Order();
        order.setOrderNo("ORD-" + IdUtil.getSnowflakeNextIdStr());
        order.setCustomerId(customerId);
        order.setServiceId(dto.getServiceId());
        order.setProfessionalId(dto.getProfessionalId());
        order.setTotalAmount(dto.getTotalAmount());
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        order.setServiceAddress(dto.getServiceAddress());
        order.setOrderStatus((byte) 10);
        order.setRequestTime(LocalDateTime.now());
        this.save(order);
        return order;
    }

    @Override
    public void cancelOrder(Long orderId, Long customerId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BizException("璁㈠崟涓嶅瓨鍦?");
        }
        if (!order.getCustomerId().equals(customerId)) {
            throw new BizException("鏃犳潈鎿嶄綔浠栦汉鐨勮鍗?");
        }
        if (order.getOrderStatus() != 10) {
            throw new BizException("褰撳墠鐘舵€佹棤娉曞彇娑堣鍗?");
        }
        order.setOrderStatus((byte) 50);
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOrder(Long orderId, Long professionalId, Integer actionStatus) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BizException("璁㈠崟涓嶅瓨鍦?");
        }
        if (order.getOrderStatus() != 10) {
            throw new BizException("褰撳墠璁㈠崟鐘舵€佷笉鍏佽鎺ュ崟/鎷掑崟");
        }
        if (order.getProfessionalId() != null && !order.getProfessionalId().equals(professionalId)) {
            throw new BizException("鏃犳潈鎿嶄綔浠栦汉鐨勬淳鍗?");
        }
        order.setProfessionalId(professionalId);
        order.setOrderStatus(actionStatus.byteValue());
        order.setAcceptTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId, Long customerId, OrderCompleteDTO dto) {
        Order order = this.getById(orderId);
        if (order == null || !order.getCustomerId().equals(customerId)) {
            throw new BizException("鏃犳潈鎿嶄綔姝よ鍗?");
        }
        if (order.getOrderStatus() != 35) {
            throw new BizException("褰撳墠璁㈠崟灏氭湭杈惧埌鍙獙鏀剁姸鎬?");
        }
        order.setOrderStatus((byte) 40);
        order.setCompleteTime(LocalDateTime.now());
        order.setRatingScore(dto.getRatingScore());
        order.setCustomerRemarks(dto.getCustomerRemarks());
        this.updateById(order);
    }
}
