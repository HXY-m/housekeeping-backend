package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.entity.Professional;
import com.euler.housekeepingservice.service.ProfessionalService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/professional")
public class ProfessionalController {
    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping("/public/recommend")
    public Result<List<Professional>> getRecommendProfessionals() {
        List<Professional> list = professionalService.list(new LambdaQueryWrapper<Professional>()
                .eq(Professional::getAuditStatus, 1)
                .orderByDesc(Professional::getRating)
                .last("LIMIT 4"));
        return Result.success(list);
    }

    @GetMapping
    public Result<Professional> getMyProfile() {
        SecurityUtils.requireRole(3);
        Long userId = SecurityUtils.getUserId();
        Professional professional = professionalService.getOne(new LambdaQueryWrapper<Professional>().eq(Professional::getUserId, userId));
        return Result.success(professional);
    }

    @GetMapping("/listByService")
    public Result<List<Professional>> getProfessionalsByServiceId(@RequestParam("serviceId") Long serviceId) {
        List<Professional> list = professionalService.list(new LambdaQueryWrapper<Professional>()
                .eq(Professional::getServiceId, serviceId)
                .eq(Professional::getAuditStatus, 1)
                .orderByDesc(Professional::getRating));
        return Result.success(list);
    }

    @PostMapping
    public Result<?> saveProfile(@RequestBody Professional professional) {
        SecurityUtils.requireRole(3);
        Long userId = SecurityUtils.getUserId();
        professional.setUserId(userId);
        Professional exist = professionalService.getOne(new LambdaQueryWrapper<Professional>().eq(Professional::getUserId, userId));
        if (exist != null) {
            professional.setId(exist.getId());
            professional.setAuditStatus(exist.getAuditStatus() != null && exist.getAuditStatus() == 2 ? (byte) 0 : exist.getAuditStatus());
            professional.setRating(exist.getRating());
            professionalService.updateById(professional);
        } else {
            professional.setAuditStatus((byte) 0);
            professional.setRating(new BigDecimal("5.0"));
            professionalService.save(professional);
        }
        return Result.success("甯堝倕璧勬枡宸叉彁浜わ紝绛夊緟骞冲彴瀹℃牳");
    }

    @GetMapping("/admin/list")
    public Result<List<Professional>> getAllProfessionals() {
        SecurityUtils.requireRole(1);
        List<Professional> list = professionalService.list(new LambdaQueryWrapper<Professional>()
                .orderByAsc(Professional::getAuditStatus)
                .orderByDesc(Professional::getCreateTime));
        return Result.success(list);
    }

    @PatchMapping("/admin/audit/{id}")
    public Result<?> auditProfessional(@PathVariable("id") Long id, @RequestParam("status") Byte status) {
        SecurityUtils.requireRole(1);
        Professional professional = new Professional();
        professional.setId(id);
        professional.setAuditStatus(status);
        boolean success = professionalService.updateById(professional);
        if (success) {
            return Result.success(status == 1 ? "宸查€氳繃璇ュ笀鍌呯殑璧勮川瀹℃牳" : "宸查┏鍥炶甯堝倕鐨勮祫璐ㄧ敵璇?");
        }
        return Result.error(500, "瀹℃壒鎿嶄綔澶辫触");
    }
}
