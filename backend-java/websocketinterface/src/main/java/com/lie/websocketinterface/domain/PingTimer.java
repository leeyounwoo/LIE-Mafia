package com.lie.websocketinterface.domain;

import org.springframework.web.socket.WebSocketSession;

import java.util.Timer;
import java.util.TimerTask;

public class PingTimer extends Timer {

    @Override
    public void schedule(TimerTask task, long delay) {
        super.schedule(task, delay);
    }
}
