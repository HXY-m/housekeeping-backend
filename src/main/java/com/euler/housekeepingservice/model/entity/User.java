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
 * 系统用户表
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "User对象", description = "系统用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户名(邮箱/手机号)")
    @TableField("username")
    private String username;

    @ApiModelProperty("BCrypt加密密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("角色: 1-Admin, 2-Customer, 3-Professional")
    @TableField("role")
    private Byte role;

    @ApiModelProperty("账号状态: 0-封禁, 1-正常")
    @TableField("status")
    private Byte status;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("逻辑删除: 0-未删除, 1-已删除")
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}
