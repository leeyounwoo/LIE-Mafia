package com.lie.connectionstatus.domain.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.Authority;
import com.lie.connectionstatus.domain.User;
import com.lie.connectionstatus.domain.UserConnection;
import com.lie.connectionstatus.domain.UserConnectionManager;
import com.lie.connectionstatus.dto.ServerMessageDto;
import com.lie.connectionstatus.port.MessageInterface;
import com.lie.connectionstatus.port.RoomRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.springframework.stereotype.Component;
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
    public UserConnection joinRoom(String roomId, User participant, WebSocketSession session) throws JsonProcessingException {
        log.debug("Looking for Room {}", roomId);
        Room room = roomRepository.findById(roomId).orElseThrow();
        log.info("PARTICIPANT {} : trying to join room {}", participant, roomId);


        //room안에 join 할 수 있는지 없는지 조건 체크 안에서하기
        room = room.join(participant);

        roomRepository.save(room);

        final UserConnection userConnection =
                new UserConnection(participant.getUsername(), session, roomId, roomsPipeline.get(roomId));


        userConnectionManager.connectUser(userConnection);

        //이부분 서버에서 지정해준대로 변경? 현재 포맷 유지?
        ServerMessageDto serverMessageDto = new ServerMessageDto("existingParticipants",room);
        messageInterface.broadcastToRoom(room,objectMapper.writeValueAsString(serverMessageDto));

        return userConnection;

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
