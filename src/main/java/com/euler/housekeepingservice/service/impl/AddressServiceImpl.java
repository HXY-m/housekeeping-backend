package com.euler.housekeepingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.euler.housekeepingservice.mapper.AddressMapper;
import com.euler.housekeepingservice.model.entity.Address;
import com.euler.housekeepingservice.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 客户服务地址表 服务实现类
 * </p>
 *
 * @author Euler
 * @since 2026-03-22
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证两步更新要么都成功要么都失败
    public void setDefaultAddress(Long customerId, Long addressId) {
        // 1. 先把该客户的所有地址都设为非默认 (isDefault = 0)
        this.update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getCustomerId, customerId)
                .set(Address::getIsDefault, 0));

        // 2. 再把指定的这个地址设为默认 (isDefault = 1)
        this.update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getCustomerId, customerId)
                .set(Address::getIsDefault, 1));
    }
}
