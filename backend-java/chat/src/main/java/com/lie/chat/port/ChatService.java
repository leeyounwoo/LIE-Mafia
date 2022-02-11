package com.lie.chat.port;

import com.lie.chat.domain.ChatRoom;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {
    void createChatRoom(ChatRoom chatRoom);
}
