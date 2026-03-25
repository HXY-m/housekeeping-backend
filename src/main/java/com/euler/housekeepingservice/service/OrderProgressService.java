package com.euler.housekeepingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.euler.housekeepingservice.model.dto.OrderProgressDTO;
import com.euler.housekeepingservice.model.entity.OrderProgress;

import java.util.List;

public interface OrderProgressService extends IService<OrderProgress> {
    List<OrderProgress> getProgressList(Long orderId, Long userId);

    void appendProgress(Long orderId, Long professionalId, OrderProgressDTO dto);
}
