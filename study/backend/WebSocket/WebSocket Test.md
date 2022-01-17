# WebSocket Test

### 1. 라이브러리 추가



- Gradle

  ```groovy
  dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  implementation 'org.springframework.boot:spring-boot-starter-security'
  implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'

  ///////////////////////////////////////////////////////////////////////
  implementation 'org.springframework.boot:spring-boot-starter-web'
  implementation 'org.springframework.boot:spring-boot-starter-websocket'
  ///////////////////////////////////////////////////////////////////////

  implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity5'
  compileOnly 'org.projectlombok:lombok'
  developmentOnly 'org.springframework.boot:spring-boot-devtools'
  runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
  annotationProcessor 'org.projectlombok:lombok'
  providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
  testImplementation 'org.springframework.boot:spring-boot-starter-test'
  testImplementation 'org.springframework.security:spring-security-test'

  compile group: 'org.thymeleaf.extras', name: 'thymeleaf-extras-java8time'
  implementation 'com.querydsl:querydsl-jpa'
  // https://mvnrepository.com/artifact/net.coobird/thumbnailator
  implementation group: 'net.coobird', name: 'thumbnailator', version: '0.4.8'
  }
  ```

- build .gradle에 Spring webSocket을 추가

- spring-boot-starter-web 이 부분이 WebSocket을 추가 시켜주는 부분임

  ​

  ### 2. WebSocket Handler

  ​

- 소켓 통신은 서버와 클라이언트가 1:N의 관계를 맺음 , 서버는 다수의 클라이언트가 보낸 메세지를 처리할 핸들러 필요

- 텍스트 기반의 채팅 구현을 위한 **'TextWebSocketHandler'**를 상속받아서 작성함

- Log출력하고 클라이언트에게 환영하는 메세지를 보내는 역할 추가

  ```java
  package org.gorany.community.handler;

  import lombok.extern.log4j.Log4j2;
  import org.springframework.stereotype.Component;
  import org.springframework.web.socket.CloseStatus;
  import org.springframework.web.socket.TextMessage;
  import org.springframework.web.socket.WebSocketSession;
  import org.springframework.web.socket.handler.TextWebSocketHandler;

  import java.util.ArrayList;
  import java.util.List;

  @Component
  @Log4j2
  public class ChatHandler extends TextWebSocketHandler {

      private static List<WebSocketSession> list = new ArrayList<>();

      @Override
      protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
          String payload = message.getPayload();
          log.info("payload : " + payload);

          for(WebSocketSession sess: list) {
              sess.sendMessage(message);
          }
      }

      /* Client가 접속 시 호출되는 메서드 */
      @Override
      public void afterConnectionEstablished(WebSocketSession session) throws Exception {

          list.add(session);

          log.info(session + " 클라이언트 접속");
      }

      /* Client가 접속 해제 시 호출되는 메서드드 */

      @Override
      public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

          log.info(session + " 클라이언트 접속 해제");
          list.remove(session);
      }
  }
  ```

  ​

  - payload 

    - 사전적인 의미로 전송되는 데이터
    -  데이터를 전송할 때, Header와 META 데이터, 에러 체크 비트 등과 같은 다양한 요소들을 함께 보내 데이터 전송 효율과 안정성 증대
    - 데이터 전송시 Header와 META 데이터등 과 같은 요소를 제거 하고 데이터 그 자체를 페이로드라고 한다. 

    ```json
    {
    "status":
    "from":"localhost",
    "to":"http://melonicedlatte.com/chatroom/1",
    "method":"GET",
    "data":{"message":"There is a cutty dog!"}
    }
    ```

    이러한 JSON 데이터에서 Payload은 data로 부분이 된다. 

    ​

  - log4j2

    - java에서 로그를 생성하는 프로그램  log4j, logback, log4j2 가 있음

    - log4j - > logback - > log4j2 순으로 개발 

    - 멀티스레드 환경에서 많은 처리량을 처리 가능, 다양한 설정파일 및 appender을 지원

      ​

  - WebSession

    - cookie-session과는 다른 개념 
    - 웹 소켓의 연결 및 전체 통신을 관리해주는 객체
    - 한번 연결된 것을 가지고 계속 통신 가능
    - https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/server/WebSession.html
    - Start()라는 것을 사용해서 명시적으로 사용이 가능함.

  ### 3. WebSocket Config

  - 핸들러를 이용해 WebSocket을 활성화하기 위함


  - @EnableWebSocket 어노테이션 사용 WebSocket 활성화
  - Endpoint(WebSocket에 접속하기 위한) ws/chat으로 설정
  - 도메인이 다른 서버에서도 접속 가능하도록 **CORS : setAllowedOrigins("\*");**  설정

  ```java
  import lombok.RequiredArgsConstructor;
  import org.gorany.community.handler.ChatHandler;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.socket.config.annotation.EnableWebSocket;
  import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
  import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

  @Configuration
  @RequiredArgsConstructor
  @EnableWebSocket
  public class WebSocketConfig implements WebSocketConfigurer {

      private final ChatHandler chatHandler;

      @Override
      public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
      
          registry.addHandler(chatHandler, "ws/chat").setAllowedOrigins("*");
      }
  }
  ```

  - CORS

    - **교차 출처 리소스 공유(Cross-Origin Resource Sharing, CORS)**
    - 추가 [HTTP](https://developer.mozilla.org/ko/docs/Glossary/HTTP) 헤더를 사용하여, 한 출저에서 실행 중인 웹 애플리케이션이 다른 출저의 선택한 자원에 접근할 수 있는 권한을 부여하도록 브라우저에게 알려주는 체제
    - 출저 -> 도메인, 프로토콜, 포트 
    - 웹 어플리케이션은 리소스가 자신의 출저와 다를때 교차 출저 HTTP 요청을 실행

  - Endpoint

    ![img](https://blog.kakaocdn.net/dn/cizILv/btq1z48g0iR/SsOCKefke7I1Cj9xUk6PK1/img.png)

    - 메서드는 같은 URL들에 대해서도 다른 요청을 하게끔 구별하게 해주는 항목
    - GET,POST,PUT,DELETE 메소드에 따라 요청이 다름
    -  API가 서버에서 자원(resource)에 접근할 수 있도록 하는 URL

### 4. ChatController

- controller 부분

  ```java
  import lombok.extern.log4j.Log4j2;
  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.GetMapping;

  @Controller
  @Log4j2
  public class ChatController {
      
      @GetMapping("/chat")
      public String chatGET(){

          log.info("@ChatController, chat GET()");
          
          return "chat";
      }
  }
  ```

### 5.  **chat.html 그리고 JS**

- thymeleaf 템플릿 엔진을 적용

- text박스에 문자열 입력시 "사람명 : 문자열" 이 형식으로 전송됨

  ```html
  <!DOCTYPE html>
  <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

  <th:block th:replace="~{/layout/basic :: setContent(~{this :: content})}">
      <th:block th:fragment="content">
      
          <div class="container">
              <div class="col-6">
                  <label><b>채팅방</b></label>
              </div>
              <div>
                  <div id="msgArea" class="col"></div>
                  <div class="col-6">
                      <div class="input-group mb-3">
                          <input type="text" id="msg" class="form-control" aria-label="Recipient's username" aria-describedby="button-addon2">
                          <div class="input-group-append">
                              <button class="btn btn-outline-secondary" type="button" id="button-send">전송</button>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
          
      </th:block>
  </th:block>

  </html>
  ```

  ```javascript
  <script th:inline="javascript">
              $(document).ready(function(){

              const username = [[${#authentication.principal.username}]];

              $("#disconn").on("click", (e) => {
                  disconnect();
              })
              
              $("#button-send").on("click", (e) => {
                  send();
              });

              const websocket = new WebSocket("ws://localhost:8080/ws/chat");

              websocket.onmessage = onMessage;
              websocket.onopen = onOpen;
              websocket.onclose = onClose;

              function send(){

                  let msg = document.getElementById("msg");

                  console.log(username + ":" + msg.value);
                  websocket.send(username + ":" + msg.value);
                  msg.value = '';
              }
              
              //채팅창에서 나갔을 때
              function onClose(evt) {
                  var str = username + ": 님이 방을 나가셨습니다.";
                  websocket.send(str);
              }
              
              //채팅창에 들어왔을 때
              function onOpen(evt) {
                  var str = username + ": 님이 입장하셨습니다.";
                  websocket.send(str);
              }

              function onMessage(msg) {
                  var data = msg.data;
                  var sessionId = null;
                  //데이터를 보낸 사람
                  var message = null;
                  var arr = data.split(":");

                  for(var i=0; i<arr.length; i++){
                      console.log('arr[' + i + ']: ' + arr[i]);
                  }

                  var cur_session = username;

                  //현재 세션에 로그인 한 사람
                  console.log("cur_session : " + cur_session);
                  sessionId = arr[0];
                  message = arr[1];

                  console.log("sessionID : " + sessionId);
                  console.log("cur_session : " + cur_session);

                  //로그인 한 클라이언트와 타 클라이언트를 분류하기 위함
                  if(sessionId == cur_session){
                      var str = "<div class='col-6'>";
                      str += "<div class='alert alert-secondary'>";
                      str += "<b>" + sessionId + " : " + message + "</b>";
                      str += "</div></div>";
                      $("#msgArea").append(str);
                  }
                  else{
                      var str = "<div class='col-6'>";
                      str += "<div class='alert alert-warning'>";
                      str += "<b>" + sessionId + " : " + message + "</b>";
                      str += "</div></div>";
                      $("#msgArea").append(str);
                  }
              }
              })
  </script>
  ```

  Log 형태 

  ![img](https://blog.kakaocdn.net/dn/bLqo1F/btq1AGHFkaB/W0OBd6ZRkl9AyxRakkyE21/img.png)



### 6. 문제점

- 채팅방은 단 하나이다. 여러 방을 만들어야함
- 웹소켓을 지원하지 않는 브라우저에서 동작 X
- SockJS로 같은 방법으로 채팅 동작 하게 해주는 방법이 필요

### 7. Simple WebSocket Client 

![img](https://lh3.googleusercontent.com/HANIICCpYwjRwRJt0fvlGzGYC99poUpWVKeug4krKn0r2E_l85H6HGKUXi8ExKIcJCm-LWk7XI63URJD9zGqfuSDKg=w640-h400-e365-rj-sc0x00ffffff)

- URL에 ws에 webSocket 관련 url을 넣어줌
- Status에 연결 여부 
- Request에 message를 넣어주면 됨 -> header 같은 것이 나오게 됨 

### 8. 참조

> https://dev-gorany.tistory.com/212
>
> https://velog.io/@zzarbttoo/%EB%A1%9C%EA%B7%B8log4j2%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%B4-%ED%8A%B9%EC%A0%95-%ED%8C%8C%EC%9D%BC%EC%97%90-%EB%A1%9C%EA%B7%B8-%EA%B8%B0%EB%A1%9D%EC%9D%84-%EB%AA%A8%EC%95%84%EB%B3%B4%EC%95%98%EB%8B%A4
>
> https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/server/WebSession.html
>
> https://developer.mozilla.org/ko/docs/Web/HTTP/CORS
>
> https://stomp.github.io/stomp-specification-1.2.html#Frames_and_Headers

- WebSocket의 단점

  - **모든 클라이언트의 브라우저에서 WebSocket을 지원한다는 보장이 없다.**

  - **Server/Client 중간에 위치한 Proxy가 Upgrade헤더를 해석하지 못해 서버에 전달하지 못할 수 있다. **

  - **Server/Client 중간에 위치한 Proxy가 유휴 상태에서 도중에 Connection 종료시킬 수도 있다.**

    ​

- **WebSocket Emulation**사용으로 이를 극복 가능!

  - **WebSocket을 시도** 후, **실패할 경우 HTTP Streaming, Long-Polling 같은 HTTP 기반의 다른 기술로 전환해 다시 연결을 시도하는 것**

    ​

- node.js 사용시 Socket.io를 이용하는 것이 일반적

- Spring 사용시 SockJs를 이용 

  - Spring 프레임워크는 Servlet 스택 위에서 Server/Client 용도의 SockJS 프로토콜을 모두 지원





## SockJS

- 정의
  - 어플리케이션이 WebSocket API를 사용하도록 허용하지만 브라우저에서 WebSocket을 지원하지 않는 경우에 대안으로 사용
  - 어플리케이션의 코드를 변경할 필요 없이 런타임에 필요할 때 대체하는 것


- SockJS의 구성
  - **SockJS** **Protocol**
  - **SockJS Javascript** **Client** - 브라우저에서 사용되는 클라이언트 라이브러리
  - **SockJS** **Server** 구현 - Spring-websocket 모듈을 통해 제공
  - **SockJS Java Client **- Spring-websocket 모듈을 통해 제공 (**Spring ver.4.1** ~ )



- 전송 타입
  - WebSocket
  - HTTP Streaming
  - HTTP Long Polling





## WebSocket Emulation Process

- **GET /info**

  - **서버가 WebSocket을 지원 여부 파악**
  - 전송과정에서 **Cookies 지원이 필요 여부 파악 **
  - CORS를 위한 Origin 정보 등의 정보를 응답으로 전달 받음

- 전달 받은 값을 이용해 전송 타입 결정 

- 노란색이 전송 요청임

  ![img](https://blog.kakaocdn.net/dn/bZp2Bu/btq1OM0cPc2/4wVEG1T6fnwKbVwuxQgREK/img.png)

  ![img](https://blog.kakaocdn.net/dn/caZXp4/btq1I8jRmCu/Nhj5KfKGFVZThMyk0qvWdK/img.png)

전송 요청 URL 구성

> **https://host:port/myApp/myEndpoint/{server-id}/{session-id}/{transport}**

- server-id : 클러스터에서 요청을 라우팅하는데 사용하나 이외에는 의미 없음
- session-id : SockJS session에 소속하는 HTTP 요청과 연관성 있음
- transport : 전송 타입 (예 : websocket, xhr-streaming, xhr-polling )

![img](https://blog.kakaocdn.net/dn/dBACTK/btq1ISnXXlJ/IvxcusE04cWtG3cF6SNFq0/img.png)

- **WebSocket 전송**
  - WebSocket Handshaking을 위한 하나의 HTTP 요청을 필요
  - 모든 메세지들은 그 이후 사용했던 Socket을 통해 교환된다

![img](https://blog.kakaocdn.net/dn/b5G3Oq/btq1KCjXvj6/NCoiNv7i3tKL5kt0LFcfOK/img.png)

- **HTTP Streaming 전송**
  - 서버 -> 클라이언트로의 메세지들을 위해 하나의 Long-running 요청
  - 추가적인 HTTP POST 요청은 클라이언트 -> 서버로의 메세지를 위해 사용


- **Long Polling 전송**

  -  서버 -> 클라이언트로의 응답 후 현재의 요청을 끝내는 것을 제외하고는 XHR Streaming과 유사

- ![img](https://blog.kakaocdn.net/dn/bDFQmv/btq1Mq4pXcz/OvdPpuirZpzjEU7k8K9xFk/img.png)

- 서버는

  **"o"** (open frame)을 초기에 전송하고, 메세지는 ["msg1", "msg2"]와 같은 JSON-Encoded 배열로서 전달되며,

  **"h"** (heartbeat frame)는 기본적으로 25초간 메세지 흐름이 없는 경우에 전송하고

  **"c"** (close frame)는 해당 세션을 종료한다.

- **SockJS**는 메세지 Frame의 크기를 최소화하기 위해 노력

### SockJS Enabling

이전 WebSocketConfig에서

```java
import lombok.RequiredArgsConstructor;
import org.gorany.community.handler.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat")
        .setAllowedOrigins("http://localhost:8080")
        .withSockJS();
    }
    //.withSockJS() 추가
    //setAllowedOrigins("*")에서 *라는 와일드 카드를 사용하면
    //보안상의 문제로 전체를 허용하는 것보다 직접 하나씩 지정해주어야 한다고 한다.
}
```

### 허용된 Origins

- **Origin**은 **Protocol, Host, Port** 3개 부분으로 구성됨

  > http://localhost:8080/
  >
  > protocol : http
  >
  > host : localhost
  >
  > port : 8080

  **3개 부분이 모두 동일한 경우**만 **동일한 Origin** 임

  WebSocket 및 SockJS의 default는 동일한 Origin요청만 수락하는 것

  - 모든 목록이나 특정 목록을 허용하는 것도 가능

> - 동일한 오리진 요청만 허용 (default)
>   - 이 모드에서는  SockJS가 활성화되면 iframe HTTP 응답 헤더 X-Frame-Options가 'SameOrigin'으로 설정되며, JSONP 전송은 요청의 오리진 확인이 불가능하므로 비활성화된다. 따라서 이 모드가 활성화된 경우 IE 6, 7은 지원되지 않는다.
> - 지정된 Origin목록 허용
>   - 이 모드에서는 지정된 Origin은 반드시 http:// or https://로 시작해야한다. 이 모드에서 SockJS가 활성화되면 iframe 전송이 비활성화되므로 IE 6 ~ 9까지는 지원되지 않는다.
>   - 위의 코드는 지정된 Origin 목록 허용을 한 것이다.
> - 모든 Origin 허용
>   - 이 모드를 사용하면 허가된 오리진 값으로써 **'\*'**를 사용해야 한다. 이 모드에서는 모든 전송(Send)를 사용할 수 없다.



#### **Heartbeats**

- SockJS 프로토콜은 프록시가 연결이 끊겼다는 결론을 내리는 것을 방지하기 위해 서버가 Heartbeat 메세지를 보내도록 요구
- Spring SockJS 구성에는 HeartbeatTime 빈도를 사용자 정의하는 데 사용할 수 있는 속성
- 기본값은 해당 연결에 어떤 메세지도 없는 25초
- 25초는 IETF 권고안
- **STOMP를 이용해 Heartbeat를 주고 받는 경우 SockJS Heartbeat 설정은 비활성화**

![img](https://blog.kakaocdn.net/dn/biiTKe/btq1Qn7djRy/D4rNUvrds1xHEV4XeQupFk/img.png)

- 개발자도구 - 네트워크 - WebSocket- Message탭을 눌려보면 WebSocket 메시지가 보임
- 여기서 h가 heatbeat임

### STOMP

- **Simple Text Oriented Messaging Protocol** 메세징 전송을 효율적으로 하기 위해 탄생한 프로토콜
- pub / sub 구조로 되어 있어 메세지를 전송 / 메세지를 받아 처리하는 부분이 확실히 정해져 있음
- 개발자 입장에서 명확하게 인지하고 개발할 수 있는 이점이 있음
- **WebSocket 위에서 동작하는 프로토콜로써 클라이언트와 서버가 전송할 메세지의 유형, 형식, 내용들을 정의하는 매커니즘**
- **메세지의 헤더에 값을 줄 수 있어** 헤더 값을 기반으로 통신 시 인증 처리를 구현하는 것도 가능
- STOMP 스펙에 정의한 규칙만 잘 지키면 여러 언어 및 플랫폼 간 메세지를 상호 운영



STOMP란

-  Text 지향 프로토콜이나, Message Payload에는 Text or Binary 데이터를 포함
- **pub / sub**란 메세지를 공급하는 주체와 소비하는 주체를 분리해 제공하는 메세징 방법

기본적인 패턴

> 우체통(Topic)이 있다면 집배원(Publisher)이 신문을 우체통에 배달하는 행위가 있고, 우체통에 신문이 배달되는 것을 기다렸다가 빼서 보는 구독자(Subscriber)의 행위가 있다. 이때 구독자는 다수가 될 수 있다. pub / sub 컨셉을 채팅방에 빗대면 다음과 같다.
>
> 
>
> **채팅방 생성** : pub / sub 구현을 위한 Topic이 생성됨
>
> **채팅방 입장** : Topic 구독
>
> **채팅방에서 메세지를 송수신** : 해당 Topic으로 메세지를 송신(pub), 메세지를 수신(sub)



클라이언트는 메세지를 전송하기 위해

SEND, SUBSCRIBE COMMAND를 사용 가능, 

SEND, SUBSCRIBE COMMAND 요청 Frame에는 메세지, 누가 받아서 처리할지에 대한 Header 정보가 포함됨

"destination" 헤더를 요구, 어디에 전송할지, 혹은 어디에서 메세지를 구독할 것 인지를 나타냄.

- STOMP는 Publish-Subscribe 매커니즘을 제공
- Broker를 통해 타 사용자들에게 메세지를 보내거나 서버가 특정 작업을 수행하도록 메시지를 보냄



**Spring에서 지원하는 STOMP를 사용하면 Spring WebSocket 어플리케이션은 STOMP Broker**로 동작하게 됨

- Simple In Memory Broker 클라이언트의 SUBSCRIBE 정보를 자체적으로 메모리에 유지한다.

 **스프링은 메세지를 외부 Broker에게 전달**하고, **Broker는 WebSocket으로 연결된 클라이언트에게 메세지를 전달**

- HTTP 기반의 보안 설정과 공통된 검증 등을 적용



### STOMP 구조 

- HTTP에서 모델링 되는 Frame 기반 프로토콜
-  Frame은 몇 개의 Text Line으로 지정된 구조
- 첫 번째 라인은 Text이고 이후 Key:Value 형태로 Header의 정보를 포함

> **COMMAND**
> header1:value1
> header2:value2
>
> Body^@

- COMMAND : SEND, SUBSCRIBE를 지시할 수 있다.
- header : 기존의 WebSocket으로는 표현이 불가능한 header를 작성할 수 있다.
  - destination : 이 헤더로 메세지를 보내거나(SEND), 구독(SUBSCRIBE)할 수 있다.

**destination**는 의도적으로 정보를 불분명하게 정의하였는데, 이는 STOMP 구현체에서 문자열 구문에 따라 직접 의미를 부여하도록 하기위함이다.

따라서 destination 정보는 STOMP 서버 구현체마다 달라질 수 있기 때문에 각 구현체의 스펙을 살펴보아야 한다.

 

일반적인 형식 

> "topic/.." --> publish-subscribe (1:N)
> "queue/" --> point-to-point (1:1)



ClientA가 5번 채팅방에 대해 **구독**하는 예시이다.

> **SUBSCRIBE**
> **destination**: /topic/chat/room/5
> **id**: sub-1
>
> ^@

ClientB에서 채팅 메세지를 보내는 예시

> **SEND**
> **destination**: /pub/chat
> **content-type**: application/json
>
> **{"chatRoomId": 5, "type": "MESSAGE", "writer": "clientB"} **^@

- **서버는 모든 구독자에게 메세지를 Broadcasting하기 위해 MESSAGE COMMAND** 사용

> **MESSAGE**
> **destination:** /topic/chat/room/5
> **message-i**d: d4c0d7f6-1
> **subscription:** sub-1
>
> **{"chatRoomId": 5, "type": "MESSAGE", "writer": "clientB"}** ^@

![img](https://blog.kakaocdn.net/dn/c28XZO/btq2cX8trC2/pCkz1QsD4C9g2G9wKUQKo0/img.jpg)

#### 주의점

- **서버는 불분명한 메세지를 전송할 수 없다**
- **서버 메시지의 "subscription-id" 헤더는 클라이언트 구독의 "id"헤더와 일치**해야함

## Client Frames

![img](https://blog.kakaocdn.net/dn/q1J3E/btq2b479ejW/er7R29yKhqy7bY8gNVd4A0/img.png)

#### **SEND**

- SEND frame은 destination의 메세징 시스템으로 메세지를 보낸다. 필수 헤더는 어디로 보낼지에 대한 "destination" 하나이다. SEND frame의 body는 보내고자 하는 메세지이다.

> **SEND**
> **destination**: /queue/a
> **content-type**: text/plain
> hello queue a
> ^@

- SEND Frame은 body가 있는 경우 "content-length"와 "content-type"헤더를 반드시 가져야만 한다..

#### **SUBSCRIBE**

- SUBSCRIBE frame은 주어진 destination에 등록하기 위해 사용된다. SEND frame과 마찬가지로 Subscribe는 client가 구독하기 원하는 목적지를 가리키는 "destination" 헤더를 필요로 한다. 가입된 대상에서 수신된 모든 메세지는 이후 MESSAGE frame로서 서버에서 클라이언트에게 전달된다.

> **SUBSCRIBE**
> **id**: 0
> **destination**: /queue/foo
> **ack**: client
> ^@

- 단일 연결은 여러 개의 구독을 할 수 있으므로 구독 ID를 고유하게 식별하기 위해 "id"헤더가 프레임에 포함되어야 한다.



### STOMP 장점

Spring framework 및 Spring Security는 STOMP 를 사용하여 WebSocket만 사용할 때보다 더 다채로운 모델링을 할 수 있다.

- Messaging Protocol을 만들고 메세지 형식을 커스터마이징 할 필요가 없다.
- RabbitMQ, ActiveMQ 같은 Message Broker를 이용해, Subscription(구독)을 관리하고 메세지를 브로드캐스팅할 수 있다.
- WebSocket 기반으로 각 Connection(연결)마다 WebSocketHandler를 구현하는 것 보다 @Controller 된 객체를 이용해 조직적으로 관리할 수 있다.
  - 즉, 메세지는 STOMP의 "destination" 헤더를 기반으로 @Controller 객체의 @MethodMapping 메서드로 라우팅 된다.
- STOMP의 "destination" 및 Message Type을 기반으로 메세지를 보호하기 위해 Spring Security를 사용할 수 있다.

### **Enable STOMP**

Spring은 WebSocket / SockJS를 기반으로 STOMP를 위해 spring-messaging과 spring-websocket 모듈을 제공한다.

아래 예시와 같이, STOMP 설정을 할 수 있는데 기본적으로 커넥션을 위한 STOMP Endpoint를 설정해야만 한다.

 

```java
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    
        registry.addEndpoint("/example").withSockJS();  
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    
        config.setApplicationDestinationPrefixes("/test"); 
        config.enableSimpleBroker("/topic", "/queue"); 
    }
}
```

- /example는 WebSocket 또는 SockJS Client가 웹소켓 핸드셰이크 커넥션을 생성할 경로이다.
- /test 경로로 시작하는 STOMP 메세지의 "destination" 헤더는 @Controller 객체의 @MessageMapping 메서드로 라우팅된다.
- 내장된 메세지 브로커를 사용해 Client에게 Subscriptions, Broadcasting 기능을 제공한다. 또한 /topic, /queue로 시작하는 "destination" 헤더를 가진 메세지를 브로커로 라우팅한다.

내장된 **Simple Message Broker**는 **/topic, /queue prefix**에 대해 특별한 의미를 부여하지 않는다.

### **Using STOMP**

SockJS로 브라우저에 연결하기 위해 sockjs-client를 이용할 수 있다. STOMP에 있어 많은 어플리케이션들은 jmesnil/stomp-websocket( stomp.js 로 알려진 )라이브러리를 사용해왔지만, 더이상 유지되지 않는다. 최근에는 JSteunou/webstomp-client를 많이 사용한다.

 

```java
var sock = new SockJS("/ws/chat");
var stomp = webstomp.over(sock);

stomp.connect({}, function(frame) {
}

/* WebSocket만 이용할 경우 */

var websocket = new WebSocket("/ws/chat");
var stomp = webstomp.over(websocket);

stomp.connect({}, function(frame) {
}
```

More Example Codes...

- [Using WebSocket to build an interactive web application - a getting started guide](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [Stock Portfolio - a sample application](https://github.com/rstoyanchev/spring-websocket-portfolio)

### **Flow of Messages**

STOMP Endpoint가 노출되고 나면, Spring 어플리케이션은 연결되어있는 Client들에 대해 STOMP 브로커가 된다

 

(/app == /pub, /topic == /sub) 아래 그림은 내장 메세지 브로커를 사용한 경우 컴포넌트 구성을 보여준다.

![img](https://blog.kakaocdn.net/dn/HOTPn/btq2c29QThJ/WcP1GMTYDPMDrlDutl1aZk/img.png)출처: https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp

 

**spring**-**message** 모듈은 Spring framework의 통합된 Messaging 어플리케이션을 위한 지원을 한다.

- **Message** : headers와 payload를 포함하는 메세지의 표현

- **MessageHandler** : Message 처리에 대한 계약

- **SimpleAnnotationMethod** : @MessageMapping 등 Client의 SEND를 받아서 처리한다.

- **SimpleBroker** : Client의 정보를 메모리 상에 들고 있으며, Client로 메세지를 보낸다.

- channel

  - clientInboundChannel : WebSocket Client로부터 들어오는 요청을 전달하며, WebSocketMessageBrokerConfigurer 를 통해 intercept, taskExecutor를 설정할 수 있다.

    - 클라이언트로 받은 메세지를 전달

  - clientOutboundChannel : WebSocket Client로 Server의 메세지를 내보내며, 

    WebSocketMessageBrokerConfigurer를 통해 intercept, taskExecutor를 설정할 수 있다.

    - 클라이언트에게 메세지를 전달

  - brokerChannel :  Server 내부에서 사용하는 채널이며, 이를 통해SimpleAnnotationMethod는 SimpleBroker 의 존재를 직접 알지 못해도 메세지를 전달할 수 있다.

    - 서버의 어플리케이션 코드 내에서 브로커에게 메세지를 전달

### STOMP 적용



### DI(Dependency Injection)

> implementation group: 'org.webjars', name: 'stomp-websocket', version: '2.3.3-1' 

build.gradle에 추가 시켜 준다. 

#### StompWebSocketConfig 

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //endpoint를 /stomp로 하고, allowedOrigins를 "*"로 하면 페이지에서
    //Get /info 404 Error가 발생한다. 그래서 아래와 같이 2개의 계층으로 분리하고
    //origins를 개발 도메인으로 변경하니 잘 동작하였다.
    //이유는 왜 그런지 아직 찾지 못함
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
```

- @EnableWebSocketMessageBroker

  - Stomp를 사용하기위해 선언하는 어노테이션

- setApplicationDestinationPrefixes

   

  : Client에서 SEND 요청을 처리

  ​

  - Spring docs에서는 /topic, /queue로 나오나 편의상 /pub, /sub로 변경

- enableSimpleBroker

  - 해당 경로로 SimpleBroker를 등록. SimpleBroker는 해당하는 경로를 SUBSCRIBE하는 Client에게 메세지를 전달하는 간단한 작업을 수행

- enableStompBrokerRelay 

  - SimpleBroker의 기능과 외부 Message Broker( RabbitMQ, ActiveMQ 등 )에 메세지를 전달하는 기능을 가짐

#### ChatMessageDTO

``` java
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {

    private String roomId;
    private String writer;
    private String message;
}

```

#### DTO란?

- `DTO(Data Transfer Object)` 는 계층 간 데이터 교환을 하기 위해 사용하는 객체로, DTO는 로직을 가지지 않는 순수한 데이터 객체(getter & setter 만 가진 클래스)입니다.
- 클라이언트 요청에 포함된 데이터를 담아 서버 측에 전달하고, 서버 측의 응답 데이터를 담아 클라이언트에 전달하는 계층간 전달자 역할



#### **ChatRoomDTO**

```java
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ChatRoomDTO {

    private String roomId;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();
    //WebSocketSession은 Spring에서 Websocket Connection이 맺어진 세션

    public static ChatRoomDTO create(String name){
        ChatRoomDTO room = new ChatRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;
    }
```

채팅방을 만들어 준다. create 함수를 통해서 만들어준다. 



#### ChatRoomRepository

- 채팅방을 생성하고 정보를 조회하는 Repository 생성 Map Collection을 사용하여 채팅방 정보 관리

```java
import org.gorany.community.dto.ChatRoomDTO;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Stream;

@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoomDTO> chatRoomDTOMap;

    @PostConstruct
    private void init(){
        chatRoomDTOMap = new LinkedHashMap<>();
    }

    public List<ChatRoomDTO> findAllRooms(){
        //채팅방 생성 순서 최근 순으로 반환
        List<ChatRoomDTO> result = new ArrayList<>(chatRoomDTOMap.values());
        Collections.reverse(result);

        return result;
    }

    public ChatRoomDTO findRoomById(String id){
        return chatRoomDTOMap.get(id);
    }

    public ChatRoomDTO createChatRoomDTO(String name){
        ChatRoomDTO room = ChatRoomDTO.create(name);
        chatRoomDTOMap.put(room.getRoomId(), room);

        return room;
    }
}
```

#### StompChatController

```java
import lombok.RequiredArgsConstructor;
import org.gorany.community.dto.ChatMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDTO message){
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDTO message){
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
```

- @**MessageMapping** 을 통해 WebSocket으로 들어오는 메세지 발행을 처리한다. Client에서는 prefix를 붙여 "/pub/chat/enter"로 발행 요청을 하면 Controller가 해당 메세지를 받아 처리하는데, 메세지가 발행되면 "/sub/chat/room/[roomId]"로 메세지가 전송되는 것을 볼 수 있다.

- Client에서는 해당 주소를 **SUBSCRIBE**하고 있다가 메세지가 전달되면 화면에 출력한다. 

  이때 /sub/chat/room/[roomId]는 채팅방을 구분하는 값이다.

- 기존의 핸들러 ChatHandler의 역할을 대신 해주므로 핸들러는 없어도 된다.

#### RoomController

- 채팅화면을 보여주기 위한 Controller.

```java
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gorany.community.dto.ChatRoomDTO;
import org.gorany.community.repository.ChatRoomRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Log4j2
public class RoomController {

    private final ChatRoomRepository repository;

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public ModelAndView rooms(){

        log.info("# All Chat Rooms");
        ModelAndView mv = new ModelAndView("chat/rooms");

        mv.addObject("list", repository.findAllRooms());

        return mv;
    }

    //채팅방 개설
    @PostMapping(value = "/room")
    public String create(@RequestParam String name, RedirectAttributes rttr){

        log.info("# Create Chat Room , name: " + name);
        rttr.addFlashAttribute("roomName", repository.createChatRoomDTO(name));
        return "redirect:/chat/rooms";
    }

    //채팅방 조회
    @GetMapping("/room")
    public void getRoom(String roomId, Model model){

        log.info("# get Chat Room, roomID : " + roomId);

        model.addAttribute("room", repository.findRoomById(roomId));
    }
}
```

### View

##### rooms.html

- 채팅창 목록

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

<th:block th:replace="~{/layout/basic :: setContent(~{this :: content})}">
    <th:block th:fragment="content">
        <div class="container">
            <div>
                <ul th:each="room : ${list}">
                    <li><a th:href="@{/chat/room(roomId=${room.roomId})}">[[${room.name}]]</a></li>
                </ul>
            </div>
        </div>
        <form th:action="@{/chat/room}" method="post">
            <input type="text" name="name" class="form-control">
            <button class="btn btn-secondary">개설하기</button>
        </form>
    </th:block>
</th:block>

</html>
```

```javascript
<script th:inline="javascript">
            $(document).ready(function(){

                var roomName = [[${roomName}]];

                if(roomName != null)
                    alert(roomName + "방이 개설되었습니다.");

                $(".btn-create").on("click", function (e){
                    e.preventDefault();

                    var name = $("input[name='name']").val();

                    if(name == "")
                        alert("Please write the name.")
                    else
                        $("form").submit();
                });

            });
        </script>

```

##### room.html

- 채팅방 상세

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

<th:block th:replace="~{/layout/basic :: setContent(~{this :: content})}">
    <th:block th:fragment="content">

        <div class="container">
            <div class="col-6">
                <h1>[[${room.name}]]</h1>
            </div>
            <div>
                <div id="msgArea" class="col"></div>
                <div class="col-6">
                    <div class="input-group mb-3">
                        <input type="text" id="msg" class="form-control">
                        <div class="input-group-append">
                            <button class="btn btn-outline-secondary" type="button" id="button-send">전송</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-6"></div>
        </div>


        <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
        
    </th:block>
</th:block>

</html>

```

``` javascript
<script th:inline="javascript">
            $(document).ready(function(){

                var roomName = [[${room.name}]];
                var roomId = [[${room.roomId}]];
                var username = [[${#authentication.principal.username}]];

                console.log(roomName + ", " + roomId + ", " + username);

                var sockJs = new SockJS("/stomp/chat");
                //1. SockJS를 내부에 들고있는 stomp를 내어줌
                var stomp = Stomp.over(sockJs);

                //2. connection이 맺어지면 실행
                stomp.connect({}, function (){
                   console.log("STOMP Connection")

                   //4. subscribe(path, callback)으로 메세지를 받을 수 있음
                   stomp.subscribe("/sub/chat/room/" + roomId, function (chat) {
                       var content = JSON.parse(chat.body);

                       var writer = content.writer;
                       var str = '';

                       if(writer === username){
                           str = "<div class='col-6'>";
                           str += "<div class='alert alert-secondary'>";
                           str += "<b>" + writer + " : " + message + "</b>";
                           str += "</div></div>";
                           $("#msgArea").append(str);
                       }
                       else{
                           str = "<div class='col-6'>";
                           str += "<div class='alert alert-warning'>";
                           str += "<b>" + writer + " : " + message + "</b>";
                           str += "</div></div>";
                           $("#msgArea").append(str);
                       }

                       $("#msgArea").append(str);
                   });

                   //3. send(path, header, message)로 메세지를 보낼 수 있음
                   stomp.send('/pub/chat/enter', {}, JSON.stringify({roomId: roomId, writer: username}))
                });

                $("#button-send").on("click", function(e){
                    var msg = document.getElementById("msg");

                    console.log(username + ":" + msg.value);
                    stomp.send('/pub/chat/message', {}, JSON.stringify({roomId: roomId, message: msg.value, writer: username}));
                    msg.value = '';
                });
            });
```

- (주석 출처 및 코드 참고: [supawer0728.github.io/2018/03/30/spring-websocket/](https://supawer0728.github.io/2018/03/30/spring-websocket/))

### 결론



- **WebSocket**을 사용하면 Streaming, Polling 보다 실시간에 가깝게 처리할 수 있고 트래픽이 줄어든다.


- **SockJS**를 사용하면 WebSocket을 지원하지 않는 브라우저에서도 WebSocket Emulation을 이용해 웹소켓을 이용하는 것 처럼 동작하게 해준다.


- **STOMP**를 사용하면 Session을 직접 관리하지 않아도 되고, 메세지의 처리 방식이 간편해진다. 





**Reactive Websocket **



Spring5 에서는 websocket 채널에 Reactive 능력을 추가함에 따라 조금 더 유연하게 사용할 수 있도록 지원하였다.

spring5 에서 web flow는 다음과 같다.

![img](https://t1.daumcdn.net/cfile/tistory/99B78C3C5C5F18BA07)

**https://blog.monkey.codes/how-to-build-a-chat-app-using-webflux-websockets-react/**

>1. Spring4에서 웹소켓을 직접 처리하는 방식과 같이 기본적으로 WebsocketHandler를 구현하여 처리해야한다.
>2. handler는 연결이 설정될 때마다, 웹소켓 세션이 제공되며 세션에는 receive 와 send 를 포함하는 flux stream을 갖게 된다.
>3. GMS(UnicastProcessor)를 통해 모든 웹소켓 세션은 연결된다.
>4. GMS를 통해 메세지를 보내야 하며, publisher는 websocket session을 통해 receive한  flux 메세지를 수신한다.
>5. GMS는 모든 웹소켓 세션을 연결하고 있지 않고 가장 최근 생성된 25개의 subscrber만 갖고 있으며, 메세지를 전달한다.



