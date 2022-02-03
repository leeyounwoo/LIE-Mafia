package com.lie.gamelogic.adapter.Redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher{

    private RedisTemplate<?,?> redisTemplate;

    private ChannelTopic topic;

    public RedisMessagePublisher(RedisTemplate<?,?> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
