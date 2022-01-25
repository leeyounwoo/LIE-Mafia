package com.lie.connectionstatus.port;

import com.lie.connectionstatus.domain.room.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public interface ConnectionService {
    public void createRoom(WebSocketSession session, String username) throws IOException;

    public Room checkIfRommExists(String roomId);

    public Boolean checkIfUsernameExistsInRoom(String roomId, String username);
}
