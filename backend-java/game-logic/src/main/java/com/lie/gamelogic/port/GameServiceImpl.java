package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.User;
import com.lie.gamelogic.dto.JoinGameRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

//service 롤백의 개념 transcation 처리
@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final MessageInterface messageInterface;
    private final RoomRepository roomRepository;

    @Override
    public void createGameRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public void joinGameRoom(JoinGameRoomDto joinGameRoomDto) {
        Room room = roomRepository.findById(joinGameRoomDto.getRoomId()).orElseThrow();
        room = room.join(joinGameRoomDto.getUser());
        roomRepository.save(room);

    }

    @Override
    public void leaveGameRoom(String username, String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        room = room.leave(username);
        roomRepository.save(room);

    }

    @Override
    public void closeGameRoom(String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        room.close();
        roomRepository.deleteById(roomId);

    }

    @Override
    public void pressStart(WebSocketSession session, String roomId, String username) throws IOException {
        Room room = roomRepository.findById(roomId).orElseThrow();

        room = room.pressStart(username);

        if(ObjectUtils.isEmpty(room)){
            log.info("Error");
            session.sendMessage(new TextMessage("Start failed"));
            return;
        }

        messageInterface.publishStartEvent("start", roomId);

        roomRepository.save(room);
        //message produce

    }

    @Override
    public void pressReady(WebSocketSession session, String roomId, String username) {
        Room room = roomRepository.findById(roomId).orElseThrow();

        if(!room.checkIfUserExists(username)){
            log.info("User {} doesn't exist in Room {}",username, roomId);
            return;
        }
        if(room.checkIfUserIsLeader(username)){
            log.info("User {} is a leader");
            return;
        }
        room = room.pressReady(username);
        User user = room.getUserByUsername(username);
        messageInterface.publishReadyEvent("ready", user, roomId);
        roomRepository.save(room);
        //produce ready
        return;
    }
}
