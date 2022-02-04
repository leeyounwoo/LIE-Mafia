package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.Room;
import org.springframework.stereotype.Service;

@Service
public interface GameService {

// 메소드는 호출하는 순간 파악 할 수 있어야 한다.
    void createGameRoom(Room room);

}
