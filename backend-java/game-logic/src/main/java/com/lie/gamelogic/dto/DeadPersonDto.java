package com.lie.gamelogic.dto;


import com.lie.gamelogic.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeadPersonDto {
    private String roomId;
    private User user;
}
