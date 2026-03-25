package com.euler.housekeepingservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderProgressDTO {
    @NotNull(message = "杩涘害鐘舵€佷笉鑳戒负绌?")
    private Integer orderStatus;

    private String progressNote;

    private String progressImageUrl;
}
