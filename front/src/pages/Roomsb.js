import React, { useEffect } from "react";
import kurentoUtils from "kurento-utils";
import { useLocation, useParams } from "react-router-dom";
import { Container, Row, Col } from "react-bootstrap";
import styles from "./RoomGrid.module.css";
import ws from "./websocket";
const PARTICIPANT_MAIN_CLASS = "participant main";
// 다른 사용자
const PARTICIPANT_CLASS = "participant";

function sendMessage(message) {
  var jsonMessage = JSON.stringify(message);

  console.log("Sending message: " + jsonMessage);

  ws.send(jsonMessage);
}

function Participant(name, positionID) {
  console.log(555, name, positionID);
  this.name = name;
  // 비디오를 넣을 ID(positionID): 참여자 배열의 길이

  // 비디오를 넣을 div(inputContainer): id가 positionID인 div
  const inputContainer = document.getElementById(positionID);
  // 대체 이미지(alterImg): inputConiner 안에 있던 대체 이미지
  const alterImg = inputContainer.querySelector("img");
  // 대체 이미지가 있는 경우엔 이미지 삭제하고 영상 추가
  if (alterImg) {
    // 대체 이미지 삭제
    inputContainer.removeChild(alterImg);
    // container: 사용자 이름과 사용자 영상을 저장하는 div
    var container = document.createElement("div");
    // 로컬 사용자의 영상은 class={PARTICIPANT_MAIN_CLASS}
    container.className = isPresentMainParticipant()
      ? PARTICIPANT_CLASS
      : PARTICIPANT_MAIN_CLASS;
    // 각 container의 id는 닉네임
    container.id = name;

    // 영상 태그 만들기 (스타일, video태그의 id, 설정)
    var video = document.createElement("video");
    video.style.width = 400 + "px";
    video.id = "video-" + name;
    video.autoplay = true;
    video.controls = false;
    container.appendChild(video);

    var span = document.createElement("span");
    container.appendChild(span);

    inputContainer.appendChild(container);

    span.appendChild(document.createTextNode(name));
  }

  this.getElement = function () {
    return container;
  };

  this.getVideoElement = function () {
    return video;
  };

  function isPresentMainParticipant() {
    return document.getElementsByClassName(PARTICIPANT_MAIN_CLASS).length !== 0;
  }

  this.offerToReceiveVideo = function (error, offerSdp, wp) {
    if (error) return console.error("sdp offer error");

    console.log("Invoking SDP offer callback function");

    var msg = { id: "receiveVideoFrom", sender: name, sdpOffer: offerSdp };

    sendMessage(msg);
  };

  this.onIceCandidate = function (candidate, wp) {
    console.log("Local candidate" + JSON.stringify(candidate));

    var message = {
      id: "onIceCandidate",

      candidate: candidate,

      name: name,
    };

    sendMessage(message);
  };

  Object.defineProperty(this, "rtcPeer", { writable: true });

  this.dispose = function () {
    console.log("Disposing participant " + this.name);

    this.rtcPeer.dispose();

    container.parentNode.removeChild(container);
  };
}

function Room() {
  // const ws = new WebSocket("wss://3.37.1.251:8443/groupcall");
  // const ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
  // 자기 자신
  let { roomId } = useParams();
  // 사용자 닉네임 저장하는 배열
  const subscribers_name = [];
  const subscribers_video = [];
  // 방 번호
  var room = roomId;
  // 닉네임
  const location = useLocation();
  const nickName = location.state.nickName;
  var name = nickName;
  const authority = location.state.authority;
  let level = authority;

  ws.onmessage = function (message) {
    console.log(message);
    var parsedMessage = JSON.parse(message.data);

    console.info("Received message: " + message.data);

    switch (parsedMessage.id) {
      // 현재 서버에 연결된 사용자 정보를 가져온다.
      case "existingParticipants":
        onExistingParticipants(parsedMessage);
        break;

      case "newParticipantArrived":
        onNewParticipant(parsedMessage);
        break;

      case "participantLeft":
        onParticipantLeft(parsedMessage);
        break;

      case "receiveVideoAnswer":
        receiveVideoResponse(parsedMessage);
        break;

      case "iceCandidate": {
        if (parsedMessage.name === nickName) {
          break;
        }
        console.log(parsedMessage, subscribers_video, subscribers_name);
        subscribers_video[
          subscribers_name.indexOf(parsedMessage.name)
        ].rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
          if (error) {
            console.error("Error adding candidate: " + error);
            return;
          }
        });

        break;
      }
      default:
        console.error("Unrecognized message", parsedMessage);
    }
  };

  function onNewParticipant(request) {
    receiveVideo(request.name);
  }

  function receiveVideoResponse(result) {
    console.log(subscribers_name);
    console.log(subscribers_video);
    console.log(result.name);
    subscribers_video[
      subscribers_name.indexOf(result.name)
    ].rtcPeer.processAnswer(
      result.sdpAnswer,

      function (error) {
        if (error) return console.error(error);
      }
    );
  }

  function onExistingParticipants(msg) {
    if (!subscribers_name.includes(name)) {
      var constraints = {
        audio: false,
        video: {
          mandatory: {
            maxWidth: 320,

            maxFrameRate: 15,

            minFrameRate: 15,
          },
        },
      };
      console.log(name + " registered in room " + room);
      const participant = new Participant(name, subscribers_name.length);

      subscribers_name.push(name);
      subscribers_video.push(participant);

      var video = participant.getVideoElement();

      var options = {
        localVideo: video,

        mediaConstraints: constraints,
        onicecandidate: participant.onIceCandidate.bind(participant),
      };

      participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
        options,

        function (error) {
          if (error) {
            return console.error(error);
          }
          this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        }
      );
    }
    Object.keys(msg.data.participants)
      .filter((a) => {
        return !subscribers_name.includes(a);
      })
      .forEach(receiveVideo);
  }

  function receiveVideo(sender) {
    const participant = new Participant(sender, subscribers_name.length);

    subscribers_name.push(sender);
    subscribers_video.push(participant);
    console.log(subscribers_name, subscribers_video);

    var video = participant.getVideoElement();

    var options = {
      remoteVideo: video,
      onicecandidate: participant.onIceCandidate.bind(participant),
      configuration: {
        iceServers: [
          {
            urls: "turn:3.38.118.187:3478?transport=udp",

            username: "ssafy",

            credential: "1234",
          },
        ],
      },
    };

    participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
      options,
      function (error) {
        if (error) {
          return console.error(error);
        }

        this.generateOffer(participant.offerToReceiveVideo.bind(participant));
      }
    );
  }

  function onParticipantLeft(request) {
    console.log("Participant " + request.name + " left");

    const requestNameIndex = subscribers_name.indexOf(request.name);
    const participant = subscribers_video[requestNameIndex];

    participant.dispose();

    subscribers_name.slice(requestNameIndex);
    subscribers_video.slice(requestNameIndex);
  }

  useEffect(() => {
    console.log({ nickName });

    var message = {
      id: nickName === "leader" ? "create" : "join",

      username: nickName,

      roomId: roomId,
    };
    // console.log("ㅎㅇ");
    sendMessage(message);

    ws.onclose = (event) => {
      console.log(event);
    };

    ws.onerror = (error) => {
      console.log(error);
    };
    return function cleanup() {
      console.log(1);
      ws.close();
    };
  }, []);

  return (
    <Container fluid>
      <Row>
        <Col>
          <div id="0">
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          </div>
        </Col>
        <Col>
          <div id="1">
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          </div>
        </Col>
        <Col>
          <div id="2">
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          </div>
        </Col>
      </Row>
      <Row>
        <Col></Col>
        <Col>
          <h1>공지사항 들어오는 공간</h1>
          <div className={styles.infobox}>
            <div className={styles.infotextwrap}>
              <span className={styles.infotext}>메세지</span>
            </div>
            <div className={styles.policetextwrap}>
              <span className={styles.policetext}>서브메세지</span>
            </div>
          </div>
        </Col>
        <Col></Col>
      </Row>
      <Row>
        <Col>
          <div id="3">
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          </div>
        </Col>
        <Col>
          <div id="4">
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          </div>
        </Col>
        <Col>
          <div id="5">
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          </div>
        </Col>
      </Row>
    </Container>
  );
}

export default Room;
