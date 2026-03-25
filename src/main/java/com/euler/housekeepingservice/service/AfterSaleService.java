package com.euler.housekeepingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.euler.housekeepingservice.model.dto.AfterSaleCreateDTO;
import com.euler.housekeepingservice.model.dto.AfterSaleHandleDTO;
import com.euler.housekeepingservice.model.entity.AfterSale;

import java.util.List;

public interface AfterSaleService extends IService<AfterSale> {
    AfterSale createAfterSale(Long customerId, AfterSaleCreateDTO dto);

    List<AfterSale> listMyAfterSales(Long customerId);

    void handleAfterSale(Long id, AfterSaleHandleDTO dto);
}
