package com.euler.housekeepingservice.mapper;

import com.euler.housekeepingservice.model.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 家政服务预约订单表 Mapper 接口
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
