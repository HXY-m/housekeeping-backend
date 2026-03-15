package com.euler.housekeepingservice.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 服务人员资质档案表
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Getter
@Setter
@TableName("biz_professional")
@ApiModel(value = "Professional对象", description = "服务人员资质档案表")
public class Professional implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("关联sys_user表的ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("所提供的核心服务ID(关联biz_service)")
    @TableField("service_id")
    private Long serviceId;

    @ApiModelProperty("师傅真实姓名")
    @TableField("full_name")
    private String fullName;

    @ApiModelProperty("从业经验(年)")
    @TableField("experience_years")
    private Integer experienceYears;

    @ApiModelProperty("资质证明文件URL(OSS地址)")
    @TableField("cert_file_url")
    private String certFileUrl;

    @ApiModelProperty("常驻地址")
    @TableField("address")
    private String address;

    @ApiModelProperty("服务辐射邮编")
    @TableField("pin_code")
    private String pinCode;

    @ApiModelProperty("审核状态: 0-待审, 1-审核通过, 2-审核拒绝")
    @TableField("audit_status")
    private Byte auditStatus;

    @ApiModelProperty("综合评分(0.0-5.0)")
    @TableField("rating")
    private BigDecimal rating;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("逻辑删除标志")
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}
