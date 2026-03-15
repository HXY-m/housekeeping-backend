package com.euler.housekeepingservice.controller;

import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.entity.Service;
import com.euler.housekeepingservice.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 服务类目字典表 前端控制器
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    /**
     * 获取所有上架的家政服务列表
     */
    @GetMapping
    public Result<List<Service>> getAllServices() {
        // 直接调用 MyBatis-Plus 提供的 list() 方法查询全表
        return Result.success(serviceService.list());
    }

    /**
     * 2. 管理员：新增服务项目
     */
    @PostMapping
    public Result<?> addService(@RequestBody Service service) {
        serviceService.save(service);
        return Result.success("新增服务成功");
    }

    /**
     * 3. 管理员：修改服务信息 (如改名、改价格)
     */
    @PutMapping("/{id}")
    public Result<?> updateService(@PathVariable("id") Long id, @RequestBody Service service) {
        service.setId(id); // 确保按路径参数的 ID 更新
        serviceService.updateById(service);
        return Result.success("修改服务成功");
    }

    /**
     * 4. 管理员：下架/删除服务项目
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteService(@PathVariable("id") Long id) {
        serviceService.removeById(id);
        return Result.success("删除服务成功");
    }
}
