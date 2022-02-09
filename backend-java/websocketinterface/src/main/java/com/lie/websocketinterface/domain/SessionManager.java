package com.lie.websocketinterface.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
@Component
@Slf4j
public class SessionManager {
    private final ConcurrentMap<String, WebSocketSession> sessionBySessionId = new ConcurrentHashMap<String, WebSocketSession>();
    private final ConcurrentMap<String, WebSocketSession> sessionByUsername = new ConcurrentHashMap<String, WebSocketSession>();

    public void registerSession(WebSocketSession session,String username){
        log.info(session.toString() + " registerSession Method");
        if(checkIfSessionDoesNotExists(session.getId())){
            log.info(session.getId() + " registering");
            sessionBySessionId.put(session.getId(), session);
            return;
        }
        if(checkIfUsernameExist(username)){
            log.info("USER {} already exist. Replacing WebSocketSession");

            sessionByUsername.replace(username,session);
        }
        return;
    }

    public WebSocketSession getBySessionId(String sessionId){
        return sessionBySessionId.get(sessionId);
    }
    public Boolean checkIfUsernameExist(String username){
        if(sessionByUsername.containsKey(username)){
            return true;
        }
        return false;
    }
    public Boolean checkIfSessionDoesNotExists(String sessionId){
        log.info(sessionId + "checkIfSessionDoesNotExist");
        if(sessionBySessionId.containsKey(sessionId)){
            return false;
        }
        return true;
    }
    public void removeBySession(WebSocketSession session) {
        if(checkIfSessionDoesNotExists(session.getId())){
            log.info("Session Does not exist");
            return;
        }
        log.info("Session" + sessionBySessionId.get(session.getId())+"exist");
        sessionBySessionId.remove(session.getId());
        log.info("Session Removed");
        return;
    }
}
