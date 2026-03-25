package com.euler.housekeepingservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderMessageCreateDTO {
    @NotBlank(message = "留言内容不能为空")
    private String messageContent;

    private String attachmentUrl;
}
