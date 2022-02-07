package com.lie.gamelogic.port.task;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.port.GameService;
import com.lie.gamelogic.port.GameTurnImpl;
import com.lie.gamelogic.port.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.TimerTask;

@Service
@Log4j2
@RequiredArgsConstructor
public class NightVoteTask extends TimerTask {

    private final RoomRepository roomRepository;
    private final GameTurnImpl gameTurn;
    private final GameService gameService;

    Room room;

    @Override
    public void run() {
        room = gameTurn.getRoom();
        room.setDay(room.getDay()+1);
        room.setRoomPhase(gameTurn.getNextPhase());

        //log.info(room);
        roomRepository.save(room);

        log.info(roomRepository.findById(gameTurn.getRoomId()));
    }
}
