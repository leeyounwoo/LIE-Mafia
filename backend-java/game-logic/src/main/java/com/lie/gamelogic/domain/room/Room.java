package com.lie.gamelogic.domain.room;


import com.lie.gamelogic.domain.Time.TimeUtils;
import com.lie.gamelogic.domain.User.Job;
import com.lie.gamelogic.domain.User.User;
import com.lie.gamelogic.port.VoteService;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.*;

@Data
@RedisHash(value = "room")
public class Room {

    @Id
    @Indexed
    private String roomId;
    private String actionType;
    private LocalDateTime endTime;
    // 방 안에 있는 Day 값
    private int Day;
    //낮(True)인지 밤(False)인지
    private boolean isDay;
    private List<User> Users = new ArrayList<>();
    private RoomPhase roomPhase;

    public Room() {
        this.roomPhase =  RoomPhase.NIGHT;
        this.Day = 0;//첫째날
        this.isDay = false;
        
    }

    public Room ChangePhase(){
        //현재 RoomPhase 정보
        RoomPhase currentPhase = this.roomPhase;

        //기다려주고 phase 바꾸기
        switch (currentPhase){

            case NIGHT: this.roomPhase = RoomPhase.MORNING; this.Day = this.Day++; this.isDay = true; break;
            case MORNING: this.roomPhase = RoomPhase.MORNING_VOTE; break;
            case MORNING_VOTE: this.roomPhase = RoomPhase.LAST_SAY;break;
            case LAST_SAY: this.roomPhase = RoomPhase.LAST_VOTE; break;
            case LAST_VOTE:this.roomPhase = RoomPhase.NIGHT; this.isDay = false; break;
        }

        System.out.println(this.roomPhase);

        return this;
    }
    // 직업 랜덤 배정을 위한 것
    public Room MakeJob(){

        Random random = new Random(); //랜덤 객체 생성(디폴트 시드값 : 현재시간)
        random.setSeed(System.currentTimeMillis());
        int participant_number = Users.size(); // 참여한 사람 수
        System.out.println(participant_number);
        int Mafia_number = 1;
        int doctor_number = 1;
        if(participant_number >= 6){
            Mafia_number = 2;
        }
        int citizen_number = participant_number - (Mafia_number + doctor_number);

        //System.out.println(citizen_number);
        //HashMap으로 중복 처리 
        HashMap<Job,Integer> JobNum = new HashMap<>();
        JobNum.put(Job.MAFIA,Mafia_number);
        JobNum.put(Job.DOCTOR,doctor_number);
        JobNum.put(Job.CITIZEN,citizen_number);


        boolean[] isRole = new boolean[Users.size()];

        JobNum.forEach((Job,num) ->{
            while (num-- > 0) {
                int idx = -1;
                do{
                    idx = (int) (random.nextInt(Users.size() * 100) % Users.size());
                }while(isRole[idx]);

                User player = Users.get(idx);
                isRole[idx] = true;

                player.setJob(Job);

            }
        });

        return this;
    }

}
