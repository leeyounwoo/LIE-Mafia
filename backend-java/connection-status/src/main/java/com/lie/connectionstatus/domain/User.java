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
    //private final WebSocketSession session;

    //private final MediaPipeline mediaPipeline;
    private WebRtcEndpoint outgoingMedia;
    private ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

    private Boolean ready;

    private  Authority authority;

    public User (final String username,Authority authority){
        this.username = username;
        //this.session = session;
        //this.mediaPipeline = mediaPipeline;
        this.ready = false;
        this.authority =  authority;
        this.outgoingMedia = null;
    }

    public WebRtcEndpoint getOutgoingWebRtcPeer() {
        return outgoingMedia;
    }

    //public WebSocketSession getSession() {
    //    return session;
    //}

    public void pressReady(){
        if(this.ready){
            this.ready = false;
            return;
        }
        this.ready = true;
    }
}
