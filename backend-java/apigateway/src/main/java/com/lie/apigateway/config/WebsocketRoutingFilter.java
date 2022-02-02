package com.lie.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

//@Configuration
public class WebsocketRoutingFilter implements GlobalFilter, Ordered {
    public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    private final WebSocketClient webSocketClient;
    private final WebSocketService webSocketService;
    public WebsocketRoutingFilter(WebSocketClient webSocketClient) {
        this(webSocketClient, new HandshakeWebSocketService());
    }
    public WebsocketRoutingFilter(WebSocketClient webSocketClient,
                                  WebSocketService webSocketService) {
        this.webSocketClient = webSocketClient;
        this.webSocketService = webSocketService;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        String scheme = requestUrl.getScheme();
         	if (isAlreadyRouted(exchange) || (!scheme.equals("ws") && !scheme.equals("wss"))) {
                 return chain.filter(exchange);
             }

        setAlreadyRouted(exchange);

        return this.webSocketService.handleRequest(exchange,
                 			new ProxyWebSocketHandler(requestUrl, this.webSocketClient, exchange.getRequest().getHeaders()));
    }

}