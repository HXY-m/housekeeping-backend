package com.euler.housekeepingservice.mapper;

import com.euler.housekeepingservice.model.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 客户个人档案表 Mapper 接口
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}
