package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
public class PressReadyResponseDto {
    private String roomId;
    private User user;

}
