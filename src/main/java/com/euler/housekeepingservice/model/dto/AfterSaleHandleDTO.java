package com.euler.housekeepingservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AfterSaleHandleDTO {
    @NotNull(message = "澶勭悊鐘舵€佷笉鑳戒负绌?")
    private Integer status;

    @NotBlank(message = "澶勭悊璇存槑涓嶈兘涓虹┖")
    private String handleRemark;
}
