package com.euler.housekeepingservice.service.impl;

import com.euler.housekeepingservice.model.entity.Customer;
import com.euler.housekeepingservice.mapper.CustomerMapper;
import com.euler.housekeepingservice.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户个人档案表 服务实现类
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

}
