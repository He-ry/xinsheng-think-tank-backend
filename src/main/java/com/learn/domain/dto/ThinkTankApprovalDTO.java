package com.learn.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThinkTankApprovalDTO {

    @NotBlank(message="id不能为空")
    private @NotBlank(message="id不能为空") String id;
    @NotBlank(message="状态不能为空")
    private @NotBlank(message="状态不能为空") String status;
    private String message;
}
