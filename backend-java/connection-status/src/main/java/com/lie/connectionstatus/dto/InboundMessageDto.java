package com.lie.connectionstatus.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InboundMessageDto {
    private String id;
    private String data;
    private String sender;
}
