package com.euler.housekeepingservice.service;

import com.euler.housekeepingservice.model.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 客户服务地址表 服务类
 * </p>
 *
 * @author Euler
 * @since 2026-03-22
 */
public interface AddressService extends IService<Address> {
    /**
     * 核心业务：将某个地址设为默认，并自动将其他地址取消默认
     */
    void setDefaultAddress(Long customerId, Long addressId);

}
