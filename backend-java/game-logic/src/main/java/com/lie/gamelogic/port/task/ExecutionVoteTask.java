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

        //투표 결과를 처리하는 것
        gameService.resultMornigVote(room.getRoomId());
        //저장한 결과값을 불려와주어야 한다.
        room = roomRepository.findById(room.getRoomId()).orElseThrow();

        room.setDay(room.getDay());
        room.setRoomPhase(gameTurn.getNextPhase());

        //log.info(room);

        //투표 결과를 삭제
        gameService.deleteVote(room.getRoomId());
        roomRepository.save(room);

        //이제 밤투표 시작 해주어야 함
        gameService.createVote(room.getRoomId());

        log.info(roomRepository.findById(gameTurn.getRoomId()));
    }
}
