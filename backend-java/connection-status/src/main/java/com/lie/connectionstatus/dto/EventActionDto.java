package com.lie.connectionstatus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class EventActionDto {
    String eventType;
    String id;

    public String createTopic(){
        return eventType+"."+id;
    }
}
