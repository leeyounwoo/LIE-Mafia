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
@RequiredArgsConstructor
@Log4j2
public class MorningTask extends TimerTask {

    private final RoomRepository roomRepository;
    private final GameService gameService;
    private final GameTurnImpl gameTurn;

    Room room;

    @Override
    public void run() {
        //밤이 끝나면 처리 하도록 넣어줌

        room = gameTurn.getRoom();
        //날짜 변환
        room.setDay(room.getDay());
        //페이즈 변환
        room.setRoomPhase(gameTurn.getNextPhase());
        //log.info(room);
        roomRepository.save(room);

        //vote를 만들어준다.
        gameService.createVote(room.getRoomId());
        log.info(roomRepository.findById(gameTurn.getRoomId()));
    }
}
