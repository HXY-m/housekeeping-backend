package com.euler.housekeepingservice.mapper;

import com.euler.housekeepingservice.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统用户表 Mapper 接口
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
