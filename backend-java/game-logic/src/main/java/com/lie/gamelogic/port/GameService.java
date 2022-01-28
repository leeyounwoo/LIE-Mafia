package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.User.User;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Service
public interface GameService {

    void GetJobs(WebSocketSession session) throws IOException;
    boolean GameStart(WebSocketSession session,String roomId);
    void phaseturn(WebSocketSession session);
    public boolean GameEnd(List<User> userList);

}
