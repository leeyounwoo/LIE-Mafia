package com.lie.connectionstatus.port;

import com.lie.connectionstatus.domain.user.Authority;
import com.lie.connectionstatus.domain.user.User;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.domain.room.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@RequiredArgsConstructor @Service
public class ConnectionServiceImpl implements ConnectionService{


    private final UserConnectionManager userConnectionManager;
    private final RoomManager roomManager;
    private final RoomRepository roomRepository;


    @Override
    public void createRoom(WebSocketSession session, String username) throws Exception{
        Room room = roomManager.createRoom();

        roomRepository.save(room);
        roomManager.createMediaPipeline(room);

        User newParticipant = new User(username,session.getId(), Authority.LEADER);

        //user에게 pipeline 주고, 시스템에 저장해주기
        room = roomManager.joinRoom(room, newParticipant, session);

        roomRepository.save(room);
    }

    @Override
    public void joinRoom(WebSocketSession session, String username, String roomId) throws Exception{
        Room room = roomRepository.findById(roomId).orElseThrow();
        User newParticipant = new User(username, session.getId(), Authority.PLAYER);

        room = roomManager.joinRoom(room, newParticipant, session);

        roomRepository.save(room);
    }

    @Override
    public void leaveRoom(WebSocketSession session) throws Exception {
        if(userConnectionManager.checkIfUserDoesNotExists(session)){
           log.info("USER doesn't exist. There is no one to leave");
           return;
        }

        UserConnection participant = userConnectionManager.getBySession(session);
        Room room = roomRepository.findById(participant.getRoomId()).orElseThrow();

        if(room.checkIfLeader(participant.getUsername())){
            room = roomManager.leave(participant,room);
            roomManager.close(room);
            roomRepository.delete(room);
            return;
        }

        room = roomManager.leave(participant, room);

        roomRepository.save(room);
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
