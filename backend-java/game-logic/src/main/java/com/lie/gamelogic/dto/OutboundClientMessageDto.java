package com.lie.gamelogic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Data
@AllArgsConstructor
public class OutboundClientMessageDto {
    List<String> receivers;
    String message;
    String Status;
    public OutboundClientMessageDto(List<String> recivers, String message){
        this.receivers =recivers;
        this.message = message;
    }



}
