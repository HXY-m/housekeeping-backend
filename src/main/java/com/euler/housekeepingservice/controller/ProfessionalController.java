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

    // ==========================================
    // 官网公开 API (免登录)
    // ==========================================

    /**
     * 官网首页：获取推荐的金牌师傅列表 (取评分最高的4位)
     */
    @GetMapping("/public/recommend")
    public Result<List<Professional>> getRecommendProfessionals() {
        List<Professional> list = professionalService.list(new LambdaQueryWrapper<Professional>()
                .eq(Professional::getAuditStatus, 1) // 必须是认证通过的
                .orderByDesc(Professional::getRating) // 评分由高到低
                .last("LIMIT 4")); // 只取前4名
        return Result.success(list);
    }

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

            // 【核心修复】：如果师傅当前是"已驳回(2)"状态，他重新提交修改后，必须重置为"待审核(0)"
            if (exist.getAuditStatus() != null && exist.getAuditStatus() == 2) {
                professional.setAuditStatus((byte) 0);
            }
            // (注意：如果前端传来了 auditStatus，为了防止越权，强制覆盖成后端的逻辑)
            // 甚至更严谨的做法是：不允许前端修改审核状态和评分
            if (professional.getAuditStatus() != null && professional.getAuditStatus() == 1) {
                // 防止恶意绕过审核直接变 1
                professional.setAuditStatus(exist.getAuditStatus() == 2 ? (byte)0 : exist.getAuditStatus());
            }

            professionalService.updateById(professional);
        } else {
            // 默认新入驻的师傅是待审核状态 (0)，评分为 5.0
            professional.setAuditStatus((byte) 0);
            professional.setRating(new java.math.BigDecimal("5.0"));
            professionalService.save(professional);
        }
        return Result.success("师傅资料已提交，等待平台审核");
    }

    // ==========================================
    // 管理员专属 API (资质审核)
    // ==========================================

    /**
     * 管理员获取所有师傅的资质档案 (待审核状态优先排在最前面)
     */
    @GetMapping("/admin/list")
    public Result<List<Professional>> getAllProfessionals() {
        List<Professional> list = professionalService.list(new LambdaQueryWrapper<Professional>()
                .orderByAsc(Professional::getAuditStatus) // 0-待审排在最上面
                .orderByDesc(Professional::getCreateTime));
        return Result.success(list);
    }

    /**
     * 管理员审批师傅资质
     * status: 1-审核通过, 2-审核拒绝
     */
    @PatchMapping("/admin/audit/{id}")
    public Result<?> auditProfessional(@PathVariable("id") Long id, @RequestParam("status") Byte status) {
        Professional professional = new Professional();
        professional.setId(id);
        professional.setAuditStatus(status);
        boolean success = professionalService.updateById(professional);

        if (success) {
            return Result.success(status == 1 ? "已通过该师傅的资质审核" : "已驳回该师傅的资质申请");
        }
        return Result.error(500, "审批操作失败");
    }
}
