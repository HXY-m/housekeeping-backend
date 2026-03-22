package com.euler.housekeepingservice.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 家政服务预约订单表
 * </p>
 *
 * @author Euler
 * @since 2026-03-14
 */
@Getter
@Setter
@TableName("biz_order")
@ApiModel(value = "Order对象", description = "家政服务预约订单表")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("业务订单流水号(雪花算法/特定前缀)")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty("预约的服务ID")
    @TableField("service_id")
    private Long serviceId;

    @ApiModelProperty("下单客户的user_id")
    @TableField("customer_id")
    private Long customerId;

    @ApiModelProperty("接单师傅的user_id(待接单时为NULL)")
    @TableField("professional_id")
    private Long professionalId;

    @ApiModelProperty("订单状态: 10-待接单, 20-已接单, 30-已拒单, 40-已完成, 50-已取消")
    @TableField("order_status")
    private Byte orderStatus;

    @ApiModelProperty("客户发起预约的时间")
    @TableField("request_time")
    private LocalDateTime requestTime;

    @ApiModelProperty("师傅接单/拒单时间")
    @TableField("accept_time")
    private LocalDateTime acceptTime;

    @ApiModelProperty("总金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty("订单完成时间")
    @TableField("complete_time")
    private LocalDateTime completeTime;

    @ApiModelProperty("客户完单后的文字评价")
    @TableField("customer_remarks")
    private String customerRemarks;

    @ApiModelProperty("本次服务客户打分")
    @TableField("rating_score")
    private BigDecimal ratingScore;

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
