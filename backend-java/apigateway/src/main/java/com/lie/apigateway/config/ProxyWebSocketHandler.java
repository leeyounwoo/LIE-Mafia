package com.lie.apigateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.springframework.cloud.gateway.filter.WebsocketRoutingFilter.SEC_WEBSOCKET_PROTOCOL;

//@Configuration
public class ProxyWebSocketHandler implements WebSocketHandler {

    private final WebSocketClient client;
  	private final URI url;
  	private final HttpHeaders headers;
  	private final List<String> subProtocols;


    public ProxyWebSocketHandler(WebSocketClient client, URI url, HttpHeaders headers, List<String> subProtocols) {
        this.client = client;
        this.url = url;
        this.headers = headers;
        this.subProtocols = subProtocols;
    }

    public ProxyWebSocketHandler(URI url, WebSocketClient client, HttpHeaders headers) {
        this.client = client;
        this.url = url;
        this.headers = new HttpHeaders();//headers;
        headers.entrySet().forEach(header -> {
            if (!header.getKey().toLowerCase().startsWith("sec-websocket")
             					&& !header.getKey().equalsIgnoreCase("upgrade")
             					&& !header.getKey().equalsIgnoreCase("connection")) {
                this.headers.addAll(header.getKey(), header.getValue());
            }
        });
        List<String> protocols = headers.get(SEC_WEBSOCKET_PROTOCOL);
        if (protocols != null) {
            this.subProtocols = protocols;
        } else {
            this.subProtocols = Collections.emptyList();
        }
    }

    @Override
    public List<String> getSubProtocols() {
        return WebSocketHandler.super.getSubProtocols();
    }

    @Override
  public Mono<Void> handle(WebSocketSession session) {
        //pass headers along so custom headers can be sent through
        return client.execute(url, this.headers, new WebSocketHandler() {

            @Override
            public Mono<Void> handle(WebSocketSession proxySession) {
                //Use retain() for Reactor Netty
                //    =
                Mono<Void> proxySessionSend = proxySession
                        .send(session.receive().doOnNext(WebSocketMessage::retain));
                //   =
                //.log("proxySessionSend", Level.FINE);
                Mono<Void> serverSessionSend = session
                        .send(proxySession.receive().doOnNext(WebSocketMessage::retain));
                //.log("sessionSend", Level.FINE);
                 			//
                return Mono.when(proxySessionSend, serverSessionSend).then();
            }

 		    public List<String> getSubProtocols() {
                return ProxyWebSocketHandler.this.subProtocols;
            }
        });
    }
}
