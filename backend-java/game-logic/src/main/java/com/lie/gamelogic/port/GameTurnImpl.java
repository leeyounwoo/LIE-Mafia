package com.lie.gamelogic.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.RoomPhase;
import com.lie.gamelogic.dto.GameEndDto;
import com.lie.gamelogic.port.task.*;
import com.lie.gamelogic.util.TimeUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Data
@RequiredArgsConstructor
@Log4j2
public class GameTurnImpl implements GameTurn{

    LocalDateTime endTime; //Phase가 종료 되는 시간
    LocalDateTime startTime; //새로운 페이즈가 시작됨
    private final RoomRepository roomRepository;
    private final GameServiceImpl gameService;
    Room room;
    Integer day;
    String roomId;
    RoomPhase nextPhase;

    HashMap<RoomPhase,Integer> save_time = new HashMap<>();
    List<RoomPhase> roomPhases = new ArrayList<>(Arrays.asList(new RoomPhase[]{RoomPhase.ROLEASSIGN,RoomPhase.MORNING,RoomPhase.MORNINGVOTE,RoomPhase.FINALSPEECH,RoomPhase.EXECUTIONVOTE,RoomPhase.NIGHTVOTE}));

    @Override
    public void setTimer(int time) {
        //페이즈가 end될때의 time
        endTime = TimeUtils.getFinTime(time);
        //새로운 페이즈가 시작될떄 time
        startTime = TimeUtils.getStartTime(time);
    }

    @Override
    public void setnextWork(String roomId, Timer timer) {

        save_time.put(RoomPhase.ROLEASSIGN,15);
        save_time.put(RoomPhase.NIGHTVOTE,30);
        save_time.put(RoomPhase.MORNING,20);
        save_time.put(RoomPhase.MORNINGVOTE,30);
        save_time.put(RoomPhase.FINALSPEECH,10);
        save_time.put(RoomPhase.EXECUTIONVOTE,30);

        this.roomId = roomId;
        room = roomRepository.findById(roomId).orElseThrow();

        if(room.getDay() == null)
            room.setDay(1);
        else
            day = room.getDay();

        if(room.getRoomPhase() == null){
            room.setRoomPhase(RoomPhase.ROLEASSIGN);
        }

        int time = 10;
        //log.info(room);

        this.setTimer(save_time.get(room.getRoomPhase()));
        //RoomPhase를 변환 시켜줌
        if(roomPhases.indexOf(room.getRoomPhase()) == roomPhases.size()-1){
            setNextPhase(roomPhases.get(1));
        }
        else{
            setNextPhase(roomPhases.get(roomPhases.indexOf(room.getRoomPhase())+1));
        }

        roomRepository.save(room);
        //gameService.StartMeesage(room);
        //endTime 사용함
        switch(room.getRoomPhase()){
            case ROLEASSIGN:
                gameService.roleAssign(roomId);
                timer.schedule(new RoleAssignTask(roomRepository,gameService,this),TimeUtils.convertToDate(endTime)); break;
            case NIGHTVOTE:
                gameService.createNightVote(room.getRoomId());
                timer.schedule(new NightVoteTask(roomRepository, this,gameService),TimeUtils.convertToDate(endTime)); break;
            case MORNING:
                timer.schedule(new MorningTask(roomRepository,gameService, this),TimeUtils.convertToDate(endTime)); break;
            case MORNINGVOTE:
                gameService.createMovingVote(room.getRoomId());
                timer.schedule(new MorningVoteTask(roomRepository,gameService,this),TimeUtils.convertToDate(endTime)); break;
            case FINALSPEECH: timer.schedule(new FinalSpeechTask(roomRepository,gameService,this),TimeUtils.convertToDate(endTime)); break;
            case EXECUTIONVOTE:
                gameService.createExecutionVote(room.getRoomId());
                timer.schedule(new ExecutionVoteTask(roomRepository,gameService,this),TimeUtils.convertToDate(endTime)); break;
        }
        //새로운 phase가 시작되면 페이지 변화

        TimerTask timerTask2 = new TimerTask(){
            @Override
            public void run() {
                room = roomRepository.findById(roomId).orElseThrow();
                room.setEndTime(TimeUtils.getFinTime(save_time.get(room.getRoomPhase())));
                roomRepository.save(room);
                gameService.StartMeesage(roomRepository.findById(roomId).orElseThrow());
                new GameTurnImpl(roomRepository,gameService).setnextWork(roomId,timer);
            }
        };


        if(roomRepository.findById(roomId).orElseThrow().getGameResult()==null)
            timer.schedule(timerTask2,TimeUtils.convertToDate(startTime));
        else {
            GameEndDto gameResult = roomRepository.findById(roomId).orElseThrow().getGameResult();
            //log.info(room.getGameResult());
            gameService.GameEndMessage(room,gameResult);
            Room room = roomRepository.findById(roomId).orElseThrow();
            room = room.Gameclear();
            roomRepository.save(room);

            //timert를 종료 시킴
            //timer cancel()시키면 문제 발생
            timer.cancel();
        }
    }

}
