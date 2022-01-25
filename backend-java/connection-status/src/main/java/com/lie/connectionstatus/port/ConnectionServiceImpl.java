package com.lie.connectionstatus.port;

import com.lie.connectionstatus.domain.Authority;
import com.lie.connectionstatus.domain.User;
import com.lie.connectionstatus.domain.room.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@RequiredArgsConstructor @Service
public class ConnectionServiceImpl implements ConnectionService{


    private final RoomRepository roomRepository;
    private final KurentoClient kurentoClient;

    @Override
    public void createRoom(WebSocketSession session, String username) {
        Room room = new Room(kurentoClient.createMediaPipeline());
        User newParticipant = new User(username,Authority.LEADER);
        room.join(newParticipant);
        roomRepository.save(room);
    }
}
