import { useEffect, useState } from "react";
import VideoRoom from "../components/VideoRoom/videoRoom";
import FinalArgument from "../components/VideoRoom/finalArgument";
import WaitingNav from "../components/Navbar/navbar";
import GameNav from "../components/Navbar/gameNav";
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
  const ws = new WebSocket("ws://52.79.223.21:8001/ws");
  // 게임 참여자
  const [participantsName, setParticipantsName] = useState([]);
  const [participantsVideo, setParticipantsVideo] = useState([]);
  const tempParticipantsName = participantsName;
  const tempParticipantsVideo = participantsVideo;
  // 로컬 사용자
  const username = `User${Math.random().toString(36).substr(2, 11)}`;
  const [authority, setAuthority] = useState([]);
  const [roomId, setRoomId] = useState(
    window.location.pathname.split("/").pop()
  );
  // 지목받은 사용자 (최후의 변론)
  const [selectedUserName, setSelectedUserName] = useState(participantsName[0]);
  const [selectedUserVideo, setSelectedUserVideo] = useState(
    participantsVideo[0]
  );
  // 최후의 변론 그리드
  const [isExcutionGrid, setIsExcutionGrid] = useState(false);

  // 게임 생존자
  const [votersName, setVotersName] = useState(participantsName);

  // 투표 가능한 상태
  const [isVotable, setIsVotable] = useState(false);

  // 밤투표 상황인지 아닌지
  const [isNight, setIsNight] = useState(false);

  // 게임 진행 상태
  const [isGameStart, setIsGameStart] = useState(false);

  // 서버쪽으로 메세지를 보내는 함수
  const sendMessage = (message) => {
    const newMessage = { eventType: "connection", data: message };
    const jsonMessage = JSON.stringify(newMessage);
    console.log("Sending message: " + jsonMessage);
    ws.send(jsonMessage);
  };

  // 비디오를 등록하는 함수
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
      // onicecandidate: (candidate) => {
      //   console.log("Remote candidate" + JSON.stringify(candidate));
      //   const message = {
      //     id: "onIceCandidate",
      //     candidate: candidate,
      //     name: participant.username,
      //   };
      //   sendMessage(message);
      // },
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

  // 처음 사용자가 방에 입장하면 본인을 등록하고 기존 사용자를 등록
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

    // 임시
    setSelectedUserName(user.name);
    setSelectedUserVideo(user);

    console.log(msg.user.username + " registered in room " + roomId);

    var video = document.getElementById(
      `video-${tempParticipantsVideo.length - 1}`
    );

    const options = {
      localVideo: video,
      mediaConstraints: constraints,
      // onicecandidate: (candidate) => {
      //   console.log("Local candidate" + JSON.stringify(candidate));
      //   const message = {
      //     id: "onIceCandidate",
      //     candidate: candidate,
      //     name: msg.user.username,
      //   };
      //   sendMessage(message);
      // },
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

  // 방에 새로운 사용자가 입장했을 때 기존 사용자는 새로운 사용자를 등록
  const onNewParticipant = (msg) => {
    receiveVideo(Object.values(msg.data)[0]);
    updateParticipants();
  };

  // rtcPeer Answer
  const onReceiveVideoAnswer = (msg) => {
    tempParticipantsVideo[
      tempParticipantsName.indexOf(msg.name)
    ].rtcPeer.processAnswer(msg.sdpAnswer);
  };

  // rtcPeer IceCandidate
  const onAddIceCandidate = (msg) => {
    tempParticipantsVideo[
      tempParticipantsName.indexOf(msg.name)
    ].rtcPeer.addIceCandidate(msg.candidate);
  };

  // tempParticipant와 participant 동기화
  const updateParticipants = () => {
    setParticipantsName([]);
    setParticipantsVideo([]);
    setParticipantsName(tempParticipantsName);
    setParticipantsVideo(tempParticipantsVideo);
  };

  // 최후의 변론
  // 지목된 사용자 지정
  const onFinalSpeech = (msg) => {
    const selectedUserIndex = participantsName.indexOf(msg.result);
    setSelectedUserName(participantsName[selectedUserIndex]);
    setSelectedUserVideo(participantsVideo[selectedUserIndex]);
  };

  // 사형 투표
  // 해야 할 일: 타이머 설정
  const onExecutionVote = (msg) => {
    console.log("onExecutionVote");
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

      // 최후의 변론 또는 사형 투표일 땐 사형투표 그리드
      // 그 외엔 일반 게임 그리드
      if (
        parsedMessage.id === "finalspeech" ||
        parsedMessage.id === "executionvote"
      ) {
        setIsExcutionGrid(true);
      } else {
        setIsExcutionGrid(false);
      }

      // 아침 투표, 사형 투표, 밤 투표일 땐 투표가능
      // 그 외엔 투표 불가능
      if (
        parsedMessage.id === "executionvote" ||
        parsedMessage.id === "morningvote" ||
        parsedMessage.id === "nightvote"
      ) {
        setIsVotable(true);
      } else {
        setIsVotable(false);
      }

      // 밤투표일 땐 밤
      // 그 외엔 낮
      if (parsedMessage.id === "nightvote") {
        setIsNight(true);
      } else {
        setIsNight(false);
      }

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

        // 최후의 변론
        case "finalspeech":
          onFinalSpeech(parsedMessage);
          break;

        // 사형 투표
        case "executionvote":
          onExecutionVote(parsedMessage);
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

  // 카메라 켜고 끄기
  const onClickCamera = () => {
    participantsVideo[0].rtcPeer.videoEnabled =
      !participantsVideo[0].rtcPeer.videoEnabled;
  };

  // 마이크 켜고 끄기
  const onClickMute = () => {
    participantsVideo[0].rtcPeer.audioEnabled =
      !participantsVideo[0].rtcPeer.audioEnabled;
  };

  return (
    <StyledContainer>
      {!join && <Home onBtnClick={onBtnClick} />}
      {join && (
        <div>
          {/* 게임 진행 */}
          {isGameStart && (
            <div>
              <GameNav />
              <header>
                {/* 최후의 변론 X */}
                {!isExcutionGrid && (
                  <VideoRoom
                    isNight={isNight}
                    isVotable={isVotable}
                    participantsVideo={participantsVideo}
                    participantsName={participantsName}
                  />
                )}
                {/* 최후의 변론 */}
                {isExcutionGrid && (
                  <FinalArgument
                    isVotable={isVotable}
                    selectedUserName={selectedUserName}
                    selectedUserVideo={selectedUserVideo}
                    votersName={votersName}
                    participantsName={participantsName}
                    participantsVideo={participantsVideo}
                  />
                )}
              </header>
            </div>
          )}
          {/* 게임 진행 X (게임 시작 전) */}
          {!isGameStart && (
            <div>
              <WaitingNav roomId={roomId} />
              <Main>
                <header className="App-header">
                  <>
                    <VideoRoom
                      isNight={isNight}
                      isVotable={isVotable}
                      participantsVideo={participantsVideo}
                      participantsName={participantsName}
                    />
                  </>
                </header>
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
        </div>
      )}
    </StyledContainer>
  );
}
export default Game;
