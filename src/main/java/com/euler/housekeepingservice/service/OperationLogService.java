package com.euler.housekeepingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.euler.housekeepingservice.model.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {
    void log(String moduleName, String actionName, Long operatorId, Integer operatorRole, Long targetId, String description);
}
