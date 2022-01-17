# WebSocket 등장 배경

-  http 프로토콜의 한계점

  - 요청/응답 패러다임이기에 클라이언트에서 요청을 보내야만 그에 대한 응답을 받음
  - 최근 동적인 기능 필요성 증대
  - 요청을 보내지 않아도 서버에서 클라이언트 쪽으로 데이터를 보내야 함

- HTTP 특징

  - Stateless 
    - 연결 유지X
    - 서버에서 먼저 요청을 보내는 것이 불가능

- 이를 위해서 다양한 방식 사용

  - Polling

    - 클라이언트에서 일정 주기마다 요청을 보내고 서버는 현재 상태를 바로 응답하는 방식

    - 실시간으로 반영되는 것이 중요한 서비스에는 별로 좋지 않고 

    - 서버에서 변화가 없더라도 매 요청마다 응답을 내려주기 때문에 불필요한 트래픽이 발생

      ![img](http://www.secmem.org/assets/images/websocket-socketio/polling.png)

  -  Long Polling

    - 클라이언트에서 요청을 보내고 서버에서는 이벤트가 발생했을 때 응답을 내려주고 클라이언트가 응답을 받았을때 다시 다음 응답을 기다리는 요청을 보내는 방식
    - 실시간 반응이 가능하고 polling에 비해서 불필요한 트래픽은 유발하지는 않지만 오히려 이벤트가 잦다면 순간적으로 과부하![img](http://www.secmem.org/assets/images/websocket-socketio/long-polling.png)

  - Streaming

    - 이벤트가 발생했을때 응답을 내려주는데 응답을 완료X , 연결을 유지하는 방식
    - 응답마다 다시 요청을 하지 않아도 되므로 효율적
    - 연결 시간이 길어질 수록 유효성 관리의 부담이 발생

# Web Socket

- 하나의 TCP 커넥션으로 전이중(full duplex) 통신을 제공하는 프로토콜
- HTTP에 비해 오버헤드가 적으므로 유용하게 사용할 수 있음
- Handshake는 HTTP 프로토콜에서 수행되지만, 이 후 통신은 HTTP 프로토콜 아래에서 수행
- HTTP 와 호환되지만 동일하지는 않다고 함
  - HTTP 포트 80, 443 위에서 동작하도록 설계
    - 초기 연결 시(HandShake) -> HTTP Request를 통해서 이루어진다. 
    - 초기 연결 이후 Upgrade Header 를 사용
- 이후 WebSocket / WS 용어 혼용

![img](http://www.secmem.org/assets/images/websocket-socketio/websocket.png)

- 최초 HTTP 요청(정확히는 WebSocket 을 사용하기 위한 기반 요청)을 통해 WebSocket 커넥션 생성

- 클라이언트마다 커넥션이 살아 있는 것은 동일

- STOMP

  - 텍스트 기반의 메시징 프로토콜
  - WS 위에서 동작할 필요는 없지만 주로 위로 올라가서 사용 되는 중

- Browser별 지원 현황

  ![캡처](C:\Users\dong\Desktop\캡처.JPG)

  - [github.com/sockjs/sockjs-client#supported-transports-by-browser-html-served-from-http-or-https](https://github.com/sockjs/sockjs-client#supported-transports-by-browser-html-served-from-http-or-https)

- WebSocket 접속 과정

  - TCP/IP 연결
  - 웹소켓 열기 핸드 쉐이크
  - 데이터 송, 수신

- 웹소켓 열기 HandShake

  - HTTP 1.1 프로토콜 사용

  - HandShake Request

    > **GET** /chat HTTP/1.1
    > **Host**: server.gorany.org
    > **Upgrade**: websocket
    > **Connection**: Upgrade
    > **Sec**-**WebSocket**-**Key**: dGhlIHNhbXBsZSBub25jZQ==
    > **Origin**: http://localhost:8080
    > Sec-WebSocket-Protocol: v10.stomp, v11.stomp, my-team-custom
    > **Sec-WebSocket-Version**: 13

    ![ClientHeader](C:\Users\dong\Desktop\ClientHeader.JPG)

  - **HandShake Response**

    > **HTTP/1.1 101** Switching Protocols
    > **Upgrade**: websocket
    > **Connection**: Upgrade
    > **Sec-WebSocket-Accept**: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=

    ![Response_header](C:\Users\dong\Desktop\Response_header.JPG)

#  SpringFrameWork에서 WebSocket

- Pacakge org.springframework.web.socket 

- Interface WebSocketHander을 implements 해서 사용

- 공식문서

  >  https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/socket/WebSocketHandler.html

