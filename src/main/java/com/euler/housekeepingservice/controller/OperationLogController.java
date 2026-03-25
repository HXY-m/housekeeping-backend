package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.entity.OperationLog;
import com.euler.housekeepingservice.service.OperationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/operation-logs")
public class OperationLogController {
    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<OperationLog>> list(@RequestParam(required = false) String moduleName) {
        SecurityUtils.requireRole(1);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (moduleName != null && !moduleName.isBlank()) {
            wrapper.eq(OperationLog::getModuleName, moduleName);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return Result.success(operationLogService.list(wrapper));
    }
}
