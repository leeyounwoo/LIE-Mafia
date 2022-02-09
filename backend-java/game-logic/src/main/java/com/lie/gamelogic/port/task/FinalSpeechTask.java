package com.lie.gamelogic.port.task;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.RoomPhase;
import com.lie.gamelogic.port.GameService;
import com.lie.gamelogic.port.GameTurnImpl;
import com.lie.gamelogic.port.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.TimerTask;

@Log4j2
@Service
@RequiredArgsConstructor
public class FinalSpeechTask extends TimerTask {

    private final RoomRepository roomRepository;
    private final GameService gameService;
    private final GameTurnImpl gameTurn;

    Room room;

    @Override
    public void run() {
        room = gameTurn.getRoom();

        RoomPhase currentPhase = room.getRoomPhase();
        log.info("this Phase is {} and next Phase is {}" ,currentPhase,gameTurn.getNextPhase());

        //room의 페이지를 만드러준다 처리 해준다.
        room.setRoomPhase(gameTurn.getNextPhase());
        //log.info(room);
        roomRepository.save(room);

        //Vote를 만들어준다.
        gameService.createVote(room.getRoomId(),room.getRoomPhase());


    }
}
