package com.euler.housekeepingservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderPaymentDTO {
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
}
