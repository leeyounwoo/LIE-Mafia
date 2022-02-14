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
  const webSocketUrl = "ws://52.79.223.21:8001/ws";
  let ws = useRef(null);
  // 게임 참여자
  const [participantsName, setParticipantsName] = useState([]);
  const [participantsVideo, setParticipantsVideo] = useState([]);
  const [readyState, setReadyState] = useState({});
  const tempReadyState = readyState;

  const tempParticipantsName = participantsName;
  const tempParticipantsVideo = participantsVideo;

  // 로컬 사용자
  const [username, setUsername] = useState(
    `User${Math.random().toString(36).substr(2, 11)}`
  );
  // console.log("name", username);
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
  const [isExecutionGrid, setIsExecutionGrid] = useState(false);

  // 게임 생존자
  const [playerName, setPlayerName] = useState(participantsName);

  // 투표 가능한 상태
  // 임시 (원래는 false)
  const [isVotable, setIsVotable] = useState(false);

  // 밤투표 상황인지 아닌지
  const [isNight, setIsNight] = useState(false);

  // 날짜
  const [dateCount, setDateCount] = useState(1);

  // 게임 진행 상태
  // 임시 (원래는 false)
  const [isGameStart, setIsGameStart] = useState(false);

<<<<<<< HEAD
  const [canStart, setCanStart] = useState(false);
=======
  const [endTime, setEndTime] = useState('');

  const messageRef = useRef('');
>>>>>>> 16e523f88b5bba5a1ec385aa5c608ae162d84d65

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
    tempReadyState[participant.username] = false;

    var video = document.getElementById(
      `video-${tempParticipantsVideo.length - 1}`
    );

    const options = {
      remoteVideo: video,
      onicecandidate: (candidate) => {
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

    tempReadyState[msg.user.username] = false;

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

  // 직업 배정(O)
  // 게임 시작
  // 공지사항
  const onRoleAssign = (msg) => {
    setIsGameStart(true);
    setUserRole(msg.job);
    setEndTime(msg.endTime);
    messageRef.current = (`당신은 ${userRole}입니다.`)
  };
<<<<<<< HEAD
=======
  console.log(messageRef);
>>>>>>> 16e523f88b5bba5a1ec385aa5c608ae162d84d65

  // 아침
  // 공지사항 구현 X
  // - 첫 날인 경우엔 "아침이 되었다" + "토론을 해달라"
  // - 첫 날이 아닌 경우엔, "밤 사이 ~~ 가 죽었다" or "밤 사이 아무도 죽지 않았다."
  const onMorning = (msg) => {
    console.log(msg.data);
    setDateCount(msg.data.dayCount);
    setEndTime(msg.data.endTime);
    if (msg.data.result !== null) {
      updatePlayer(msg.data.result);
    }
    msg.data.dayCount === 1 ? messageRef.current =('낮이 되었습니다. 2분 동안 마피아가 누구일지 토론하세요.') : messageRef.current = (`낮이 되었습니다. 밤 사이 ${msg.data.result}가 사망했습니다. 2분 동안 마피아가 누구일지 토론하세요.`)
  };
  // console.log("datecount in game", dateCount);

  // 아침 투표
  const onMorningVote = (msg) => {
    setEndTime(msg.data.endTime);
    messageRef.current = ('90초 동안 마피아로 생각되는 사람을 찾아 투표해주세요.')
  };

  // 최후의 변론
  // 지목된 사용자 지정
  const onFinalSpeech = (msg) => {
    const selectedUserIndex = participantsName.indexOf(msg.result);
    setSelectedUserName(participantsName[selectedUserIndex]);
    setSelectedUserVideo(participantsVideo[selectedUserIndex]);
    setEndTime(msg.data.endTime);
    messageRef.current = ('지목당한 유저는 30초 간 최후의 변론을 하세요.')
  };

  // 사형 투표
  // 공지사항 X
  const onExecutionVote = (msg) => {
    setEndTime(msg.data.endTime);
    messageRef.current = ('60초 간 유저의 사형에 대해 찬성 or 반대를 투표하세요!')
  };

  // 밤 투표
  // 공지사항 구현 X
  const onNightVote = (msg) => {
    setEndTime(msg.data.endTime);
    messageRef.current = ('밤이 되었습니다. 마피아는 죽이고 싶은 사람을, 의사는 살리고 싶은 사람을 투표하세요.')
  };

  const onReady = (msg) => {
    console.log("before", tempReadyState);
    tempReadyState[msg.username] = msg.ready;
    updateReadyState();
    console.log("after", tempReadyState);

    console.log("in onReady", tempReadyState);
    if (Object.keys(tempReadyState).length >= 4 && msg.ready === true) {
      let flag = true;
      Object.entries(tempReadyState).forEach(([key, value]) => {
        console.log("key", key, participantsName[0]);
        console.log("value", value);
        if (key !== participantsName[0] && value === false) {
          flag = false;
        }
      });
      if (flag === true) {
        console.log("시작가능");
        setCanStart(true);
      }
    }
  };

  console.log("readyState", readyState);
  // 투표 상황을 보여주는 voteState
  // 투표 상황이 True가 될 때 마다 초기화해줘야 함 (아직 구현 X)
  const [voteState, setVoteState] = useState({
    0: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    1: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    2: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    3: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    4: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    5: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
  });

  // 투표
  // 서버로 메세지 보내는거 구현해야 함
  const onVote = (clickIndex) => {
    if (isVotable) {
      // voteState를 갱신시켜줄 newVoteState
      let newVoteState = JSON.parse(JSON.stringify(voteState));

      // 사용자가 이전에 선택했던 컴포넌트
      const prevChoice = newVoteState[0]["choice"];
      // 이전에 선택했던 컴포넌트가 있고 그 값이 true 일 경우 false로 바꿔줌
      if (prevChoice !== "" && newVoteState[prevChoice][0]) {
        newVoteState[prevChoice][0] = false;
      }
      // 사용자가 선택한 값을 선택한 컴포넌트로 갱신
      newVoteState[0]["choice"] = clickIndex;
      // 해당 컴포넌트의 사용자 이름 보일 수 있도록 true로 바꿔줌
      newVoteState[clickIndex][0] = true;
      setVoteState(newVoteState);
      const message = {
        id: isNight ? "madeNightVote" : "madeMorningVote",
        roomId: roomId,
        username: participantsName[0],
        select: participantsName[clickIndex],
      };
      sendGameMessage(message);
      console.log(
        participantsName[0],
        " vote to ",
        participantsName[clickIndex]
      );
    }
  };

  // 투표 상황을 보여주는 voteState
  // 투표 상황이 True가 될 때 마다 초기화해줘야 함 (아직 구현 X)
  const [voteStateFinal, setVoteStateFinal] = useState({
    agree: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
    },
    disagree: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
    },
  });

  // 사형에 찬성하는 버튼 클릭시 호출
  // 서버로 메세지 보내는 부분 구현해야 함
  const onVoteAgree = () => {
    // 본인이 사형 투표 당사자면 투표 못하게 하는 코드 추가해야 함
    if (isVotable) {
      let newVoteStateFinal = JSON.parse(JSON.stringify(voteStateFinal));
      newVoteStateFinal["agree"][0] = true;
      newVoteStateFinal["disagree"][0] = false;
      setVoteStateFinal(newVoteStateFinal);
      const message = {
        id: "madeExcutionVote",
        roomId: roomId,
        username: participantsName[0],
        select: selectedUserName,
        agreeToDead: true,
      };
      sendGameMessage(message);
      console.log(participantsName[0], " vote for the approval of death");
    }
  };

  // 사형에 반대하는 버튼 클릭시 호출
  // 서버로 메세지 보내는 부분 구현해야 함
  const onVoteDisAgree = () => {
    // 본인이 사형 투표 당사자면 투표 못하게 하는 코드 추가해야 함
    if (isVotable) {
      let newVoteStateFinal = JSON.parse(JSON.stringify(voteStateFinal));
      newVoteStateFinal["disagree"][0] = true;
      newVoteStateFinal["agree"][0] = false;
      setVoteStateFinal(newVoteStateFinal);
      const message = {
        id: "madeExcutionVote",
        roomId: roomId,
        username: participantsName[0],
        select: selectedUserName,
        agreeToDead: false,
      };
      sendGameMessage(message);
      console.log(participantsName[0], " vote for the rejection of death");
    }
  };

  // tempParticipant와 participant 동기화
  const updateParticipants = () => {
    setParticipantsName([]);
    setParticipantsVideo([]);
    setParticipantsName(tempParticipantsName);
    setParticipantsVideo(tempParticipantsVideo);
  };

  useEffect(() => {
    updateParticipants();
  });

  const updateReadyState = () => {
    setReadyState({});
    setReadyState(tempReadyState);
  };

  useEffect(() => {
    updateReadyState();
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
          parsedMessage.id === "startFinalSpeech" ||
          parsedMessage.id === "startExecutionVote"
        ) {
          setIsExecutionGrid(true);
        } else {
          setIsExecutionGrid(false);
        }

        // 아침 투표, 사형 투표, 밤 투표일 땐 투표가능
        // 그 외엔 투표 불가능
        // 임시 (주석처리해둠)
        if (
          parsedMessage.id === "startExecutionVote" ||
          parsedMessage.id === "startMorningVote" ||
          parsedMessage.id === "nightVote"
        ) {
          setIsVotable(true);
        } else {
          setIsVotable(false);
        }

        // 밤투표일 땐 밤
        // 그 외엔 낮
        if (parsedMessage.id === "nightVote") {
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

          // 준비
          case "ready":
            onReady(parsedMessage);
            console.log(readyState);
            break;

          // 직업 배정
          case "roleAssign":
            onRoleAssign(parsedMessage);
            setIsGameStart(true);
            break;

          // 아침 토론
          case "startMorning":
            onStartMorning(parsedMessage);
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
    tempReadyState[participantsName[0]] = !tempReadyState[participantsName[0]];
    updateReadyState();
    let message = "";
    message = {
      id: "ready",
      roomId: roomId,
      username: participantsName[0],
    };
    sendGameMessage(message);
  };

  const onClickStart = () => {
    let message = "";
    message = {
      id: "start",
      roomId: roomId,
      username: participantsName[0],
    };
    sendGameMessage(message);
  };

  console.log("participantsName", participantsName);

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
                endTime={endTime}
              />
              <header>
                {/* <Message /> */}
                {/* 최후의 변론 X */}
                {!isExecutionGrid && (
                  <VideoRoom
                    // dateCount에 따라서 공지사항 달라질거 같아서
                    dateCount={dateCount}
                    isNight={isNight}
                    onVote={onVote}
                    voteState={voteState}
                    participantsVideo={participantsVideo}
                    participantsName={participantsName}
                    isGameStart={isGameStart}
                    message={messageRef}
                  />
                )}
                {/* 최후의 변론 */}
                {isExecutionGrid && (
                  <FinalArgument
                    voteStateFinal={voteStateFinal}
                    onVoteAgree={onVoteAgree}
                    onVoteDisAgree={onVoteDisAgree}
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
              
                <header className="App-header">
                  <>
                    <VideoRoom
                      dateCount={dateCount}
                      isNight={isNight}
                      onVote={onVote}
                      voteState={voteState}
                      participantsVideo={participantsVideo}
                      participantsName={participantsName}
                      isGameStart={isGameStart}
                    />
                  </>
                </header>
              
              <Footer
                authority={authority}
                roomId={roomId}
                username={participantsName[0]}
                localUserVideo={participantsVideo[0]}
                canStart={canStart}
                readyState={readyState}
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
