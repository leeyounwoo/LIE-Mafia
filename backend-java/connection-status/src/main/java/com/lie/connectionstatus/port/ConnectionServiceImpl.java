package com.lie.connectionstatus.port;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.user.Authority;
import com.lie.connectionstatus.domain.user.User;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.domain.room.RoomManager;
import com.lie.connectionstatus.dto.CreateEventDto;
import com.lie.connectionstatus.dto.JoinEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@RequiredArgsConstructor @Service
public class ConnectionServiceImpl implements ConnectionService{
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserConnectionManager userConnectionManager;
    private final RoomManager roomManager;
    private final RoomRepository roomRepository;
    private final MessageInterface messageInterface;
    private final ObjectMapper objectMapper;

    @Override
    public void createRoom(WebSocketSession session, String senderSession, String username) throws Exception{
        //닉네임 랜덤 배정 시 checkUsername 지워도됨
        if(!roomManager.checkIfUsernameExists(username)){
            Room room = roomManager.createRoom();

            roomRepository.save(room);
            roomManager.createMediaPipeline(room);

            User newParticipant = new User(username,senderSession, Authority.LEADER);

            //user에게 pipeline 주고, 시스템에 저장해주기
            room = roomManager.joinRoom(room, newParticipant, session);

            room = roomRepository.save(room);

            CreateEventDto createEventDto = new CreateEventDto("create", room);
            messageInterface.publishEventToKafka("create",objectMapper.writeValueAsString(createEventDto));
            return;
        }
        throw new Exception();
    }

    @Override
    public void joinRoom(WebSocketSession session, String sederSession, String username, String roomId) throws Exception{
        Room room = roomRepository.findById(roomId).orElseThrow();
        User newParticipant = new User(username, sederSession, Authority.PLAYER);

        room = roomManager.joinRoom(room, newParticipant, session);

        room = roomRepository.save(room);
        JoinEventDto joinEventDto = new JoinEventDto("join", room.getRoomId(), newParticipant);
        messageInterface.publishEventToKafka("join", objectMapper.writeValueAsString(joinEventDto));
    }

    @Override
    public void leaveRoom(WebSocketSession interfaceSession, String senderSession) throws Exception {
        if(userConnectionManager.checkIfUserDoesNotExists(senderSession)){
           log.info("USER doesn't exist. There is no one to leave");
           return;
        }

        UserConnection participant = userConnectionManager.getBySession(senderSession);
        Room room = roomRepository.findById(participant.getRoomId()).orElseThrow();

        if(room.checkIfLeader(participant.getUsername())){
            room = roomManager.leave(interfaceSession, participant,room);
            roomManager.close(interfaceSession, room);
            roomRepository.delete(room);
            return;
        }

        room = roomManager.leave(interfaceSession, participant, room);

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
