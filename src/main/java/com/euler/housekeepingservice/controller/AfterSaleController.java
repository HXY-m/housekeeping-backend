package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.dto.AfterSaleCreateDTO;
import com.euler.housekeepingservice.model.dto.AfterSaleHandleDTO;
import com.euler.housekeepingservice.model.entity.AfterSale;
import com.euler.housekeepingservice.service.AfterSaleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/after-sale")
public class AfterSaleController {
    private final AfterSaleService afterSaleService;

    public AfterSaleController(AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    @PostMapping
    public Result<AfterSale> createAfterSale(@Valid @RequestBody AfterSaleCreateDTO dto) {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        return Result.success(afterSaleService.createAfterSale(customerId, dto));
    }

    @GetMapping
    public Result<List<AfterSale>> getMyAfterSales() {
        SecurityUtils.requireRole(2);
        Long customerId = SecurityUtils.getUserId();
        return Result.success(afterSaleService.listMyAfterSales(customerId));
    }

    @GetMapping("/admin/all")
    public Result<List<AfterSale>> getAllAfterSales() {
        SecurityUtils.requireRole(1);
        return Result.success(afterSaleService.list(new LambdaQueryWrapper<AfterSale>()
                .orderByAsc(AfterSale::getStatus)
                .orderByDesc(AfterSale::getCreateTime)));
    }

    @PatchMapping("/admin/{id}")
    public Result<?> handleAfterSale(@PathVariable Long id, @Valid @RequestBody AfterSaleHandleDTO dto) {
        SecurityUtils.requireRole(1);
        afterSaleService.handleAfterSale(id, dto);
        return Result.success("鍞悗璁板綍澶勭悊鎴愬姛");
    }
}
