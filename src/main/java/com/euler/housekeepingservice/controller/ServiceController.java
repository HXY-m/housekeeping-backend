package com.euler.housekeepingservice.controller;

import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.entity.Service;
import com.euler.housekeepingservice.service.ServiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public Result<List<Service>> getAllServices() {
        return Result.success(serviceService.list());
    }

    @PostMapping
    public Result<?> addService(@RequestBody Service service) {
        SecurityUtils.requireRole(1);
        serviceService.save(service);
        return Result.success("鏂板鏈嶅姟鎴愬姛");
    }

    @PutMapping("/{id}")
    public Result<?> updateService(@PathVariable("id") Long id, @RequestBody Service service) {
        SecurityUtils.requireRole(1);
        service.setId(id);
        serviceService.updateById(service);
        return Result.success("淇敼鏈嶅姟鎴愬姛");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteService(@PathVariable("id") Long id) {
        SecurityUtils.requireRole(1);
        serviceService.removeById(id);
        return Result.success("鍒犻櫎鏈嶅姟鎴愬姛");
    }
}
