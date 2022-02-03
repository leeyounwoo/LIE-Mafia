package com.lie.connectionstatus.domain.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.User;
import com.lie.connectionstatus.domain.UserConnection;
import com.lie.connectionstatus.domain.UserConnectionManager;
import com.lie.connectionstatus.dto.ExistingParticipantMessageDto;
import com.lie.connectionstatus.dto.NewParticipantMessageDto;
import com.lie.connectionstatus.port.MessageInterface;
import com.lie.connectionstatus.port.RoomRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class RoomManager {
    private final KurentoClient kurentoClient;
    private final ConcurrentMap<String, MediaPipeline> roomsPipeline = new ConcurrentHashMap<>();
    private final UserConnectionManager userConnectionManager;
    private final RoomRepository roomRepository;
    private final MessageInterface messageInterface;
    private final ObjectMapper objectMapper;

    public Room createRoom(){
        log.info("New Room is creating now");
        Room room = new Room();;
        room = roomRepository.save(room);
        createMediaPipeline(room.getRoomId());
        return room;
    }
    public void createMediaPipeline(String roomId){
        if(roomsPipeline.containsKey(roomId)){
            log.debug("Room {} already has pipeline", roomId);
            return;
        }
        roomsPipeline.put(roomId, kurentoClient.createMediaPipeline());
    }
    public Boolean checkIfRoomIsNotAvailable(Room room, User participant){
        if(checkIfUsernameExists(participant.getUsername())){
            log.info("USERNAME ALREADY EXISTS");
            //optional이던 오류 내기
            return true;
        }
        if(room.checkIfFull()){
            log.info("Room is already Full");
            return true;
        }
        return false;
    }
    public UserConnection joinRoom(String roomId, User participant, WebSocketSession session) throws JsonProcessingException {
        log.debug("Looking for Room {}", roomId);
        Room room = roomRepository.findById(roomId).orElseThrow();
        log.info("PARTICIPANT {} : trying to join room {}", participant, roomId);

        if(checkIfRoomIsNotAvailable(room, participant)){
            return null;
        }

        //username, session 바탕으로 userconnection 만들어주기 mediapipeline 형성
        final UserConnection userConnection =
                new UserConnection(participant.getUsername(), session, roomId, roomsPipeline.get(roomId));

        //connection 만들어진 것 저장해주기
        userConnectionManager.connectUser(userConnection);

        //이거 여기서 빼야함
        ExistingParticipantMessageDto existingParticipantsMessage = new ExistingParticipantMessageDto("existingParticipants",participant,room);
        messageInterface.broadcastToNewParticipants(userConnection,objectMapper.writeValueAsString(existingParticipantsMessage));

        //이거 여기서 빼야함
        NewParticipantMessageDto newParticipantMessage = new NewParticipantMessageDto("newParticipant", participant);
        messageInterface.broadcastToExistingParticipants(room, objectMapper.writeValueAsString(newParticipantMessage));

        //room안에 join 할 수 있는지 없는지 조건 체크 안에서하기
        room = room.join(participant);

        roomRepository.save(room);

        //이부분 서버에서 지정해준대로 변경? 현재 포맷 유지?

        return userConnection;

    }
    public Boolean checkIfUsernameExists(String username){
        if(ObjectUtils.isEmpty(userConnectionManager.getByUsername(username))){
            return false;
        }
        return true;
    }
    public MediaPipeline getRoomsMediaPipeline(String roomId){
        if(roomsPipeline.containsKey(roomId)){
            log.debug("Room {} pipeline found", roomId);
            return roomsPipeline.get(roomId);
        }
        log.info("Room {} pipeline not found ", roomId);
        return roomsPipeline.get(roomId);
    }

    public void removeRoomPipeline(Room room){
        this.roomsPipeline.remove(room.getRoomId());
        //room domain =>close 필요
        log.info("Room {} is closed", room.getRoomId());
    }
}
