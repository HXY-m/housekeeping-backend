package com.euler.housekeepingservice.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
public class OrderCompleteDTO {
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "最低1分")
    @Max(value = 5, message = "最高5分")
    private BigDecimal ratingScore;

    private String customerRemarks;
}