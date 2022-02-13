package com.lie.websocketinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebsocketinterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketinterfaceApplication.class, args);
	}

}
