package com.euler.housekeepingservice.mapper;

import com.euler.housekeepingservice.model.entity.Service;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 服务类目字典表 Mapper 接口
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Mapper
public interface ServiceMapper extends BaseMapper<Service> {

}
