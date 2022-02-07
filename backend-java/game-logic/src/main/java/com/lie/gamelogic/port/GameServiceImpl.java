package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.Room;
import com.lie.gamelogic.domain.User;
import com.lie.gamelogic.domain.UserVote;
import com.lie.gamelogic.domain.Vote;
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
    public void createVote(String roomId) {
        Vote vote=new Vote();

        Room room = roomRepository.findById(roomId).orElseThrow();
        voteRepository.save(vote.createVote(roomId,room.getRoomPhase()));
        vote=vote.createVote(roomId,room.getRoomPhase());
        log.info(vote.toString());
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
}
