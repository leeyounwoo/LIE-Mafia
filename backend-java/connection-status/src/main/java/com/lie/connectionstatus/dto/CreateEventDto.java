package com.lie.connectionstatus.dto;

import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
public class CreateEventDto {
    private String id;
    private Room room;
}
