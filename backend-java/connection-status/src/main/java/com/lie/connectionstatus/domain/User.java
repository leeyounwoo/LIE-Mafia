package com.lie.connectionstatus.domain;

import lombok.Builder;
import lombok.Data;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class User {
    private final String username;
    private Boolean ready;
    private  Authority authority;

    public User (final String username,Authority authority){
        this.username = username;
        this.ready = false;
        this.authority =  authority;
    }

    public void pressReady(){
        if(this.ready){
            this.ready = false;
            return;
        }
        this.ready = true;
    }
}
