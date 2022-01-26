package com.lie.connectionstatus.domain;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.awt.desktop.UserSessionEvent;
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
    public UserConnection getBySession(WebSocketSession session){ return usersBySessionId.get(session.getId()); }

    //ip 확인 가능할까? Origin ip 확인
    //WebRTCEndpoint 이미 존재하면?
    //그거에 대한 check 필요

    public UserConnection removeBySession(WebSocketSession session){
        final UserConnection user = getBySession(session);
        usersByUsername.remove(user.getUsername());
        usersBySessionId.remove(session.getId());
        return user;
    }
}
