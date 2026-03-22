package com.euler.housekeepingservice.mapper;

import com.euler.housekeepingservice.model.entity.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 客户服务地址表 Mapper 接口
 * </p>
 *
 * @author Euler
 * @since 2026-03-22
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {

}
