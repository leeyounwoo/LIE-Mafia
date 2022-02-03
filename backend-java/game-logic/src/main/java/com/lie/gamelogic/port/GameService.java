package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.room.Room;
import com.lie.gamelogic.dto.Client.VoteDto;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public interface GameService {

    void GameReady(String roomId, String username);
    boolean allReady(String roomId);
    void GameStart(WebSocketSession session, String roomId);
    Room findRoom(String roomId);
    Room GetJobs(WebSocketSession session, Room room) throws IOException;
    Room phaseTurn(String roomId);
    String executionVote(String roomId, String select, boolean isDead);
    boolean GameEnd(String roomId);
    String findVote(VoteDto vote);

}
