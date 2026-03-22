package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.entity.Address;
import com.euler.housekeepingservice.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 客户服务地址表 前端控制器
 * </p>
 *
 * @author Euler
 * @since 2026-03-22
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    /**
     * 获取我的所有地址列表 (默认地址排在最前面)
     */
    @GetMapping
    public Result<List<Address>> getMyAddresses(@RequestParam("userId") Long userId) {
        List<Address> list = addressService.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getCustomerId, userId)
                .orderByDesc(Address::getIsDefault) // 默认地址排第一
                .orderByDesc(Address::getCreateTime)); // 其余按时间倒序
        return Result.success(list);
    }

    /**
     * 新增地址
     */
    @PostMapping
    public Result<?> addAddress(@RequestBody Address address) {
        // 查一下该用户目前有几个地址
        long count = addressService.count(new LambdaQueryWrapper<Address>()
                .eq(Address::getCustomerId, address.getCustomerId()));

        // 如果这是第一个地址，强制把它变成默认地址
        if (count == 0) {
            address.setIsDefault((byte)1);
        }

        addressService.save(address);

        // 如果这不是第一个地址，但用户勾选了"设为默认"，则触发唯一默认的业务逻辑
        if (address.getIsDefault() != null && address.getIsDefault() == 1 && count > 0) {
            addressService.setDefaultAddress(address.getCustomerId(), address.getId());
        }

        return Result.success("地址添加成功");
    }

    /**
     * 修改地址
     */
    @PutMapping
    public Result<?> updateAddress(@RequestBody Address address) {
        addressService.updateById(address);
        // 如果修改时勾选了"设为默认"
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressService.setDefaultAddress(address.getCustomerId(), address.getId());
        }
        return Result.success("地址修改成功");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteAddress(@PathVariable("id") Long id) {
        addressService.removeById(id);
        return Result.success("地址删除成功");
    }

    /**
     * 快捷操作：单独将某个已有地址设为默认
     */
    @PatchMapping("/{id}/default")
    public Result<?> setDefault(@PathVariable("id") Long addressId, @RequestParam("userId") Long userId) {
        addressService.setDefaultAddress(userId, addressId);
        return Result.success("已设为默认地址");
    }
}
