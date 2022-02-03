package com.lie.gamelogic.adapter.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.dto.Client.VoteDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class RedisVoteDtoSubscriber {
    private static List<VoteDto> VoteDtos = new ArrayList<>();
    private ObjectMapper Mapper = new ObjectMapper();

    public void onMessage(Message message, byte[] pattern) {

        try {

            VoteDto result = Mapper.readValue(message.getBody(), VoteDto.class);
            VoteDtos.add(result);

            log.info("DTO Message received: " + message.toString());
            log.info("Total CoffeeDTO's size: " + VoteDtos.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
