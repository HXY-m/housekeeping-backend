package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.entity.Address;
import com.euler.housekeepingservice.service.AddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public Result<List<Address>> getMyAddresses() {
        SecurityUtils.requireRole(2);
        Long userId = SecurityUtils.getUserId();
        List<Address> list = addressService.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getCustomerId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
        return Result.success(list);
    }

    @PostMapping
    public Result<?> addAddress(@RequestBody Address address) {
        SecurityUtils.requireRole(2);
        Long userId = SecurityUtils.getUserId();
        address.setCustomerId(userId);
        long count = addressService.count(new LambdaQueryWrapper<Address>()
                .eq(Address::getCustomerId, userId));
        if (count == 0) {
            address.setIsDefault((byte) 1);
        }
        addressService.save(address);
        if (address.getIsDefault() != null && address.getIsDefault() == 1 && count > 0) {
            addressService.setDefaultAddress(userId, address.getId());
        }
        return Result.success("鍦板潃娣诲姞鎴愬姛");
    }

    @PutMapping
    public Result<?> updateAddress(@RequestBody Address address) {
        SecurityUtils.requireRole(2);
        Long userId = SecurityUtils.getUserId();
        Address dbAddress = addressService.getById(address.getId());
        if (dbAddress == null || !userId.equals(dbAddress.getCustomerId())) {
            return Result.error(403, "鏃犳潈淇敼璇ュ湴鍧€");
        }
        address.setCustomerId(userId);
        addressService.updateById(address);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressService.setDefaultAddress(userId, address.getId());
        }
        return Result.success("鍦板潃淇敼鎴愬姛");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteAddress(@PathVariable("id") Long id) {
        SecurityUtils.requireRole(2);
        Long userId = SecurityUtils.getUserId();
        Address dbAddress = addressService.getById(id);
        if (dbAddress == null || !userId.equals(dbAddress.getCustomerId())) {
            return Result.error(403, "鏃犳潈鍒犻櫎璇ュ湴鍧€");
        }
        addressService.removeById(id);
        return Result.success("鍦板潃鍒犻櫎鎴愬姛");
    }

    @PatchMapping("/{id}/default")
    public Result<?> setDefault(@PathVariable("id") Long addressId) {
        SecurityUtils.requireRole(2);
        Long userId = SecurityUtils.getUserId();
        addressService.setDefaultAddress(userId, addressId);
        return Result.success("宸茶涓洪粯璁ゅ湴鍧€");
    }
}
