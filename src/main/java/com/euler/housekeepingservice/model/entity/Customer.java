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
 * 客户个人档案表
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Getter
@Setter
@TableName("biz_customer")
@ApiModel(value = "Customer对象", description = "客户个人档案表")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("关联sys_user表的ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("客户真实姓名")
    @TableField("full_name")
    private String fullName;

    @ApiModelProperty("联系电话")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("详细服务地址")
    @TableField("address")
    private String address;

    @ApiModelProperty("所在区域邮编")
    @TableField("pin_code")
    private String pinCode;

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
