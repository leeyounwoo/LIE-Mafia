package com.lie.gamelogic.domain;

import com.lie.gamelogic.dto.GameEndDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@Data
@RedisHash(value = "room")
public class Room {
    @Id
    String roomId;
    HashMap<String,User> participants = new HashMap<>();
    RoomStatus roomStatus;
    RoomPhase roomPhase;
    Integer day;
    LocalDateTime endTime;
    String result;
    GameEndDto gameResult;

    public Room join(User user){
        this.participants.put(user.getUsername(), user);
        return this;
    }
    public Room leave(String username){
        this.participants.remove(username);
        return this;
    }

    public void close(){
        log.info("Room {} : closing", this.getRoomId());
        this.participants.clear();
    }
    public Room pressStart(String username){

        if(!checkIfUserIsLeader(username)){
            return null;
        }
        if(participants.size()<4){
            return null;
        }
        for(User participant : participants.values()){
            if(participant.getUsername().equals(username)){
                continue;
            }
            if(!participant.getReady()){
                return null;
            }
        }

        this.roomStatus = RoomStatus.START;

        //start 후 로직은 추가적으로 짜주세요
        participants.forEach((player,user)->{
            user.setAlive(true);
        });

        this.roomPhase = RoomPhase.ROLEASSIGN;
        this.day = 1;

        return this;
    }
    public Room pressReady(String username){
        User user = this.participants.get(username);
        user.pressReady();
        this.participants.put(username, user);
        return this;
    }

    public Boolean checkIfUserIsLeader(String username){
        if(participants.get(username).getAuthority().equals(Authority.LEADER)){
            return true;
        }
        return false;
    }
    //방 id 내에 username 이 존재하는가
    public Boolean checkIfUserExists(String username){
        if(participants.containsKey(username)){
            return true;
        }
        return false;
    }
    public User getUserByUsername(String username){
        if(checkIfUserExists(username)){
            return this.participants.get(username);
        }
        return null;
    }

    public Room initStartGame() {
        //직업배정
        List<String> players=new ArrayList<>(participants.keySet());

        Random random = new Random(); //랜덤 객체 생성(디폴트 시드값 : 현재시간)
        random.setSeed(System.currentTimeMillis());
        int participant_number = participants.size(); // 참여한 사람 수

        int Mafia_number = 1;
        int doctor_number = 1;
        if(participant_number >= 6){
            Mafia_number = 2;
        }
        int citizen_number = participant_number - (Mafia_number + doctor_number);

        //HashMap으로 중복 처리
        HashMap<Job,Integer> JobNum = new HashMap<>();
        JobNum.put(Job.MAFIA,Mafia_number);
        JobNum.put(Job.DOCTOR,doctor_number);
        JobNum.put(Job.CITIZEN,citizen_number);


        boolean[] isRole = new boolean[participants.size()];

        JobNum.forEach((Job,num) ->{
            while (num-- > 0) {
                int idx = -1;
                do{
                    idx = (int) (random.nextInt(participants.size() * 100) % participants.size());
                }while(isRole[idx]);

                User player = participants.get(players.get(idx));
                isRole[idx] = true;

                player.setJob(Job);
            }
        });


        return this;
    }
    //방 자체 초기화가 아닌 game초기화
    public Room Gameclear(){

        this.roomPhase = RoomPhase.MORNING;
        this.roomStatus = RoomStatus.WAITING;
        this.day = 0;
        this.setResult(null);
        this.getGameResult().setResult(null);
        this.setGameResult(null);

        this.participants.forEach((player,user)->{
            user.setAlive(false);
            user.setReady(false);
            user.setJob(null);
        });

        return this;
    }
}
