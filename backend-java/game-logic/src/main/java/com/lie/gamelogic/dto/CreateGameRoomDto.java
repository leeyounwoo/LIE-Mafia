package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.RoomPhase;
import com.lie.gamelogic.domain.RoomStatus;
import com.lie.gamelogic.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
public class CreateGameRoomDto {

    private String id;
    private Room room;
    private WebSocketSession session;


}
