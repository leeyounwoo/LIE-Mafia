import { useEffect, useState } from "react";
import VideoRoom from "../components/VideoRoom/videoRoom";
import WaitingNav from "../components/Navbar/navbar";
import Footer from "../components/Footer/footer";
// import Chat from "../components/Chat/chat";
import { WebRtcPeer } from "kurento-utils";
import styled from "styled-components";
import Home from "../components/Home/home";
import Chat from "../components/Chat/Chat";

const StyledContainer = styled.div`
  height: 100vh;
`;

const Main = styled.div`
  display: flex;
  flex-wrap: nowrap;
`;

function Game() {
  const ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
  // 참여자가 변화할 때마다 리렌더링
  const [participantsName, setParticipantsName] = useState([]);
  const [participantsVideo, setParticipantsVideo] = useState([]);
  const username = `user${Math.random().toString(36).substr(2, 11)}`;
  const [authority, setAuthority] = useState([]);
  const tempParticipantsName = participantsName;
  const tempParticipantsVideo = participantsVideo;
  const [roomId, setRoomId] = useState(
    window.location.pathname.split("/").pop()
  );

  // 서버쪽으로 메세지를 보내는 함수
  const sendMessage = (message) => {
    const jsonMessage = JSON.stringify(message);
    console.log("Sending message: " + jsonMessage);
    ws.send(jsonMessage);
  };

  const receiveVideo = (participant) => {
    let user = {
      name: participant.username,
      sessionId: participant.sessionId,
      ready: participant.ready,
      authority: participant.authority,
      type: "remote",
      rtcPeer: null,
    };

    tempParticipantsName.push(participant.username);
    tempParticipantsVideo.push(user);

    var video = document.getElementById(
      `video-${tempParticipantsVideo.length - 1}`
    );

    const options = {
      remoteVideo: video,
      onicecandidate: (candidate) => {
        console.log("Remote candidate" + JSON.stringify(candidate));
        const message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: participant.username,
        };
        sendMessage(message);
      },
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

    user.rtcPeer = WebRtcPeer.WebRtcPeerRecvonly(options, function (error) {
      if (error) {
        return console.log(error);
      }
      this.generateOffer((err, offerSdp, wq) => {
        if (err) return console.err("sdp offer error");
        console.log("Invoking SDP offer callback function");
        let msg = {
          id: "receiveVideoFrom",
          sender: participant.username,
          sdpOffer: offerSdp,
        };
        sendMessage(msg);
      });
    });
  };

  const onExistingParticipants = (msg) => {
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

    let user = {
      name: msg.user.username,
      sessionId: msg.user.sessionId,
      ready: msg.user.ready,
      authority: msg.user.authority,
      type: "local",
      rtcPeer: null,
    };

    setAuthority(msg.user.authority);
    tempParticipantsName.push(msg.user.username);
    tempParticipantsVideo.push(user);
    setRoomId(msg.data.roomId);

    console.log(msg.user.username + " registered in room " + roomId);

    var video = document.getElementById(
      `video-${tempParticipantsVideo.length - 1}`
    );

    const options = {
      localVideo: video,
      mediaConstraints: constraints,
      onicecandidate: (candidate) => {
        console.log("Local candidate" + JSON.stringify(candidate));
        const message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: msg.user.username,
        };
        sendMessage(message);
      },
    };

    user.rtcPeer = WebRtcPeer.WebRtcPeerSendonly(options, function (error) {
      if (error) {
        return console.log(error);
      }
      this.generateOffer((err, offerSdp, wq) => {
        if (err) return console.err("sdp offer error");
        console.log("Invoking SDP offer callback function");
        let message = {
          id: "receiveVideoFrom",
          sender: msg.user.username,
          sdpOffer: offerSdp,
        };
        sendMessage(message);
      });
    });

    Object.entries(msg.data.participants).forEach(
      ([msgUserName, participant]) => {
        receiveVideo(participant);
      }
    );
  };

  const onNewParticipant = (msg) => {
    receiveVideo(Object.values(msg.data)[0]);
    updateParticipants();
  };

  const onReceiveVideoAnswer = (msg) => {
    tempParticipantsVideo[
      tempParticipantsName.indexOf(msg.name)
    ].rtcPeer.processAnswer(msg.sdpAnswer);
  };

  const onAddIceCandidate = (msg) => {
    tempParticipantsVideo[
      tempParticipantsName.indexOf(msg.name)
    ].rtcPeer.addIceCandidate(msg.candidate);
  };

  const updateParticipants = () => {
    setParticipantsName([]);
    setParticipantsVideo([]);
    setParticipantsName(tempParticipantsName);
    setParticipantsVideo(tempParticipantsVideo);
  };

  useEffect(() => {
    updateParticipants();
  });

  // 컴포넌트가 처음 렌더링 됐을 때만 웹소켓 연결
  useEffect(() => {
    ws.onopen = () => {
      console.log("연결");
      let message = "";
      // 방장일 땐 create 메세지
      if (roomId === "0") {
        message = {
          id: "create",
          username: username,
        };
        // 참여자일 땐 join 메세지
      } else {
        message = {
          id: "join",
          username: username,
          roomId: roomId,
        };
      }
      if (message !== "") {
        sendMessage(message);
      }
    };

    ws.onmessage = (message) => {
      var parsedMessage = JSON.parse(message.data);
      console.info("Received message: " + message.data);

      switch (parsedMessage.id) {
        // 새로 방에 참여한 사용자에게 오는 메세지
        // 새로 참여한 사용자 정보 + 기존에 있던 사용자 정보 + 방 정보
        case "existingParticipants":
          onExistingParticipants(parsedMessage);
          break;

        // 기존에 있던 사용자에게 오는 메세지
        // 새로 참여한 사용자 정보
        case "newParticipant":
          onNewParticipant(parsedMessage);
          break;

        // receive 함수에서 보낸 receiveVideoFrom 메세지에 대한 대답
        case "receiveVideoAnswer":
          onReceiveVideoAnswer(parsedMessage);
          break;

        // onIceCandidate 메세지에 대한 대답
        case "iceCandidate":
          onAddIceCandidate(parsedMessage);
          break;

        default:
          console.error("Unrecognized message", parsedMessage);
      }
    };

    ws.onerror = (error) => {
      console.log(error);
      alert(error);
    };

    ws.onclose = (event) => {
      console.log(event);
    };

    // 컴포넌트가 파괴될 때 웹소켓 통신 닫음
    return function cleanup() {
      ws.close();
    };
  }, []);

  const [join, setJoin] = useState(false);
  const onBtnClick = () => {
    setJoin(true);
  };

  const onClickCamera = () => {
    participantsVideo[0].rtcPeer.videoEnabled =
      !participantsVideo[0].rtcPeer.videoEnabled;
  };

  const onClickMute = () => {
    participantsVideo[0].rtcPeer.audioEnabled =
      !participantsVideo[0].rtcPeer.audioEnabled;
  };

  return (
    <StyledContainer>
      {!join && <Home onBtnClick={onBtnClick} />}
      {join && (
        <div>
          <WaitingNav roomId={roomId} />
          <Main>
            <header className="App-header">
              <>
                <h1>참가자 수: {participantsVideo.length}</h1>
                <VideoRoom
                  participantsVideo={participantsVideo}
                  participantsName={participantsName}
                ></VideoRoom>
              </>
            </header>
            {/* <Chat /> */}
          </Main>
          {/* <Chat /> */}
          <Footer
            authority={authority}
            roomId={roomId}
            username={username}
            localUserVideo={participantsVideo[0]}
            onClickCamera={onClickCamera}
            onClickMute={onClickMute}
          />
        </div>
      )}
    </StyledContainer>
  );
}
export default Game;
