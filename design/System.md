
## 시스템 설계

- ### 방만들기
    - 서버로 그룹 채팅 방 개설 요청
    - 전달된 url로 클라이언트 접속

        이탈 상태가 확실하지 않음.
    
    - 예외 발생 위험
    - 끊김에도 죽는 경우
    - 가장 아름다운 그림은 다시 접속을 시켜주는 것

-  ### Front-Back 통신 
   -  WebSocket
      -  게임로직처리서버 - Client
      -  채팅서버 - Client (BroadCasting);
   -  영상처리 서버 (P2P)
- ### CI
  - CI 서버
    - AWS서버 
      - Release
      - 배포
  - Docker
  - Jekins

- ### WebRTC
  - Kurrento 사용 예정


- 상기 명시한 바는 관련 스터디 진행 후 변경 될 수 있음
