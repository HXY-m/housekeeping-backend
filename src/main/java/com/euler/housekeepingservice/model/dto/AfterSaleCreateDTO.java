package com.euler.housekeepingservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AfterSaleCreateDTO {
    @NotNull(message = "璁㈠崟ID涓嶈兘涓虹┖")
    private Long orderId;

    @NotBlank(message = "鍙嶉绫诲瀷涓嶈兘涓虹┖")
    private String feedbackType;

    @NotBlank(message = "鍙嶉鍐呭涓嶈兘涓虹┖")
    private String feedbackContent;

    private String evidenceUrl;
}
