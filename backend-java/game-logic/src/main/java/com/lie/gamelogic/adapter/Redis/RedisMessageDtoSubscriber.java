package com.lie.gamelogic.adapter.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lie.gamelogic.dto.Client.ClientMessageDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class RedisMessageDtoSubscriber implements MessageListener {

    public static List<ClientMessageDto> clientDTOS = new ArrayList<>();
    private ObjectMapper Mapper = new ObjectMapper();

    public void onMessage(Message message, byte[] pattern) {

        try {

            ClientMessageDto client = Mapper.readValue(message.getBody(), ClientMessageDto.class);
            clientDTOS.add(client);

            log.info("DTO Message received: " + message.toString());
            log.info("Total CoffeeDTO's size: " + clientDTOS.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

