package com.lie.gamelogic.domain;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;

public class UserConnectionManager{

    HashMap<String, WebSocketSession> Connection = new HashMap<>();

}
