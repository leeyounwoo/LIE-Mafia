package com.lie.connectionstatus.domain.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.connectionstatus.domain.user.User;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.dto.*;
import com.lie.connectionstatus.port.MessageInterface;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.Continuation;
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
    private final MessageInterface messageInterface;
    private final ObjectMapper objectMapper;

    public Room createRoom(){
        log.info("New Room is creating now");
        Room room = new Room();
        return room;
    }
    public void createMediaPipeline(Room room){
        if(roomsPipeline.containsKey(room.getRoomId())){
            log.debug("Room {} already has pipeline", room.getRoomId());
            return;
        }
        roomsPipeline.put(room.getRoomId(), kurentoClient.createMediaPipeline());
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
    public Room joinRoom(Room room, User participant, WebSocketSession session) throws JsonProcessingException {

        log.info("PARTICIPANT {} : trying to join room {}", participant, room);

        if(checkIfRoomIsNotAvailable(room, participant)){
            return null;
        }

        //username, session 바탕으로 userconnection 만들어주기 mediapipeline 형성
        final UserConnection userConnection =
                new UserConnection(participant.getUsername(), session, room.getRoomId(), roomsPipeline.get(room.getRoomId()));

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

        //이부분 서버에서 지정해준대로 변경? 현재 포맷 유지?

        return room;

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

    public Room leave(UserConnection participant, Room room) throws Exception{
        log.debug("PARTICIPANT {}: Leaving room {}", participant.getUsername(), room.getRoomId());

        //player leave
        room.leave(participant.getUsername());
        ExitParticipantMessageDto exitMessage = new ExitParticipantMessageDto("exitParticipant", participant.getUsername(), participant.getSession().getId());
        messageInterface.broadcastToExistingParticipants(room, objectMapper.writeValueAsString(exitMessage));
        messageInterface.publishEventToKafka("leave", objectMapper.writeValueAsString(exitMessage));
        userConnectionManager.removeBySession(participant.getSession());
        participant.close();
        return room;
    }

    public void close(Room room) throws JsonProcessingException {
        log.debug("ROOM {}: Closing Room", room.getRoomId());

        CloseMessageDto closeMessageDto = new CloseMessageDto("close",room.getRoomId());
        messageInterface.broadcastToExistingParticipants(room, objectMapper.writeValueAsString(closeMessageDto));
        messageInterface.publishEventToKafka("close", objectMapper.writeValueAsString(closeMessageDto));

        room.getParticipants().values().stream()
                .map(user -> userConnectionManager.getUsersBySessionId().get(user.getSessionId()))
                .peek(userConnection -> userConnectionManager.removeBySession(userConnection.getSession()))
                .peek(userConnection -> {
                    try{
                        userConnection.close();
                    }catch (Exception e){

                    }
                })
                .forEach(userConnection -> {
                    try{
                        userConnection.getSession().close();
                    }catch (Exception e){

                    }
                });

        room.close();
        //close Room
        removeRoomPipeline(room);
    }

    public void removeRoomPipeline(Room room){
        //release pipeline
        this.roomsPipeline.get(room.getRoomId()).release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("ROOM {}: Released Pipeline", room.getRoomId());
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("PARTICIPANT {}: Could not release Pipeline", room.getRoomId());
            }
        });

        //remove from list
        this.roomsPipeline.remove(room.getRoomId());

        //room domain =>close 필요
        log.info("Room {} is closed", room.getRoomId());
    }
}
