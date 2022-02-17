package com.lie.connectionstatus.domain.user;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//전체 프로그램에서 user에 대한 중복 관리
@Getter
@Component
public class UserConnectionManager {
    private final ConcurrentMap<String, UserConnection> usersByUsername = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, UserConnection> usersBySessionId = new ConcurrentHashMap<>();

    public void connectUser(UserConnection user){
        usersByUsername.put(user.getUsername(), user);
        usersBySessionId.put(user.getSession().getId(), user);
    }

    public UserConnection getByUsername(String username){ return usersByUsername.get(username); }
    public UserConnection getBySession(String sessionId){ return usersBySessionId.get(sessionId); }

    public Boolean checkIfUserDoesNotExists(String sessionId){
        if(ObjectUtils.isEmpty(getBySession(sessionId))){
            return true;
        }
        return false;
    }

    //ip 확인 가능할까? Origin ip 확인
    //WebRTCEndpoint 이미 존재하면?
    //그거에 대한 check 필요

    public UserConnection removeBySession(String sessionId){
        if (checkIfUserDoesNotExists(sessionId)) {
            return null;
        }
        final UserConnection user = getBySession(sessionId);
        usersByUsername.remove(user.getUsername());
        usersBySessionId.remove(sessionId);
        return user;
    }
}
