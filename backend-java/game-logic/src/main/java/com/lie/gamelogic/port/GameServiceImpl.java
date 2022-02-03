package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.Time.TimeUtils;
import com.lie.gamelogic.domain.User.Job;
import com.lie.gamelogic.domain.User.User;
import com.lie.gamelogic.domain.Vote.Vote;
import com.lie.gamelogic.domain.room.Room;
import com.lie.gamelogic.dto.Client.VoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService{

    private final RoomRepository roomRepository;

    //ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);

    //Ready 상태 변환 코드
    @Override
    public void GameReady(String roomId, String username) {

        Room room = roomRepository.findByRoomId(roomId);
        List<User> users = room.getUsers();

        for(User user : users){
            if(user.getUsername().equals(username)){
                boolean user_ready = user.isReady();
                user.setReady(!user_ready);
            }
        }
    }

    // 준비되어 있는지 확인 하는 코드
    @Override
    public boolean allReady(String roomId) {

        int count = 0;
        Room room = roomRepository.findByRoomId(roomId);
        List<User> users = room.getUsers();

        int size = users.size();

        for(User user : users){
            if(user.isReady())
                count++;
        }

        if(count == size) return true;

        return false;
    }

    @Override
    public void GameStart(WebSocketSession session, String roomId) {


        if(!allReady(roomId)) return;

        //test 코드
//        Room room = new Room();
//
//        List<User> users = new ArrayList<>();
//
//        users.add(new User("test1"));
//        users.add(new User("test2"));
//        users.add(new User("test3"));
//        users.add(new User("test4"));
//
//        room.setUsers(users);
//
//        System.out.println(users);

        //redis pub/sub을 이용해서 room 정보를 가져옴
//        List<ClientMessageDto> clients = RedisMessageDtoSubscriber.clientDTOS;
//        //room 정보 mapping 필요
//        for(int i=0;i<clients.size();i++){
//            if(clients.get(i).getRoomId() == roomId){
//
//            }
//        }

//        room.setRoomId(roomId);
//        room = GetJobs(session,room);
//
//        System.out.println(room);
//
//        roomRepository.save(room);

    }

    @Override
    public Room findRoom(String roomId) {
        return roomRepository.findByRoomId(roomId);
    }

    @Override
    public Room GetJobs(WebSocketSession session,Room room){

        room.MakeJob();
        return room;
    }

    @Override
    public Room phaseTurn(String roomId) {

        Room room = findRoom(roomId);
        room.setEndTime(TimeUtils.getFinTime(20));

        if(GameEnd(roomId)) return room;

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                room.ChangePhase();
                log.info(room);
                roomRepository.save(room);
            }
        };

        //120초 이후에 상태 변화 진행
        timer.schedule(timerTask, TimeUtils.convertToDate(room.getEndTime()));

        return room;
    }

    @Override
    public String executionVote(String roomId, String select, boolean isDead) {

        log.info(select);

        List<User> users = find_alive_one(roomId);

        int [] want_kill = new int [users.size()];

        if(isDead){
            int x = users.indexOf(select);
            want_kill[x]++;
        }

        int max = -1;
        int max_position= -1;
        for(int i=0;i<want_kill.length;i++){
            if(max <want_kill[i]){
                max = want_kill[i];
                max_position = i;
            }
        }

        User user = users.get(max_position);
        user.setDead(true);
        phaseTurn(roomId);

        return user.getUsername();

    }

    @Override
    public boolean GameEnd(String roomId) {

        Room room = findRoom(roomId);
        List<User> participants = room.getUsers();
        int citizenCount = 0;//시민 숫자
        int mapiaCount =0; //마피아 숫자
        for(User user : participants){
            Job job = user.getJob();
            if(job.equals(Job.CITIZEN)) citizenCount++;
            else if(job.equals(Job.DOCTOR)) citizenCount++;
            else mapiaCount++;
        }

        if(mapiaCount >= citizenCount) return true;

        return false;
    }

    @Override
    public String findVote(VoteDto vote) {

        if(vote.getActionType().equals("citizenVote"))
            return killByCitizen(vote.getRoomId(), vote.getSelect());
        else {
            //밤 투표 일때는 직업 별로 차이가 있음
            //마피아에 의해서 사망할 친구
            String KillByMafia = null;
            //의사에 의해서 생존할 친구
            String Saver = null;
            if(vote.getJob().equals(Job.MAFIA))
                KillByMafia = killByMafia(vote.getRoomId(),vote.getSelect());
            if(vote.getJob().equals(Job.DOCTOR))
                Saver = SaveByDoctor(vote.getRoomId(),vote.getSelect());

            if(KillByMafia == null)
                return null;
            else if(KillByMafia.equals(Saver))
                return null;


            return KillByMafia;

        }

    }
    //의사에 의해서 생존
    private String SaveByDoctor(String roomId, String select) {
        return null;
    }

    //마피아에 의해서 죽는 사람
    private String killByMafia(String roomId, String select) {
        return null;
    }

    //시민에 의해서 죽음
    private String killByCitizen(String roomId, String select) {
        return null;
    }

    public List<User> find_alive_one(String roomId){

        Room room = roomRepository.findByRoomId(roomId);
        List <User> users = room.getUsers();
        List <User> alive = new ArrayList<>();
        for(User user : users){
            if(!user.isDead())
                alive.add(user);
        }

        return alive;
    }



}
