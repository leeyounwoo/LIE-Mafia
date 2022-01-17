## WebRTC 

### WebRTC란?

- 실시간 웹 커뮤니케이션
- 동영상, 음성, 일반 데이터를 피어 간에 전송하도록 지원하므로 개발자가 강력한 음성 및 영상 커뮤니케이션 솔루션을 빌드 가능
- 개방형 웹 표준으로 구현
- 모든 주요 브라우저에서 일반 JavaScript API로 제공
- Google에서 제공하는 codelab이 있음 
- https://codelabs.developers.google.com/codelabs/webrtc-web/#0에서 튜토리얼 제공



### WebRTC 표준 

- 미디어 캡처 기기 
  - navigator.mediaDevices.getUserMedia() 
    - 카메라와 마이크 MediaStreams 캡처 하기 위해서 사용
  - navigator.mediaDevices.getDisplayMedia()
    - 화면 녹화에 사용
- P2P 연결
  - RTCPeerConnection 
    - P2P 연결 처리 함
    - WebRTC에서 두 피어 간의 연결을 설정 제어 하기 위한 중심점

### 미디어 기기 시작하기

- navigator.mediaDevices 객체룰 통해 Javascript로 액세스 할 수 있음
- 모든 기기를 열거하고 기기 변경사항을 수신 대기한 다음 기기를 열어 미디어 스트림을 가져 올 수 있음


- getUserMedia()을 통한 권한 요청 트리거 , `MediaStreamConstraints` 객체 열어줌 MediaStream를 사용

  프라미스를 해결함  

- 권한이 거부 될 시 PermissionDeniedError 이 발생

- 일치하는 기기가 연결되어 있지 않으면 NotFoundError이 발생함

### 미디어 기기 쿼리

- enumberateDevices() 
  - 각 미디어 기기를 설명하는 MeidaDevicesInfo 배열로 확인되는 promise가 반환됨 
  - MediaDevicesInfo 다음 속성을 포함하는 kind 다음 값으로 바뀜 
  - autoinput, audiooutput , videoinput 이는 어떤 미디어 기기 유형인지를 나타냄

### 기기 변경사항 수신 대기

- devicechange 이벤트의 리스너를 navigator.mediaDevices에 추가하면 됨

```javascript
// Updates the select element with the provided set of cameras
function updateCameraList(cameras) {
    const listElement = document.querySelector('select#availableCameras');
    listElement.innerHTML = '';
    cameras.map(camera => {
        const cameraOption = document.createElement('option');
        cameraOption.label = camera.label;
        cameraOption.value = camera.deviceId;
    }).forEach(cameraOption => listElement.add(cameraOption));
}

// Fetch an array of devices of a certain type
async function getConnectedDevices(type) {
    const devices = await navigator.mediaDevices.enumerateDevices();
    return devices.filter(device => device.kind === type)
}

// Get the initial set of cameras connected
const videoCameras = getConnectedDevices('videoinput');
updateCameraList(videoCameras);

// Listen for changes to media devices and update the list accordingly
navigator.mediaDevices.addEventListener('devicechange', event => {
    const newCameraList = getConnectedDevices('video');
    updateCameraList(newCameraList);
});
```

### 미디어 제약조건

- MediaStreamConstraints 이 매개변수를 매개변수로 전달하여  getUserMedia() 특정 요구사항에 맞는 미디어 기기를 열 수 있음.
- getUserMedia() API를 사용하는 애플리케이션은 먼저 기존 기기를 확인한 다음 deviceId 제약 조건을 사용하여 정확한 기기와 일치하는 제약조건을 지정하는 것이 좋음
- 마이크에서 에코 취소 기능을 사용 설정하거나 카메라에서 동영상의 특정 너비나 높이를 설정 가능

```javascript
async function getConnectedDevices(type) {
    const devices = await navigator.mediaDevices.enumerateDevices();
    return devices.filter(device => device.kind === type)
}

// Open camera with at least minWidth and minHeight capabilities
async function openCamera(cameraId, minWidth, minHeight) {
    const constraints = {
        'audio': {'echoCancellation': true},
        'video': {
            'deviceId': cameraId,
            'width': {'min': minWidth},
            'height': {'min': minHeight}
            }
        }

    return await navigator.mediaDevices.getUserMedia(constraints);
}

const cameras = getConnectedDevices('videoinput');
if (cameras && cameras.length > 0) {
    // Open first available video camera with a resolution of 1280x720 pixels
    const stream = openCamera(cameras[0].deviceId, 1280, 720);
}
```



### 로컬 재생

- MediaStream가 있으면 기기를 동영상 또는 오디오 요소에 할당하여 스트림을 로컬에서 재생할 수 있음
- autoplay -> 요소에 할당된 새 스트림이 자동으로 재생됨
- playsinline -> 모바일 브라우저에서 동영상을 전체 화면뿐 아니라 인라인으로 재생 가능
- 일시중지시킬 수 없는 경우가 아니라면 실시간 스트림에도 controls="false"  사용하는 것이 좋음



브라우저에서 지원하는 모든 카메라와 마이크는 

`navigator.mediaDevices` 객체를 통해서 관리됨 

-> 기기의 목록 검색 변경사항을 반영 가능함



- MediaTrackConstraint 객체, 즉 오디오용과 동영상용으로 정의됨. 

- 객체의 속성은 ConstraintLong, ConstraintBoolean, ConstraintDouble 또는 ConstraintDOMString 유형임

- 특정 값(숫자, 부울 또는 문자열) ,범위(최소 및 최대 값이 있는 `LongRange` 또는 `DoubleRange`) 

- ideal 또는 exact인 값이 구체적인 경우 브라우저가 최대한 근접한 값을 선택 

- 범위의 경우 해당 범위 내 최적의 값이 사용됨.

- exact가 지정되면 이 제약조건과 정확하게 일치하는 미디어 스트림만 반환

  ```javascript
  // Camera with a resolution as close to 640x480 as possible
  {
      "video": {
          "width": 640,
          "height": 480
      }
  }
  ```

  ​


- `MediaTrackSettings`를 반환하는 `MediaStreamTrack.getSettings()`를 호출
- applyConstraints()을 호출하여 열였던 미디어 기기에서 트랙의 제약 조건을 업데이트 가능


- 기존 스트림을 닫지 않고도 애플리케이션이 미디어 기기를 다시 구성 가능

### Display Media API 

- getDisplayMedia() 함수 MediaStream을 리턴 
- 스플레이의 콘텐츠(또는 그 일부)를 여는 용도로 사용

``` javascript
{
    video: {
        cursor: 'always' | 'motion' | 'never',
        displaySurface: 'application' | 'browser' | 'monitor' | 'window'
    }
}
```

cursor와 displaySurface를 사용 해준다. 

### MediaStreamTrack

- audio, video인 kind 속성이 있음 
- 속성을 나타내는 미디어 종류
- enabled 속성을 전환하면 각 트랙을 음소거 가능
- 트랙은 RTCPeerConnection에서 나오고 boolean형인 remote가 있음 



### WebRTC의 통신 방식

- P2P(Peer-To-Peer) 프로토콜
- 동영상, 오디오 또는 임의의 바이너리 데이터를 주고 받음
- 두 클라이언트 모두 ICE 서버를 제공 해야 함 
- STUN 또는 TURN 서버로 구성됨 




#### ICE란?

두 개의 단말기(Client여도 상관 없음) 서로 통신 할 수 있는 최적의 경로를 찾을 수 있도록 도와주는 프레임 워크임 

- STUN 과 TURN을 활용하는 프레임 워크로 SDP 제안 및 수락 모델(Offer / Answer Model) 에 적용 가능



#### STUN란?

- 클라이언트-서버 프로토콜임
- STUN 클라이언트는 사설망에 위치 , STUN 서버는 인터넷망에 위치함
- STUN 클라이언트는 자신의 공인 IP 주소를 사전에 확인하기 위해 STUN서버에 요청, 
- STUN 서버는 STUN 클라이언트가 사용하는 공인 IP 주소를 응답함.



#### TURN란?

- NAT 환경에 단말이 릴레이 서버를 이용하여 통신하게 함. 

- TURN 클라이언트는 사설망에 위치하고 TURN 서버는 인터넷망에 위치

- TURN 클라이언트는 통화를 할 피어들과 직접 통신하는 것이 아니라 릴레이 서버 역할을 하는 

  TURN 서버를 경유함. 

- TURN 클라이언트는 사설 주소 포함된 TURN 메시지를 TURN 서버로 전송

- TURN 서버는 TURN 메시지에 포함된 사설 주소 와 TURN 메시지 패킷의 공인 주소인 layer 3 

  IP 주소와 Layer 4 UDP 포트 넘버를 차이를 확인함. 

- TURN 서버는 TURN 클라이언트의 공인 주소로 응답



#### STUN과 TURN 초간단 정리

- STUN
  -  단말이 자신의 공인 IP주소와 포트를 확인하는 과정에 대한 프로토콜 
  -  서버는 사설 주소와 공인 주소를 바인딩
- TURN
  - 단말이 패킷을 릴레이 시켜 줄 서버를 확인하는 과정에 대한 프로토콜
  - 서버는 릴레이 주소를 할당



>  참조 - https://brunch.co.kr/@linecard/156



#### WebRTC 통신 방법

-  각 피어 연결은 RTCPeerConnection 객체에 의해 처리 됨
-  객체 생성시 RTCConfiguration을 매개변수로 사용함
-  RTCConfiguration 객체는 피어 연결이 설정되는 방식을 정의하며 사용할 ICE 서버에 대한 정보를 포함함



#### RTCPeerConnection

- 생성 시 호출 피어 또는 수신 피어에 따라 SDP 오퍼 또는 답변 만들어짐 
- 다른 채널을 통해 원격 피어로 전송해야 함
- 신호
  - SDP 객체를 원격 피어에 전달하는 것
  - WebRTC 사양이 적용 X
- 피어 연결 설정을 시작 
  - RTCPeerConnection 객체를 만든 다음 createOffer() 호출하여 `RTCSessionDescription` 객체를 만듦
  - setLocalDescription()를 사용하여 로컬 설명으로 설정되며 신호 채널을 통해 수신측으로 전송됨 
  - 수신 세션으로부터 제공된 세션 설명에 대한 응답이 수신되는 경우 신호 채널에 대한 리스너를 설정
- RTCPeerConnection 인스턴스를 만들기 전에 들어오는 쿠폰을 기다림 
  - `setRemoteDescription()`를 사용하여 수신된 오퍼를 설정
  - createAnswer() 수신된 호출에 대한 답변 생성
  - setLocalDescription()를 사용하여 로컬 설명으로 설정된 후 신호 서버로 전송됨
- ICE 서버
  - WebRTC API는 STUN 및 TURN을 모두 직접 지원
  - `RTCPeerConnection` 객체 구성에 ICE 서버 제공
- Trickle ICE
  - 일반적으로 'trickle ice' 기법을 사용하여 각 ICE 후보가 발견될 때 원격 피어에 전송하는 것이 훨씬 더 효율적
  - 어 연결 설정 시간이 크게 줄어들고 지연을 최소화
  - ICE 를 수집하려면 `icecandidate` 이벤트 리스너 추가
  - `TCPeerConnectionIceEvent`에는 원격 피어로 전송되어야 하는 새 후보를 나타내는 `candidate` 속성이 포함
- 연결 
  - ICE 후보가 수신되면 피어 연결 상태가 최종적으로 연결된 상태로 변경될 것으로 예상
  - `RTCPeerConnection`에 connectionstatechange 이벤트를 수신 대기하는 리스너 추가

### 원격 트랙 추가

- 원격 트랙을 수신하기 위해 `track` 이벤트를 수신 대기하는 로컬 `RTCPeerConnection`에 리스너를 등록
- `RTCTrackEvent에서 MediaStream.id` 값이 동일한 `MediaStream`   피어의 해당 로컬 스트림이 MediaStream 객체 배열에 넣음



### 데이터 송수신 

- RTCPeerConnection 객체에서 createDataChannel()을 이용 dataChannel 생성
- 원격 연결은 datachannel 이벤트를 수신하여 데이터 채널로 만듦
- open 이벤트를 통해서 채널을 열음 , close 이벤트를 통해서 채널을 닫음
- send() 함수를 통해서 message를 보냄
- message는 문자열, `Blob`, `ArrayBuffer` 또는 `ArrayBufferView`로 구성됨
- 보내진 것을 message라는 이벤트를 통해서 수신 받음



### TURN 서버

- RTCConfiguration만 올바르게 있으면 됨
- 서버에 대한 액세스 보안을 위해 `username` 및 `credentials` 속성을 지원
- urls에 domain:포트번호 로 구성




> 추후 스터디 진행 계획
>
> - 구글 코드 랩을 이용 직접 하나 씩 구현해보기
> - WebRTC 개념을 넘어서서 Front와 back과의 통신에 구현
> - https://codelabs.developers.google.com/codelabs/webrtc-web/#0
> - https://github.com/webrtc 깃헙에 있는 test 직접 구현 
> - KITE test engine에 대해서 공부 진행  
> - Firebase와의 연동 진행 

​	


