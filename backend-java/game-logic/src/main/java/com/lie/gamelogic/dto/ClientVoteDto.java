package com.lie.gamelogic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientVoteDto {

    String id;
    String roomId;
    String username;
    String select;
}
