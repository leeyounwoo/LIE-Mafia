package com.lie.chat.port;

import com.lie.chat.domain.ChatRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends CrudRepository<ChatRoom,String> {
}
