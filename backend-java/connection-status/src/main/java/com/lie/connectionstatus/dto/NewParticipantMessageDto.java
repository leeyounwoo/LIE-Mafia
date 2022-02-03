package com.lie.connectionstatus.dto;

import com.lie.connectionstatus.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class NewParticipantMessageDto {
    private String id;
    private HashMap<String, User> data = new HashMap<>();

    public NewParticipantMessageDto(String id, User user){
        this.id = id;
        this.data.put(user.getUsername(),user);
    }

    public NewParticipantMessageDto makeMessage(String id, User user){
        this.id = id;
        this.data.put(user.getUsername(),user);
        return this;
    }
}
