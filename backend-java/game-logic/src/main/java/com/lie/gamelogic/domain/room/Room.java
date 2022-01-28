package com.lie.gamelogic.domain.room;


import com.lie.gamelogic.domain.User.Job;
import com.lie.gamelogic.domain.User.User;
import lombok.Data;

import java.util.*;

@Data
public class Room {

    private String roomId;

    private HashMap<String, User> participants = new HashMap<>();

    private RoomPhase roomPhase;


    public Room() { this.roomPhase =  RoomPhase.night; }



    public Room MakeJob(){

        Random random = new Random(); //랜덤 객체 생성(디폴트 시드값 : 현재시간)
        random.setSeed(System.currentTimeMillis());

        int participant_number = participants.size(); // 참여한 사람 수

        int Mafia_number = 1;
        int doctor_number = 1;

        if(participant_number >= 6){
            Mafia_number = 2;
        }

        int citizen_number = participant_number - (Mafia_number + doctor_number);

        List<Job> List_Job = new ArrayList<>();

        for(int i=0;i<Mafia_number;i++){
            List_Job.add(Job.Mafia);
        }

        for(int i=0;i<citizen_number;i++){
            List_Job.add(Job.Citizen);
        }

        List_Job.add(Job.Doctor);

        for(int i=0;i<participant_number;i++){
            int position = (int)(Math.random()% List_Job.size());

        }

        return this;
    }



}
