package com.euler.housekeepingservice.service.impl;

import cn.hutool.core.util.IdUtil;
import com.euler.housekeepingservice.common.BizException;
import com.euler.housekeepingservice.model.dto.OrderCompleteDTO;
import com.euler.housekeepingservice.model.dto.OrderCreateDTO;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.mapper.OrderMapper;
import com.euler.housekeepingservice.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 家政服务预约订单表 服务实现类
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    /**
     * 1. 客户发起预约 (下单)
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long customerId, OrderCreateDTO dto) {
        Order order = new Order();
        // 使用 Hutool 生成唯一的业务流水号 (例如: ORD-20260313-xxxx)
        order.setOrderNo("ORD-" + IdUtil.getSnowflakeNextIdStr());
        order.setCustomerId(customerId);
        order.setServiceId(dto.getServiceId());
        order.setProfessionalId(dto.getProfessionalId());
        order.setTotalAmount(dto.getTotalAmount());
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        order.setServiceAddress(dto.getServiceAddress());

        order.setOrderStatus((byte) 10); // 10-待接单
        order.setRequestTime(LocalDateTime.now());

        this.save(order);
        return order;
    }

    /**
     * 客户：取消订单
     */
    public void cancelOrder(Long orderId, Long customerId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        // 防越权：只能取消自己的订单
        if (!order.getCustomerId().equals(customerId)) {
            throw new BizException("无权操作他人的订单");
        }
        // 防脏写：只能取消“待接单(10)”状态的订单
        // 如果师傅已经接单(20)，就不允许客户单方面取消了，必须联系客服
        if (order.getOrderStatus() != 10) {
            throw new BizException("当前状态无法取消订单，师傅可能已接单");
        }

        // 将状态改为 50 (已取消)
        order.setOrderStatus((byte) 50);
        this.updateById(order);
    }

    /**
     * 2. 师傅接单或拒单
     */
    @Transactional(rollbackFor = Exception.class)
    public void processOrder(Long orderId, Long professionalId, Integer actionStatus) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }

        // 【状态机校验】只有“待接单(10)”状态才能操作
        if (order.getOrderStatus() != 10) {
            throw new BizException("当前订单状态不允许接单/拒单");
        }

        // 校验是不是指定给这个师傅的单子
        if (order.getProfessionalId() != null && !order.getProfessionalId().equals(professionalId)) {
            throw new BizException("无权操作他人的派单");
        }

        order.setProfessionalId(professionalId); // 绑定接单师傅
        order.setOrderStatus(actionStatus.byteValue()); // 20-已接单，30-已拒单
        order.setAcceptTime(LocalDateTime.now());

        this.updateById(order);
    }

    /**
     * 3. 客户验收结单并评价
     */
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId, Long customerId, OrderCompleteDTO dto) {
        Order order = this.getById(orderId);
        if (order == null || !order.getCustomerId().equals(customerId)) {
            throw new BizException("无权操作此订单");
        }

        // 【状态机校验】只有“已接单(20)”状态才能结单
        if (order.getOrderStatus() != 20) {
            throw new BizException("师傅尚未接单或订单已结束，无法验收");
        }

        order.setOrderStatus((byte) 40); // 40-已完成
        order.setCompleteTime(LocalDateTime.now());
        order.setRatingScore(dto.getRatingScore());
        order.setCustomerRemarks(dto.getCustomerRemarks());

        this.updateById(order);
    }
}
