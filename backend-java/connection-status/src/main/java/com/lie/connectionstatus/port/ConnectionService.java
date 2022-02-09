package com.lie.connectionstatus.port;

import com.lie.connectionstatus.domain.room.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public interface ConnectionService {
    void createRoom(WebSocketSession interfaceSession, String senderSession, String username) throws Exception;

    void joinRoom(WebSocketSession interfaceSession, String senderSession, String username, String roomId) throws Exception;

    void leaveRoom(WebSocketSession session, String senderSession) throws Exception;

    Room checkIfRoomExists(String roomId);

    Boolean checkIfUsernameExistsInRoom(String roomId, String username);
}
