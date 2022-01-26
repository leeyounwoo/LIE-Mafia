package com.lie.connectionstatus.port;

import com.lie.connectionstatus.domain.Authority;
import com.lie.connectionstatus.domain.User;
import com.lie.connectionstatus.domain.UserConnection;
import com.lie.connectionstatus.domain.UserConnectionManager;
import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.domain.room.RoomManager;
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
    private final UserConnectionManager userConnectionManager;
    private final RoomManager roomManager;
    private final RoomRepository roomRepository;


    @Override
    public void createRoom(WebSocketSession session, String username) throws Exception{
        Room room = roomManager.createRoom();
        User newParticipant = new User(username,session.getId(), Authority.LEADER);

        //user에게 pipeline 주고, 시스템에 저장해주기
        UserConnection userConnection = roomManager.joinRoom(room.getRoomId(), newParticipant, session);

        log.info(userConnection.toString());
    }

    @Override
    public void joinRoom(WebSocketSession session, String username, String roomId) throws Exception{
        User newParticipant = new User(username, session.getId(), Authority.PLAYER);

        log.info(roomId);

        UserConnection userConnection = roomManager.joinRoom(roomId, newParticipant, session);


        log.info(userConnection.toString());
    }

    @Override
    public Room checkIfRoomExists(String roomId) {
        roomRepository.existsById(roomId);
        return roomRepository.findById(roomId).orElseThrow();
    }

    @Override
    public Boolean checkIfUsernameExistsInRoom(String roomId, String username) {
        Room room = roomRepository.findById(roomId).orElseThrow();

        return room.checkIfUserExists(username);
    }
}
