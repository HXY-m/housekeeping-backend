package com.euler.housekeepingservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateDTO {
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;

    // 师傅ID可以为空（代表平台派单），也可以指定师傅
    private Long professionalId;
}
