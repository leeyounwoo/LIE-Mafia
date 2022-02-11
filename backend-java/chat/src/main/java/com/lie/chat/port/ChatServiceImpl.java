package com.lie.chat.port;

import com.lie.chat.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;

    @Override
    public void createChatRoom(ChatRoom chatRoom) {
        log.info("save > ?");
        chatRepository.save(chatRoom);
    }

}
