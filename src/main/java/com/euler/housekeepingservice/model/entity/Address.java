package com.euler.housekeepingservice.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 客户服务地址表
 * </p>
 *
 * @author Euler
 * @since 2026-03-22
 */
@Getter
@Setter
@TableName("biz_address")
@ApiModel(value = "Address对象", description = "客户服务地址表")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("关联sys_user的ID(下单人)")
    @TableField("customer_id")
    private Long customerId;

    @ApiModelProperty("联系人姓名(可能帮别人点)")
    @TableField("contact_name")
    private String contactName;

    @ApiModelProperty("联系电话")
    @TableField("contact_phone")
    private String contactPhone;

    @ApiModelProperty("详细地址(如:xx小区x栋x单元x室)")
    @TableField("address_detail")
    private String addressDetail;

    @ApiModelProperty("是否默认地址: 0-否, 1-是")
    @TableField("is_default")
    private Byte isDefault;

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
