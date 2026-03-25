package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.entity.Customer;
import com.euler.housekeepingservice.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Result<Customer> getMyProfile() {
        SecurityUtils.requireRole(2);
        Long userId = SecurityUtils.getUserId();
        Customer customer = customerService.getOne(new LambdaQueryWrapper<Customer>().eq(Customer::getUserId, userId));
        return Result.success(customer);
    }

    @PostMapping
    public Result<?> saveProfile(@RequestBody Customer customer) {
        SecurityUtils.requireRole(2);
        Long userId = SecurityUtils.getUserId();
        customer.setUserId(userId);
        Customer exist = customerService.getOne(new LambdaQueryWrapper<Customer>().eq(Customer::getUserId, userId));
        if (exist != null) {
            customer.setId(exist.getId());
            customerService.updateById(customer);
        } else {
            customerService.save(customer);
        }
        return Result.success("瀹㈡埛璧勬枡淇濆瓨鎴愬姛");
    }
}
