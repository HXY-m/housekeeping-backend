package com.euler.housekeepingservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.euler.housekeepingservice.mapper.OperationLogMapper;
import com.euler.housekeepingservice.model.entity.OperationLog;
import com.euler.housekeepingservice.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    @Override
    public void log(String moduleName, String actionName, Long operatorId, Integer operatorRole, Long targetId, String description) {
        OperationLog log = new OperationLog();
        log.setModuleName(moduleName);
        log.setActionName(actionName);
        log.setOperatorId(operatorId);
        log.setOperatorRole(operatorRole);
        log.setTargetId(targetId);
        log.setDescription(description);
        this.save(log);
    }
}
