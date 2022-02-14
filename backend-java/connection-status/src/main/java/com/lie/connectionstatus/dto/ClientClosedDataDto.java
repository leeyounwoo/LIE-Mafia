package com.lie.connectionstatus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientClosedDataDto {
    private String id;
    private String sessionId;
}
