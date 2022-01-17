# WebSocket

## 1. WebSocket vs socket.io

웹소켓: 양방향 소통을 위한 프로토콜

​	프로토콜: 서로 다른 컴퓨터끼리 소통하기 위한 약속

반면, socket.io는 양방향 통신을 하기 위해 웹소켓 기술을 활용하는 라이브러리

그렇기 때문에 socket.io가 같은 기능을 구현하더라도 약간 느리지만, 많은 편의성을 제공

#### WebSocket

- HTML5 웹 표준 기술
- 매우 빠르게 작동하며 통신할 때 아주 적은 데이터를 이용함
- 이벤트를 단순히 듣고, 보내는 것만 가능함

#### Socket.io

- 표준 기술이 아니며, 라이브러리
- 소켓 연결 실패 시 fallback을 통해 다른 방식으로 알아서 해당 클라이언트와 연결을 시도함
- 방 개념을 이용해 일부 클라이언트에게만 데이터를 전송하는 브로드캐스팅이 가능함

#### 어떤 걸 써야할까?

- 서버에서 연결된 소켓(사용자)들을 세밀하게 관리해야 하는 서비스인 경우에는 Broadcasting  기능이 있는 socket.io를 쓰는게 유지보수 측면에서 훨씬 이점이 많습니다.
- 반면 가상화폐 거래소 같이 데이터 전송이 많은 경우에는 빠르고 비용이 적은 표준 WebSocket을 이요하는게 바람직하겠죠. 실제로 업비트나 바이낸스 소켓 API를 사용해보면 정말 엄청나게 많은 데이터가 들어옵니다.
- socket.io로 구성된 서버에게 소켓 연결을 하기 위해서는 클라이언트측에서 반드시 `socket.io-client`라이브러리를 이용해야 합니다.

출처: https://www.peterkimzz.com/websocket-vs-socket-io/







## 2. WebSocket과 Socket.io

#### WebSocket프로토콜

- WebSocket은 다른 HTTP 요청과 마찬가지로 80번 포트를 통해 웹 서버에 연결한다. 

- HTTP 프로토콜의 버전은 1.1이지만 다음 헤더의 예에서 볼 수 있듯이, Upgrade 헤더를 사용하여 웹 서버에 요청한다.

  ```
  GET /... HTTP/1.1  
  Upgrade: WebSocket  
  Connection: Upgrade  
  ```

- WebSocket 핸드쉐이킹 과정

  - 브라우저는 "Upgrade:WebSocket" 헤더 등과 함께 랜덤하게 생성한 키를 서버에 보낸다. 
  - 웹서버는 이 키를 바탕으로 토큰을 생성한 후 브라우저에 돌려준다.

  > 핸드쉐이킹
  >
  > : 채널에 대한 정상적인 통신이 시작되기 전에 두 개의 실체 간에 확립된 통신 채널의 변수를 동적으로 설정하는 자동화된 협상 과정.
  >
  > 출처: https://ko.wikitrev.com/wiki/Handshaking

- 그 뒤 웹서버와 브라우저는 Protocol Overhead 방식으로 데이터를 주고 받는다.

  > Protocol Overhead 방식
  >
  > : 여러 TCP 커넥션을 생성하지 않고 하나의 80번 포트 TCP 커넥션을 이용하고, 별도의 헤더 등으로 논리적인 데이터 흐름 단위를 이용하여 여러 개의 커넥션을 맺는 효과를 내는 방식

- WebSocket API

  - https://html.spec.whatwg.org/multipage/web-sockets.html



#### 그렇다면 Socket.io는 무엇인가?

 WebSocket은 다가올 미래의 기술이지 아직 인터넷 기업에서 시범적으로라도 써 볼 수 있는 기술이 아니다. WebSocket이 미래의 기술이라면 Socket.io는 현재 바로 사용할 수 있는 기술이다. Socket.io는 JavaScript를 이용하여 브라우저 종류에 상관없이 실시간 웹을 구현할 수 있도록 한 기술이다. 

 Socket.io는 WebSocket, FlashSocket, AJAX Long Polling, AJAX Multi part Streaming, IFrame, JSONP Polling을 하나의 API로 추상화한 것이다. 즉, 브라우저와 웹 서버의 종류와 버전을 파악하여 가장 적합한 기술을 선택하여 사용하는 방식이다. 가령 브라우저에 Flash Plugin v10.0.0 이상(FlashSocket 지원 버전)이 설치되어 있으면 FlashSocket을 사용하고, Flash Plugin이 없으면 AJAX Long Polling 방식을 사용한다.

 개발자가 각 기술을 깊이 이해하지 못하거나 구현 방법을 잘 알지 못해도 사용할 수 있다. Web Socket과 달리 Socket.io는 표준 기술이 아니고 Node.js 모듈이자 오픈소스이다. 



#### Socket.io를 사용해 보자

- Socket.io는 브라우저에서는 JavaScript, 서버에서는 Node.js 를 사용한다.

- Socket.io를 사용하려면 다음과 같이 NPM을 이용하여 Socket.io를 웹 서버에 설치한다.

  > npm install socket.io

- 설치 후 아래와 같이 서버 스크립트를 작성한다.

  ```
  // 80 포트로 소켓을 연다
  var io = require('socket.io').listen(80);
  
  // connection이 발생할 때 핸들러를 실행한다.
  io.sockets.on('connection', function (socket) {  
  // 클라이언트로 news 이벤트를 보낸다.
      socket.emit('news', { hello: 'world' });
  
  // 클라이언트에서 my other event가 발생하면 데이터를 받는다.
  socket.on('my other event', function (data) {  
          console.log(data);
      });
  });
  ```

- 작성한 스크립트를  nohup 등을 이용하여 백그라운드로 실행한다. 

  - nohup을 사용하면 hang-up signal이 발생해도 스크립트의 동작이 멈추지 않는다.

  ```
  nohup node ./server.js &  
  ```

- 클라이언트는 Socket.io 패키지에 있는 클라이언트 스크립트를 이용하여 아래와 같이 작성한다.

  ```javascript
  <script src="/socket.io/socket.io.js"></script>  
  <script>  
  // localhost로 연결한다.
  var socket =  
    io.connect('http://localhost');
  
  // 서버에서 news 이벤트가 일어날 때 데이터를 받는다.
  socket.on('news',  
    function (data) {
      console.log(data);
    //서버에 my other event 이벤트를 보낸다.
      socket.emit('my other event', 
        { my: 'data' });
  });
  </script>  
  ```

- io.Socket() 메서드를 호출하면 웹 서버(Node.js를 이용하는 웹 서버)와 연결한다. 이때 서버로부터 sessionid 정보와 timeout 정보를 받고, 브라우저의 WebSocket 지원 여부, FlashSocket 지원 여부를 보내고 크로스 도메인 설정 정보 등을 주고 받은 후 적합한 실시간 웹 방식을 선택한다.

- Socket.io

  - 공식 문서: https://socket.io/

  - Socket.io 깃허브: https://github.com/socketio/socket.io



출처: https://d2.naver.com/helloworld/1336

