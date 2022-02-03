package com.lie.connectionstatus.port;

import com.lie.connectionstatus.domain.room.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public interface ConnectionService {
    void createRoom(WebSocketSession session, String username) throws Exception;

    void joinRoom(WebSocketSession session, String username, String roomId) throws Exception;

    void leaveRoom(WebSocketSession session) throws Exception;

    Room checkIfRoomExists(String roomId);

    Boolean checkIfUsernameExistsInRoom(String roomId, String username);
}
