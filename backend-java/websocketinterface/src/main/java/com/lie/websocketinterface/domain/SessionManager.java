package com.lie.websocketinterface.domain;

import com.lie.websocketinterface.exception.DuplicateException;
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

    public void registerSession(WebSocketSession session,String username) throws DuplicateException {
        log.info(session.toString() + " registerSession Method");
        if(checkIfUsernameExist(username)){
            log.debug("USER {} already exist. Replacing WebSocketSession");
            throw new DuplicateException("User "+username+"already exist.");
        }
        if(checkIfSessionExist(session.getId())){
            throw new DuplicateException("Session Already Registered");
        }

        log.info(session.getId() + " registering");
        sessionBySessionId.put(session.getId(), session);
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
    public Boolean checkIfSessionExist(String sessionId){

        if(sessionBySessionId.containsKey(sessionId)){
            log.debug(sessionId + " Already Exists");
            return true;
        }
        return false;
    }
    public void removeBySession(WebSocketSession session) {
        if(checkIfSessionExist(session.getId())){
            log.info("Session Does not exist");
            return;
        }
        log.info("Session" + sessionBySessionId.get(session.getId())+"exist");
        sessionBySessionId.remove(session.getId());
        log.info("Session Removed");
    }
}
