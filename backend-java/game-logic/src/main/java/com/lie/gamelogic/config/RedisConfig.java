package com.lie.gamelogic.config;

import com.lie.gamelogic.adapter.Redis.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Value("${spring.redis.stage.host}")
    private String host;

    @Value("${spring.redis.stage.port}")
    private Integer port;

    @Value("${spring.redis.stage.password}")
    private String password;

    @Bean
    MessageListenerAdapter messageDtoListener(){
        return new MessageListenerAdapter(new RedisMessageDtoSubscriber());
    }

//    @Bean
//    MessageListenerAdapter messageVoteDtoListener(){
//        return new MessageListenerAdapter(new RedisVoteDtoSubscriber());
//    }
//
//    @Bean
//    MessageListenerAdapter messageExecutionDtoListener(){
//        return new MessageListenerAdapter(new RedisexeutionVoteDtoSubscriber());
//    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(messageDtoListener(),topic01());
        container.addMessageListener(messageDtoListener(),topic02());
//        container.addMessageListener(messageVoteDtoListener(),topic03());
//        container.addMessageListener(messageVoteDtoListener(),topic04());
//        container.addMessageListener(messageExecutionDtoListener(),topic05());

        return container;
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(){
        RedisTemplate<byte[],byte[]> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfigurationBuilder.build());
    }

    @Bean
    ChannelTopic topic01(){
        return new ChannelTopic("ready");
    }

    @Bean
    ChannelTopic topic02(){
        return new ChannelTopic("start");
    }

    @Bean
    ChannelTopic topic03(){
        return new ChannelTopic("citizenVote");
    }

    @Bean
    ChannelTopic topic04(){
        return new ChannelTopic("nightVote");
    }

    @Bean
    ChannelTopic topic05(){
        return new ChannelTopic("executionVote");
    }

    @Bean
    MessagePublisher redisPublisher(){
        return new RedisMessagePublisher(redisTemplate(),topic01());
    }


}
