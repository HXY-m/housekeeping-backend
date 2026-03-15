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
 * 服务类目字典表
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Getter
@Setter
@TableName("biz_service")
@ApiModel(value = "Service对象", description = "服务类目字典表")
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("服务名称(如:日常保洁)")
    @TableField("service_name")
    private String serviceName;

    @ApiModelProperty("服务分类标识(如:cleaning, plumbing)")
    @TableField("service_type")
    private String serviceType;

    @ApiModelProperty("基础指导价")
    @TableField("base_price")
    private BigDecimal basePrice;

    @ApiModelProperty("服务描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("状态: 0-下架, 1-上架")
    @TableField("status")
    private Byte status;

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
