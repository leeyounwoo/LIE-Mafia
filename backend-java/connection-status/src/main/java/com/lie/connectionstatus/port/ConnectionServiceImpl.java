package com.lie.connectionstatus.port;

import com.lie.connectionstatus.domain.Authority;
import com.lie.connectionstatus.domain.User;
import com.lie.connectionstatus.domain.room.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor @Service
public class ConnectionServiceImpl implements ConnectionService{


    private final RoomRepository roomRepository;
    private final KurentoClient kurentoClient;

    @Override
    public void createRoom(WebSocketSession session, String username) throws IOException {
        Room room = new Room();
        User newParticipant = new User(username,Authority.LEADER);

        room.join(newParticipant);
        room = roomRepository.save(room);

        //adapter level로 올려주기
        session.sendMessage(new TextMessage(room.getRoomId()));
    }

    @Override
    public void joinRoom(WebSocketSession session, String username, String roomId) {
        User newParticipant = new User(username, Authority.PLAYER);
        Room room = roomRepository.findById(roomId).orElseThrow();

        room = room.join(newParticipant);
        roomRepository.save(room);
    }

    @Override
    public Room checkIfRommExists(String roomId) {
        roomRepository.existsById(roomId);
        return roomRepository.findById(roomId).orElseThrow();
    }

    @Override
    public Boolean checkIfUsernameExistsInRoom(String roomId, String username) {
        Room room = roomRepository.findById(roomId).orElseThrow();

        return room.checkIfUserExists(username);
    }
}
