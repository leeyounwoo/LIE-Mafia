package com.lie.gamelogic.port.task;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.port.GameService;
import com.lie.gamelogic.port.GameServiceImpl;
import com.lie.gamelogic.port.GameTurnImpl;
import com.lie.gamelogic.port.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.TimerTask;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoleAssignTask extends TimerTask {

    private final RoomRepository roomRepository;
    private final GameServiceImpl gameService;
    private final GameTurnImpl gameTurn;

    @Override
    public void run() {
        Room room;

        room = roomRepository.findById(gameTurn.getRoomId()).orElseThrow();
        room.setRoomPhase(gameTurn.getNextPhase());
        roomRepository.save(room);

        log.info(roomRepository.findById(gameTurn.getRoomId()));
    }
}
