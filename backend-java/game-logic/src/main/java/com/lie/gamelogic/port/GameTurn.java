package com.lie.gamelogic.port;

import org.springframework.stereotype.Service;

import java.util.Timer;

@Service

public interface GameTurn {

    void setTimer(int time);
    void setnextWork(String roomId, Timer timer);
}
