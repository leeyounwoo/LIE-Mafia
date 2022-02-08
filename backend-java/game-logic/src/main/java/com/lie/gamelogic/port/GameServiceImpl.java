package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.*;
import com.lie.gamelogic.dto.JoinGameRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

//service 롤백의 개념 transcation 처리
@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final MessageInterface messageInterface;
    private final RoomRepository roomRepository;
    private final ExecutionVoteRepository executionVoteRepository;
    private final VoteRepository voteRepository;

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

        room.setResult("dlrjsxptmxmfmfdnl111111111111111gksdkd");
        roomRepository.save(room);
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
                max=voteResult.get(select)+1;
                username=select;
            }
        };


        room.setResult(username);

        roomRepository.save(room);

        log.info(room.toString());
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
    public void deleteVote(String roomId) {
        Vote vote=voteRepository.findById("vote"+roomId).orElseThrow();
        voteRepository.delete(vote);
    }

    @Override
    public void deleteExecutionVote(String roomId) {
        ExecutionVote vote=executionVoteRepository.findById("executionvote"+roomId).orElseThrow();
        executionVoteRepository.delete(vote);
    }
}
