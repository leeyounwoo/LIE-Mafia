package com.lie.gamelogic.port.Phase;

import com.lie.gamelogic.domain.Room;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

@Service
public interface GameTurn {

    void setTimer(int time);
    void setnextWork(String roomId, Timer timer);
}
