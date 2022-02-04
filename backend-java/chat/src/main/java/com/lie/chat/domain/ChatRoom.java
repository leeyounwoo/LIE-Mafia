package com.lie.chat.domain;

import com.lie.chat.dto.ChatMessage;
import com.lie.chat.port.ChatService;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ChatRoom {
    private String roomId;
    private List<User> users=new ArrayList<>();
    private Set<WebSocketSession> sessions=new HashSet<>();

    @Builder
    public ChatRoom(String roomId){
        this.roomId=roomId;
    }

    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService){
        if(chatMessage.getType().equals(ChatMessage.MessageType.ENTER)){
            sessions.add(session); //채팅방에 등록됨
            chatMessage.setContent(chatMessage.getUsername()+"님이 입장했습니다.");
        }
        sendMessage(chatMessage,chatService);
    }

    public <T> void sendMessage(T chatMessage, ChatService chatService) {
        sessions.parallelStream().forEach(session -> chatService.sendMessage(session,chatMessage));
    }

}
