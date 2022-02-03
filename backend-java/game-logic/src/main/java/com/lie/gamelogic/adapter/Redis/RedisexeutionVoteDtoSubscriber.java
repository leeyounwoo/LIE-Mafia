package com.lie.gamelogic.adapter.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.dto.Client.executionVoteDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class RedisexeutionVoteDtoSubscriber {
    private static List<executionVoteDto> executionVoteDtos = new ArrayList<>();
    private ObjectMapper Mapper = new ObjectMapper();

    public void onMessage(Message message, byte[] pattern) {

        try {

            executionVoteDto result = Mapper.readValue(message.getBody(), executionVoteDto.class);
            executionVoteDtos.add(result);

            log.info("DTO Message received: " + message.toString());
            log.info("Total CoffeeDTO's size: " + executionVoteDtos.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
