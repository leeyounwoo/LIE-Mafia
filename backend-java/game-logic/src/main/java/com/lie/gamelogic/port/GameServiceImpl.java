package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.*;
import com.lie.gamelogic.dto.GameEndDto;
import com.lie.gamelogic.dto.JoinGameRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//service 롤백의 개념 transcation 처리
@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final MessageInterface messageInterface;
    private final RoomRepository roomRepository;
    private final VoteRepository voteRepository;
    private final ExecutionVoteRepository executionVoteRepository;

    ConcurrentHashMap<String, Timer> gameTimerByRoomId = new ConcurrentHashMap<>();

    @Override
    public void createGameRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public void joinGameRoom(JoinGameRoomDto joinGameRoomDto) {
        Room room = roomRepository.findById(joinGameRoomDto.getRoomId()).orElseThrow();
        room = room.join(joinGameRoomDto.getUser());
        roomRepository.save(room);

    }

    @Override
    public void leaveGameRoom(String username, String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        room = room.leave(username);
        roomRepository.save(room);

    }

    @Override
    public void closeGameRoom(String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        room.close();
        roomRepository.deleteById(roomId);

    }

    @Override
    public void pressStart(WebSocketSession session, String roomId, String username) throws IOException {
        Room room = roomRepository.findById(roomId).orElseThrow();

        //test용으로
        room.setRoomStatus(RoomStatus.WAITING);

        //시작 상태면 예외 처리
        if(room.getRoomStatus() == RoomStatus.START) {
            log.info("Error");
            session.sendMessage(new TextMessage("Already Started!!!"));
            return;
        }
        room = room.pressStart(username);

        if(ObjectUtils.isEmpty(room)){
            log.info("Error");
            session.sendMessage(new TextMessage("Start failed"));
            return;
        }

        messageInterface.publishStartEvent("start", roomId);

        roomRepository.save(room);
        //message produce

        if(gameTimerByRoomId.get(roomId) == null)
            gameTimerByRoomId.put(roomId,new Timer());
        else {
            gameTimerByRoomId.remove(roomId,gameTimerByRoomId.get(roomId));
            gameTimerByRoomId.put(roomId,new Timer());
        }
        GameTurn gameTurn = new GameTurnImpl(roomRepository,this);
        gameTurn.setnextWork(roomId,gameTimerByRoomId.get(roomId));

    }

    @Override
    public void pressReady(WebSocketSession session, String roomId, String username) {
        Room room = roomRepository.findById(roomId).orElseThrow();

        if(!room.checkIfUserExists(username)){
            log.info("User {} doesn't exist in Room {}",username, roomId);
            return;
        }
        if(room.checkIfUserIsLeader(username)){
            log.info("User {} is a leader");
            return;
        }
        room = room.pressReady(username);
        User user = room.getUserByUsername(username);
        messageInterface.publishReadyEvent("ready", user, roomId);
        roomRepository.save(room);
        //produce ready
        return;
    }

    @Override
    public void roleAssign(String roomId) {
        Room room=roomRepository.findById(roomId).orElseThrow();
        room=room.initStartGame(); //alive true, 직업배정
        roomRepository.save(room);
    }

    @Override
    public void createVote(String roomId, RoomPhase phase) {

        switch (phase){
            case EXECUTIONVOTE :
                ExecutionVote executionVote=new ExecutionVote();
                executionVoteRepository.save(executionVote.createVote(roomId,phase));
                log.info(executionVote.toString());
                break;
            default:
                Vote vote=new Vote();
                voteRepository.save(vote.createVote(roomId,phase));
                vote=vote.createVote(roomId,phase);
                log.info(vote.toString());
                break;
        }

    }

    @Override
    public void selectVote(WebSocketSession session, String roomId, String username, String select) {
        Room room =roomRepository.findById(roomId).orElseThrow();
        User user=room.getUserByUsername(username);
        if(!user.getAlive()){ //살아있는 user만 select
            log.info("User {} died in Room {}", username, roomId);
            return;
        }

        Vote vote=voteRepository.findById("vote"+roomId).orElseThrow();
        UserVote userVote=new UserVote(username,user.getSessionId(),user.getJob(),select);

        vote.putUserVote(username,userVote);
        voteRepository.save(vote);

        log.info(vote.toString());
    }

    @Override
    public void selectExecutionVote(WebSocketSession session, String roomId, String username, String select, RoomPhase roomPhase, boolean agree) {
        Room room =roomRepository.findById(roomId).orElseThrow();

        User user=room.getUserByUsername(username);
        if(!user.getAlive()){ //살아있는 user만 select
            log.info("User {} died in Room {}", username, roomId);
            return;
        }

        if(username.equals(select)){ //선택받은 사용자가 투표시 return
            log.info("User {} select user in Room {}", username, roomId);
            return;
        }

        if(!room.getResult().equals(select)){ //의심자가 아닌 사용자를 선택한 경우 return
            log.info("User {} is not selected user in Room {}", username, roomId);
            return;
        }

        ExecutionVote vote=executionVoteRepository.findById("executionvote"+roomId).orElseThrow();

        UserExecutionVote userExecutionVote=vote.getVotes().get(username);
        if(userExecutionVote==null){
            userExecutionVote=new UserExecutionVote(username,user.getSessionId(),select,agree,false);
            vote.putUserVote(username,userExecutionVote);
        }

        vote=vote.pressVoted(username,agree);
        executionVoteRepository.save(vote);

        log.info(vote.toString());
    }

    @Override
    public void resultMornigVote(String roomId) {
        Vote vote=voteRepository.findById("vote"+roomId).orElseThrow();
        List<String> list= new ArrayList(vote.selectList()); //투표 내용 가져오기
        Map<String,Integer> voteResult=new HashMap<>();
        log.info(list.toString());

        String username="";
        int max=1;
        for(String select:list){
            if(!voteResult.containsKey(select)){
                voteResult.put(select,1);
            }else{
                voteResult.put(select,voteResult.get(select)+1);
            }

            if(voteResult.get(select)+1>=max){
                max=voteResult.get(select);
                username=select;
            }
        };

        Room room=roomRepository.findById(roomId).orElseThrow();
        room.setResult(username);

        roomRepository.save(room);

        log.info(room.toString());
    }

    @Override
    public void deleteVote(String roomId) {
        Vote vote=voteRepository.findById("vote"+roomId).orElseThrow();
        voteRepository.delete(vote);
    }

    @Override
    public void resultExecutionVote(String roomId) {
        ExecutionVote vote=executionVoteRepository.findById("executionvote"+roomId).orElseThrow();
        Room room=roomRepository.findById(roomId).orElseThrow();
        if(vote.getAgreeDie()*2<=vote.getVotes().size()){
            room.setResult(null);
        }
        roomRepository.save(room);
    }

    @Override
    public void resultNightVote(String roomId) {
        Room room=roomRepository.findById(roomId).orElseThrow();
        Vote vote=voteRepository.findById("vote"+roomId).orElseThrow();

        HashMap<String,UserVote> voteMap=vote.getVotes();
        Set<String> mafiaSelect=new HashSet<>();
        String doctorSelect="";

        for (UserVote jobVote: voteMap.values()){
            if(jobVote.getJob().equals(Job.MAFIA)){
                mafiaSelect.add(jobVote.getSelect());
            }else if(jobVote.getJob().equals(Job.DOCTOR)){
                doctorSelect=jobVote.getSelect();
            }
        }

        if (mafiaSelect.size()==1){ //죽일사람
            for(String selectName:mafiaSelect){
                if(!selectName.equals(doctorSelect)){
                    room.setResult(selectName);
                    roomRepository.save(room);
                    log.info(room.getResult());
                    return;
                }
            }

        } //사망자는 마피아가 죽일사람을 선택했을때, 의사가 못살리면 세팅

        room.setResult(null); //그 외는 죽은 사람이 없다.
        log.info(room.getResult());
        roomRepository.save(room);

    }

    @Override
    public void deleteExecutionVote(String roomId) {
        ExecutionVote vote=executionVoteRepository.findById("executionvote"+roomId).orElseThrow();
        executionVoteRepository.delete(vote);
    }

    @Override
    public void dead(String roomId, String username) {
        //room 정보를 가져옴
        Room room = roomRepository.findById(roomId).orElseThrow();

        //null 일시
        if(username == null){
            log.info("no One Dead");
            return;
        }
        //방안에 없을시
        if(!room.checkIfUserExists(username)){
            log.info("User {} doesn't exist in Room {}",username, roomId);
            return;
        }
        User user = room.getParticipants().get(username);
        //이미 죽어 있을 시
        if(!user.getAlive()){
            log.info("User {} is already dead ", username);
            return;
        }
        user.setAlive(false);

        log.info("User {} is dead ", username);
        messageInterface.publishDeadEvent("dead",user,roomId);
        roomRepository.save(room);

        //gameEnd(roomId);
    }

    @Override
    public void gameEnd(String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        Integer citizenCount = 0;
        Integer mapiaCount =0;
        //시민 리스트
        List<String> citizenList = new ArrayList<>();
        //마피아 리스트
        List<String> mapiaList = new ArrayList<>();
        //이긴 직업
        Job Winner = null;
        //진 직업
        Job Loser = null;
        GameEndDto gameEndDto = null;
        Set set = room.getParticipants().keySet();
        Iterator iterator = set.iterator();

        while(iterator.hasNext()){
            User user = room.getParticipants().get(iterator.next());
            //살아 있는 경우에 사용 해줌
            Job job = user.getJob();
            if (job.equals(Job.CITIZEN)) {
                citizenList.add(user.getUsername());
                if(user.getAlive())
                    citizenCount++;
            }
            else if (job.equals(Job.DOCTOR)){
                citizenList.add(user.getUsername());
                if(user.getAlive())
                    citizenCount++;
            }
            else {
                mapiaList.add(user.getUsername());
                if(user.getAlive())
                    mapiaCount++;
            }
        }
        //마피아가 한명도 없을 때
        if(mapiaCount == 0) {
            //log.info("Citizen wins winner is : {} ", citizenList);
            Winner = Job.CITIZEN;
            Loser = Job.MAFIA;
            gameEndDto = new GameEndDto(Winner,Loser,citizenList,mapiaList);
        }
        else if(mapiaCount >= citizenCount){ // 마피아가 시민 수보다 많을 때
            //log.info("Mapia wins winner is : {} " , mapiaList);
            Winner = Job.MAFIA;
            Loser = Job.CITIZEN;
            gameEndDto = new GameEndDto(Winner,Loser,mapiaList,citizenList);
        }

        else{
            log.info("Game is not end");
            return;
        }

        //topic을 end로 만듦
        messageInterface.publishGameEndEvent("end",gameEndDto);
        room.setGameResult(gameEndDto);
        //그리고 room 정보를 변경해주고 이를 저장해줌
        roomRepository.save(room);
    }


}
