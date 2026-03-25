package com.euler.housekeepingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.euler.housekeepingservice.model.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
