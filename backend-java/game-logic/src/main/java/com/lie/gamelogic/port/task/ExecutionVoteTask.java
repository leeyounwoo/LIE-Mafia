package com.lie.gamelogic.port.task;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.port.GameService;
import com.lie.gamelogic.port.GameTurnImpl;
import com.lie.gamelogic.port.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.TimerTask;

@Log4j2
@RequiredArgsConstructor
@Service
public class ExecutionVoteTask extends TimerTask {

    private final RoomRepository roomRepository;
    private final GameService gameService;
    private final GameTurnImpl gameTurn;
    Room room;

    @Override
    public void run() {
        room = gameTurn.getRoom();
        room.setDay(room.getDay());
        room.setRoomPhase(gameTurn.getNextPhase());

        //log.info(room);
        roomRepository.save(room);

        log.info(roomRepository.findById(gameTurn.getRoomId()));
    }
}
