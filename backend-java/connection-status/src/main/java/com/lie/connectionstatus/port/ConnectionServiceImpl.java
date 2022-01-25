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


        Boolean bool = roomRepository.existsById(room.getId());
        session.sendMessage(new TextMessage(room.getId()+" "+bool));
    }

    @Override
    public Room checkIfRommExists(String roomId) {
        log.info("1");
        roomRepository.existsById(roomId);

        log.info("2");
        return roomRepository.findById(roomId).orElseThrow();
    }

    @Override
    public Boolean checkIfUsernameExistsInRoom(String roomId, String username) {
        Room room = roomRepository.findById(roomId).get();

        if(ObjectUtils.isEmpty(room)){
            return false;
        }

        return room.checkIfUserExists(username);
    }
}
