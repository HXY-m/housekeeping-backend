package com.euler.housekeepingservice.service.impl;

import com.euler.housekeepingservice.model.entity.Service;
import com.euler.housekeepingservice.mapper.ServiceMapper;
import com.euler.housekeepingservice.service.ServiceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 服务类目字典表 服务实现类
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@org.springframework.stereotype.Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, Service> implements ServiceService {

}
