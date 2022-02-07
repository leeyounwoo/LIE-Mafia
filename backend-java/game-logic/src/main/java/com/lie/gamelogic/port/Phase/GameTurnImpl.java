package com.lie.gamelogic.port.Phase;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.RoomPhase;
import com.lie.gamelogic.port.GameService;
import com.lie.gamelogic.port.RoomRepository;
import com.lie.gamelogic.util.TimeUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Data
@RequiredArgsConstructor
@Log4j2
public class GameTurnImpl implements GameTurn{

    LocalDateTime endTime; //Phase가 종료 되는 시간
    LocalDateTime startTime; //새로운 페이즈가 시작됨
    private final RoomRepository roomRepository;
    private final GameService gameService;
    Room room;
    int day;
    RoomPhase nextPhase;

    @Override
    public void setTimer(int time) {
        //페이즈가 end될때의 time
        endTime = TimeUtils.getFinTime(time);
        //새로운 페이즈가 시작될떄 time
        startTime = TimeUtils.getStartTime(time);
    }

    @Override
    public void setnextWork(String roomId, Timer timer) {

        //System.out.println(roomRepository.findById(roomId));
        room = roomRepository.findById(roomId).orElseThrow();

        if(room.getDay() == null)
            day = 1;
        else
            day = room.getDay();

        if(room.getRoomPhase() == null){
            room.setRoomPhase(RoomPhase.ROLEASSIGN);
        }

        int time = 10;
        //log.info(room);
        switch(room.getRoomPhase()){
            case NIGHTVOTE: {
                nextPhase = RoomPhase.MORNING;
                time = 90;
            }  break;
            case ROLEASSIGN: nextPhase = RoomPhase.MORNING; time = 15; break;
            case MORNING: nextPhase = RoomPhase.MORNINGVOTE; time = 120; break;
            case MORNINGVOTE: nextPhase = RoomPhase.FINALSPEECH; time= 60; break;
            case FINALSPEECH: nextPhase = RoomPhase.EXECUTIONVOTE; time = 20; break;
            case EXECUTIONVOTE: nextPhase = RoomPhase.NIGHTVOTE; time = 30; break;
        }

        setTimer(time);

        room.setEndTime(endTime);
        room.setDay(day);

        roomRepository.save(room);

        //여기에서 기다리다가 처리해야 할 것을 모두 몰아 버림
        TimerTask timerTask1 = new TimerTask(){
            @Override
            public void run() {
                //페이즈가 ROLEASSIGN인 경우 roleAssign을 적용함
                if(room.getRoomPhase().equals(RoomPhase.ROLEASSIGN)){
                    System.out.println("hello");
                }
                room.setRoomPhase(nextPhase);
                if(room.getRoomPhase().equals(RoomPhase.MORNING) && room.getDay() != 1)
                    room.setDay(room.getDay()+1);
                else
                    room.setDay(room.getDay());

                //log.info(room);
                roomRepository.save(room);

                log.info(roomRepository.findById(roomId));
            }
        };

        //새로운 phase가 시작되면 처리 해야 할것을 넣음
        TimerTask timerTask2 = new TimerTask(){
            @Override
            public void run() {
                new GameTurnImpl(roomRepository,gameService).setnextWork(roomId,timer);
            }
        };

        //endTime이 끝나면
        timer.schedule(timerTask1,TimeUtils.convertToDate(endTime));
        timer.schedule(timerTask2,TimeUtils.convertToDate(startTime));
    }
}
