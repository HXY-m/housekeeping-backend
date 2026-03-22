package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.entity.Professional;
import com.euler.housekeepingservice.service.ProfessionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 服务人员资质档案表 前端控制器
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/professional")
public class ProfessionalController {
    @Autowired
    private ProfessionalService professionalService;

    /**
     * 获取我的师傅资料
     */
    @GetMapping
    public Result<Professional> getMyProfile(@RequestParam("userId") Long userId) {
        Professional professional = professionalService.getOne(new LambdaQueryWrapper<Professional>().eq(Professional::getUserId, userId));
        return Result.success(professional);
    }

    /**
     * 客户下单时：根据服务ID获取该服务下所有已审核通过的师傅列表
     */
    @GetMapping("/listByService")
    public Result<List<Professional>> getProfessionalsByServiceId(@RequestParam("serviceId") Long serviceId) {
        // 只查询审核通过(auditStatus = 1) 且 绑定了该服务 的师傅，按评分倒序排列
        List<Professional> list = professionalService.list(new LambdaQueryWrapper<Professional>()
                .eq(Professional::getServiceId, serviceId)
                .eq(Professional::getAuditStatus, 1)
                .orderByDesc(Professional::getRating));
        return Result.success(list);
    }

    /**
     * 保存或更新师傅资料
     */
    @PostMapping
    public Result<?> saveProfile(@RequestBody Professional professional) {
        Professional exist = professionalService.getOne(new LambdaQueryWrapper<Professional>().eq(Professional::getUserId, professional.getUserId()));
        if (exist != null) {
            professional.setId(exist.getId());
            professionalService.updateById(professional);
        } else {
            // 默认新入驻的师傅是待审核状态 (0)，评分为 5.0
            professional.setAuditStatus((byte) 0);
            professional.setRating(new java.math.BigDecimal("5.0"));
            professionalService.save(professional);
        }
        return Result.success("师傅资料保存成功");
    }
}
