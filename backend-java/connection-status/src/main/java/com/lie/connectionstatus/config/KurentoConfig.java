package com.lie.connectionstatus.config;

import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KurentoConfig {
    @Bean
    public KurentoClient kurentoClient(){ return KurentoClient.create("ws://3.38.118.187:8888/kurento"); }
}
