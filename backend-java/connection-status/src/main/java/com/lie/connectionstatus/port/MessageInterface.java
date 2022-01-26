package com.lie.connectionstatus.port;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.lie.connectionstatus.domain.UserConnection;
import com.lie.connectionstatus.domain.UserConnectionManager;
import com.lie.connectionstatus.domain.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MessageInterface {
    private final UserConnectionManager userConnectionManager;
    private final ObjectMapper objectMapper;
    public void broadcastToRoom(Room room, String message){
        room.getParticipants().values().stream()
                .map(user -> userConnectionManager.getUsersBySessionId().get(user.getSessionId()))
                .map(userConnection -> userConnection.getSession())
                .forEach(session -> {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
