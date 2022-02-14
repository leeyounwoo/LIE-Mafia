package com.lie.connectionstatus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorMessageDto {
    private String service;
    private String status;
}
