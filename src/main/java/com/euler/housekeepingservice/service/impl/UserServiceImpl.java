package com.euler.housekeepingservice.service.impl;

import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.mapper.UserMapper;
import com.euler.housekeepingservice.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
