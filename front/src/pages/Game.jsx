import { useEffect, useState, useRef } from "react";
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
import Message from "../components/Message/message";

const StyledContainer = styled.div`
  height: 100vh;
`;

const Main = styled.div`
  display: flex;
  flex-wrap: nowrap;
`;

function Game() {
  // const ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
  // const ws = new WebSocket("ws://52.79.223.21:8001/ws");
  const [socketConnect, setSocketConnect] = useState(false);
  const webSocketUrl ="ws://52.79.223.21:8001/ws";
  let ws = useRef(null);
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
  const [userRole, setUserRole] = useState("");

  // 지목받은 사용자 (최후의 변론)
  const [selectedUserName, setSelectedUserName] = useState(participantsName[0]);
  const [selectedUserVideo, setSelectedUserVideo] = useState(
    participantsVideo[0]
  );
  // 최후의 변론 그리드
  const [isExcutionGrid, setIsExcutionGrid] = useState(false);

  // 게임 생존자
  const [playerName, setPlayerName] = useState(participantsName);

  // 투표 가능한 상태
  const [isVotable, setIsVotable] = useState(false);

  // 밤투표 상황인지 아닌지
  const [isNight, setIsNight] = useState(false);

  // 날짜
  const [dateCount, setDateCount] = useState(0);

  // 게임 진행 상태
  const [isGameStart, setIsGameStart] = useState(false);

  // 서버쪽으로 메세지를 보내는 함수
  const sendConnectionMessage = (message) => {
    const newMessage = { eventType: "connection", data: message };
    const jsonMessage = JSON.stringify(newMessage);
    // const jsonMessage = JSON.stringify(message);

    console.log("Sending message: " + jsonMessage);
    ws.current.send(jsonMessage);
  };
  const sendGameMessage = (message) => {
    const newMessage = { eventType: "game", data: message };
    const jsonMessage = JSON.stringify(newMessage);
    // const jsonMessage = JSON.stringify(message);

    console.log("Sending message: " + jsonMessage);
    ws.current.send(jsonMessage);
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
      onicecandidate: (candidate) => {
        console.log("Remote candidate" + JSON.stringify(candidate));
        const message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: participant.username,
        };
        sendConnectionMessage(message);
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
        sendConnectionMessage(msg);
      });
    });
  };

  // 처음 사용자가 방에 입장하면 본인을 등록하고 기존 사용자를 등록
  const onExistingParticipants = async (msg) => {
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
      onicecandidate: (candidate) => {
        console.log("Local candidate" + JSON.stringify(candidate));
        const message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: msg.user.username,
        };
        sendConnectionMessage(message);
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
        sendConnectionMessage(message);
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

  // 시간 설정
  const setTime = (time) => {
    console.log(time);
  };

  // 죽은 사람 처리
  const updatePlayer = (deadPlayerName) => {
    setPlayerName(
      playerName.filter((playerName) => playerName !== deadPlayerName)
    );
  };

  // 직업 배정
  const onRoleAssign = (msg) => {
    console.log(msg)
    setUserRole(msg.job);
    setTime(msg.endTime);
  };

  // 아침
  // 공지사항 구현 X
  // - 첫 날인 경우엔 "아침이 되었다" + "토론을 해달라"
  // - 첫 날이 아닌 경우엔, "밤 사이 ~~ 가 죽었다" or "밤 사이 아무도 죽지 않았다."
  const onMorning = (msg) => {
    setDateCount(msg.day);
    setTime(msg.endTime);
    if (msg.result !== null) {
      updatePlayer(msg.result);
    }
  };

  // 아침 투표
  const onMorningVote = (msg) => {
    setTime(msg.endTime);
  };

  // 최후의 변론
  // 지목된 사용자 지정
  const onFinalSpeech = (msg) => {
    const selectedUserIndex = participantsName.indexOf(msg.result);
    setSelectedUserName(participantsName[selectedUserIndex]);
    setSelectedUserVideo(participantsVideo[selectedUserIndex]);
    setTime(msg.endTime);
  };

  // 사형 투표
  // 공지사항 X
  const onExecutionVote = (msg) => {
    setTime(msg.endTime);
  };

  // 밤 투표
  // 공지사항 구현 X
  const onNightVote = (msg) => {
    setTime(msg.endTime);
  };
  
  let readyCnt = 0;
  const onReady = (msg) => {
    console.log(Object.values(participantsName));
    console.log(`'${msg.username}'`);
    // 만약 msg.username 이 participantsName에 있고, ready가 true 이면 cnt +1
    // cnt 가 participantsname.length와 같으면 스타트버튼 활성화
    console.log(`'${msg.username}'` in [...participantsName]);
    // if (((msg.username) in participantsName) && (msg.ready)) {
    //   readyCnt = readyCnt + 1;
    // }
    if (readyCnt === participantsName.length-1) {
      // 스타트 버튼 활성화시켜
      console.log("스타트!")
    } else {
      console.log(readyCnt)
    }
  };

  useEffect(() => {
    updateParticipants();
  });

  // 컴포넌트가 처음 렌더링 됐을 때만 웹소켓 연결
  useEffect(() => {
    if (!ws.current) {
      ws.current = new WebSocket(webSocketUrl);
      ws.current.onopen = () => {
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
          sendConnectionMessage(message);
        }
      };
  
      ws.current.onmessage = (message) => {
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
  
          // 직업 배정
          case "roleAssign":
            onRoleAssign(parsedMessage);
            setIsGameStart(true);
            break;
  
          // 아침 토론
          case "startMorning":
            onMorning(parsedMessage);
            break;
  
          // 아침 투표
          case "startMorningVote":
            onMorningVote(parsedMessage);
            break;
  
          // 최후의 변론
          case "startFinalSpeech":
            onFinalSpeech(parsedMessage);
            break;
  
          // 사형 투표
          case "startExecutionVote":
            onExecutionVote(parsedMessage);
            break;
  
          // 사형 투표 결과를 어떤 메세지로 보내준다는거지?
  
          // 밤 투표
          case "nightVote":
            onNightVote(parsedMessage);
            break;
  
          case "ready":
            onReady(parsedMessage);
            break;
  
          default:
            console.error("Unrecognized message", parsedMessage);
        }
      };
  
      ws.current.onerror = (error) => {
        console.log(error);
        alert(error);
      };
  
      ws.current.onclose = (event) => {
        console.log(event);
      };
  
      // 컴포넌트가 파괴될 때 웹소켓 통신 닫음
      return function cleanup() {
        ws.current.close();
      };

    }
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

  const onClickReady = () => {
    let message = "";
    message = {
      id: "ready",
      roomId: roomId,
      username: participantsName[0],
    }
    console.log({
      id: "ready",
      roomId: roomId,
      username: participantsName[0],
    })
    sendGameMessage(message);
  };

  const onClickStart = () => {
    let message = "";
    message = {
      id: "start",
      roomId: roomId,
      username: participantsName[0],
    }
    console.log({
      id: "start",
      roomId: roomId,
      username: participantsName[0],
    })
    sendGameMessage(message);
  };

  return (
    <StyledContainer>
      {!join && <Home onBtnClick={onBtnClick} />}
      {join && (
        <div>
          {/* 게임 진행 */}
          {isGameStart && (
            <div>
              <GameNav
                // 날짜
                dateCount={dateCount}
              />
              <header>
                <Message />
                {/* 최후의 변론 X */}
                {!isExcutionGrid && (
                  <VideoRoom
                    // dateCount에 따라서 공지사항 달라질거 같아서
                    dateCount={dateCount}
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
                    playerName={playerName}
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
              <Footer
                authority={authority}
                roomId={roomId}
                username={username}
                localUserVideo={participantsVideo[0]}
                onClickCamera={onClickCamera}
                onClickMute={onClickMute}
                onClickStart={onClickStart}
                onClickReady={onClickReady}
              />
            </div>
          )}
        </div>
      )}
    </StyledContainer>
  );
}
export default Game;
