package com.euler.housekeepingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.euler.housekeepingservice.common.BizException;
import com.euler.housekeepingservice.mapper.AfterSaleMapper;
import com.euler.housekeepingservice.mapper.OrderMapper;
import com.euler.housekeepingservice.model.dto.AfterSaleCreateDTO;
import com.euler.housekeepingservice.model.dto.AfterSaleHandleDTO;
import com.euler.housekeepingservice.model.entity.AfterSale;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.service.AfterSaleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfterSaleServiceImpl extends ServiceImpl<AfterSaleMapper, AfterSale> implements AfterSaleService {
    private final OrderMapper orderMapper;

    public AfterSaleServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public AfterSale createAfterSale(Long customerId, AfterSaleCreateDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BizException(404, "璁㈠崟涓嶅瓨鍦?");
        }
        if (!customerId.equals(order.getCustomerId())) {
            throw new BizException(403, "鏃犳潈鎿嶄綔浠栦汉璁㈠崟");
        }
        if (order.getOrderStatus() == null || order.getOrderStatus() < 35) {
            throw new BizException(400, "鏈嶅姟灏氭湭瀹屾垚锛屾棤娉曞彂璧峰敭鍚?");
        }
        long count = this.count(new LambdaQueryWrapper<AfterSale>()
                .eq(AfterSale::getOrderId, dto.getOrderId())
                .eq(AfterSale::getCustomerId, customerId));
        if (count > 0) {
            throw new BizException(400, "璇ヨ鍗曞凡鎻愪氦鍞悗");
        }

        AfterSale afterSale = new AfterSale();
        afterSale.setOrderId(dto.getOrderId());
        afterSale.setCustomerId(customerId);
        afterSale.setProfessionalId(order.getProfessionalId());
        afterSale.setFeedbackType(dto.getFeedbackType());
        afterSale.setFeedbackContent(dto.getFeedbackContent());
        afterSale.setEvidenceUrl(dto.getEvidenceUrl());
        afterSale.setStatus(0);
        this.save(afterSale);
        return afterSale;
    }

    @Override
    public List<AfterSale> listMyAfterSales(Long customerId) {
        return this.list(new LambdaQueryWrapper<AfterSale>()
                .eq(AfterSale::getCustomerId, customerId)
                .orderByDesc(AfterSale::getCreateTime));
    }

    @Override
    public void handleAfterSale(Long id, AfterSaleHandleDTO dto) {
        AfterSale afterSale = this.getById(id);
        if (afterSale == null) {
            throw new BizException(404, "鍞悗璁板綍涓嶅瓨鍦?");
        }
        afterSale.setStatus(dto.getStatus());
        afterSale.setHandleRemark(dto.getHandleRemark());
        this.updateById(afterSale);
    }
}
