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
        //현재 페이지 받아옴
        RoomPhase currentPhase = room.getRoomPhase();

        log.info("this Phase is {} and next Phase is {}" ,currentPhase,gameTurn.getNextPhase());

        //결과를 초기화
        room.setGameResult(null);
        roomRepository.save(room);

        //투표 결과를 처리하는 것
        gameService.resultNightVote(room.getRoomId());
        //저장한 결과값을 불려와주어야 한다.
        room = roomRepository.findById(room.getRoomId()).orElseThrow();

        //사망 처리
        gameService.dead(room.getRoomId(),room.getResult());

        //사망 처리값을 불려와주어야 한다.
        room = roomRepository.findById(room.getRoomId()).orElseThrow();

        //날짜를 추가해준다.
        room.setDay(room.getDay()+1);
        room.setRoomPhase(gameTurn.getNextPhase());
        roomRepository.save(room);


        //저녁 결과를 삭제
        gameService.deleteVote(room.getRoomId());

        //종료 체크
        gameService.gameEnd(room.getRoomId());
        room = roomRepository.findById(room.getRoomId()).orElseThrow();

//        if(room.getGameResult().getWinner() != null){
//            return;
//        }

    }
}
