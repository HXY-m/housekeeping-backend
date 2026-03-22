package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.entity.Customer;
import com.euler.housekeepingservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 客户个人档案表 前端控制器
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    /**
     * 获取我的客户资料
     */
    @GetMapping
    public Result<Customer> getMyProfile(@RequestParam("userId") Long userId) {
        Customer customer = customerService.getOne(new LambdaQueryWrapper<Customer>().eq(Customer::getUserId, userId));
        return Result.success(customer);
    }

    /**
     * 保存或更新客户资料
     */
    @PostMapping
    public Result<?> saveProfile(@RequestBody Customer customer) {
        Customer exist = customerService.getOne(new LambdaQueryWrapper<Customer>().eq(Customer::getUserId, customer.getUserId()));
        if (exist != null) {
            customer.setId(exist.getId());
            customerService.updateById(customer);
        } else {
            customerService.save(customer);
        }
        return Result.success("客户资料保存成功");
    }
}
