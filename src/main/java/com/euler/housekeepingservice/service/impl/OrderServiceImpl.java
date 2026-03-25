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
        order.setPaymentStatus((byte) 0);
        order.setRequestTime(LocalDateTime.now());
        this.save(order);
        return order;
    }

    @Override
    public void processOrder(Long orderId, Long professionalId, Integer actionStatus) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (order.getOrderStatus() != 10) {
            throw new BizException("当前订单状态不允许接单处理");
        }
        if (order.getPaymentStatus() == null || order.getPaymentStatus() != 1) {
            throw new BizException("订单未支付，不能接单");
        }
        if (order.getProfessionalId() != null && !order.getProfessionalId().equals(professionalId)) {
            throw new BizException("该订单已被其他服务人员锁定");
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
            throw new BizException("无权完成该订单");
        }
        if (order.getOrderStatus() != 35) {
            throw new BizException("当前订单还未进入待验收状态");
        }
        order.setOrderStatus((byte) 40);
        order.setCompleteTime(LocalDateTime.now());
        order.setRatingScore(dto.getRatingScore());
        order.setCustomerRemarks(dto.getCustomerRemarks());
        this.updateById(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long customerId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!order.getCustomerId().equals(customerId)) {
            throw new BizException("无权取消该订单");
        }
        if (order.getOrderStatus() != 10) {
            throw new BizException("当前订单状态不允许取消");
        }
        order.setOrderStatus((byte) 50);
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Long customerId, String paymentMethod) {
        Order order = this.getById(orderId);
        if (order == null || !order.getCustomerId().equals(customerId)) {
            throw new BizException("无权支付该订单");
        }
        if (order.getPaymentStatus() != null && order.getPaymentStatus() == 1) {
            throw new BizException("该订单已支付");
        }
        if (order.getOrderStatus() == 30 || order.getOrderStatus() == 50) {
            throw new BizException("当前订单状态不允许支付");
        }
        order.setPaymentStatus((byte) 1);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentNo("PAY-" + IdUtil.getSnowflakeNextIdStr());
        order.setPaymentTime(LocalDateTime.now());
        this.updateById(order);
    }
}
