package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.dto.JoinGameRoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//service 롤백의 개념 transcation 처리
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final RoomRepository roomRepository;

    @Override
    public void createGameRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public void joinGameRoom(JoinGameRoomDto joinGameRoomDto) {
        Room room = roomRepository.findById(joinGameRoomDto.getRoomId()).orElseThrow();
        room = room.userSave(joinGameRoomDto.getUser());
        roomRepository.save(room);

    }
}
