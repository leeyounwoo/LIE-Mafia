package com.lie.gamelogic.port;

<<<<<<< HEAD
import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.User;
import com.lie.gamelogic.domain.UserVote;
import com.lie.gamelogic.domain.Vote;
=======
import com.lie.gamelogic.domain.*;
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
import com.lie.gamelogic.dto.JoinGameRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
<<<<<<< HEAD
import java.util.concurrent.ConcurrentHashMap;
=======
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06

//service 롤백의 개념 transcation 처리
@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final MessageInterface messageInterface;
    private final RoomRepository roomRepository;
<<<<<<< HEAD
    private final VoteRepository voteRepository;

    ConcurrentHashMap<String, Timer> gameTimerByRoomId = new ConcurrentHashMap<>();
=======
    private final ExecutionVoteRepository executionVoteRepository;
    private final VoteRepository voteRepository;
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06

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

        room = room.pressStart(username);

        if(ObjectUtils.isEmpty(room)){
            log.info("Error");
            session.sendMessage(new TextMessage("Start failed"));
            return;
        }

        messageInterface.publishStartEvent("start", roomId);

        roomRepository.save(room);
        //message produce

        gameTimerByRoomId.put(roomId,new Timer());
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
<<<<<<< HEAD
    public void createVote(String roomId) {
        Vote vote=new Vote();

        Room room = roomRepository.findById(roomId).orElseThrow();
        voteRepository.save(vote.createVote(roomId,room.getRoomPhase()));
        vote=vote.createVote(roomId,room.getRoomPhase());
        log.info(vote.toString());
=======
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

>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
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
<<<<<<< HEAD
    public void resultMornigVote(String roomId) {
=======
    public void selectExecutionVote(WebSocketSession session, String roomId, String username, String select, RoomPhase roomPhase, boolean agree) {
        Room room =roomRepository.findById(roomId).orElseThrow();
    //test
        room.setResult("dlrjsxptmxmfmfdnl111111111111111gksdkd");
        roomRepository.save(room);
        //
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
        Room room=roomRepository.findById(roomId).orElseThrow();
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
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
<<<<<<< HEAD
                max=voteResult.get(select);
=======
                max=voteResult.get(select)+1;
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
                username=select;
            }
        };

<<<<<<< HEAD
        Room room=roomRepository.findById(roomId).orElseThrow();
        room.setResult(username);

=======
        room.setResult(username);
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
        roomRepository.save(room);

        log.info(room.toString());
    }

    @Override
<<<<<<< HEAD
    public void deleteVote(String roomId) {
        Vote vote=voteRepository.findById("vote"+roomId).orElseThrow();
        voteRepository.delete(vote);
    }

    @Override
    public void dead(String roomId, String username) {
        //room 정보를 가져옴
        Room room = roomRepository.findById(roomId).orElseThrow();
        //user에서 찾아봄
        //없을시
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

        messageInterface.publishDeadEvent("dead",user,roomId);

=======
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

        //test
        room.setResult("himynameishjhahahohi");
        roomRepository.save(room);
        //test
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
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
        roomRepository.save(room);

    }

    @Override
<<<<<<< HEAD
    public void gameEnd(String roomId) {

    }


=======
    public void deleteVote(String roomId) {
        Vote vote=voteRepository.findById("vote"+roomId).orElseThrow();
        voteRepository.delete(vote);
    }

    @Override
    public void deleteExecutionVote(String roomId) {
        ExecutionVote vote=executionVoteRepository.findById("executionvote"+roomId).orElseThrow();
        executionVoteRepository.delete(vote);
    }
>>>>>>> c5ea1426c1f17c6add44b349dc4cc5b8421e8b06
}
