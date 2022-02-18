import { useEffect, useState, useRef } from "react";
import VideoRoom from "../components/VideoRoom/videoRoom";
import FinalArgument from "../components/VideoRoom/finalArgument";
import WaitingNav from "../components/Navbar/navbar";
import GameNav from "../components/Navbar/gameNav";
import Footer from "../components/Footer/footer";
import { WebRtcPeer } from "kurento-utils";
import styled from "styled-components";
import Home from "../components/Home/home";
import GameResult from "../pages/GameResult";

const StyledContainer = styled.div`
  height: 100vh;
`;

function Game() {
  // git
  const webSocketUrl = "wss://lie-mafia.site/ws";
  // const webSocketUrl = "ws://i6c209.p.ssafy.io:8001/ws";
  let ws = useRef(null);
  // 게임 참여자
  const [participantsName, setParticipantsName] = useState([]);
  const [participantsVideo, setParticipantsVideo] = useState([]);
  const tempParticipantsName = participantsName;
  const tempParticipantsVideo = participantsVideo;
  // 준비 상태
  const [readyState, setReadyState] = useState({});
  const tempReadyState = readyState;

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

  // 로컬 사용자 닉네임
  const [username, setUsername] = useState(
    `User${Math.random().toString(36).substr(2, 11)}`
  );

  // LEADER or PLAYER
  const [authority, setAuthority] = useState("");

  // 방 고유 번호
  const [roomId, setRoomId] = useState(
    window.location.pathname.split("/").pop()
  );

  // 직업
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
  const [isVotable, setIsVotable] = useState(false);

  // 밤투표 상황인지 아닌지
  const [isNight, setIsNight] = useState(false);

  // 날짜
  const [dateCount, setDateCount] = useState(0);

  // 게임 진행 상태
  const [isGameStart, setIsGameStart] = useState(false);

  const [isGameEnd, setIsGameEnd] = useState(false);

  // 게임 시작 가능 상태
  const [canStart, setCanStart] = useState(false);

  // 시간 조정
  const [endTime, setEndTime] = useState(60);

  // 로컬 사용자가 사망했는지
  const [isDeadPlayer, setIsDeadPlayer] = useState(false);

  const [gameWinner, setGameWinner] = useState("CITIZEN");

  const [message, setMessage] = useState("Game Start");

  const clickClose = () => {
    ws.current.close();
  };

  // Connection 서버로 메세지를 보내는 함수
  const sendConnectionMessage = (message) => {
    const newMessage = { eventType: "connection", data: message };
    const jsonMessage = JSON.stringify(newMessage);
    console.log("Sending message: " + jsonMessage);
    ws.current.send(jsonMessage);
  };

  // Game 서버로 메세지를 보내는 함수
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
      // git
      audio: true,
      // audio: false,
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

  // Ready 메세지 왔을 때
  const onDeliverReady = (msg) => {
    tempReadyState[msg.username] = msg.ready;
    updateReadyState();

    if (Object.keys(tempReadyState).length >= 4 && msg.ready === true) {
      let flag = true;
      Object.entries(tempReadyState).forEach(([key, value]) => {
        if (key !== participantsName[0] && value === false) {
          flag = false;
        }
      });
      if (flag === true) {
        setCanStart(true);
      }
    }
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

  // Ready 메세지 보내기
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

  // Start 메세지 보내기
  const onClickStart = () => {
    let message = "";
    message = {
      id: "start",
      roomId: roomId,
      username: participantsName[0],
    };
    sendGameMessage(message);
  };

  const treatDeadPerson = async (deadPlayerName) => {
    if (deadPlayerName === participantsName[0]) {
      setIsDeadPlayer(true);
      participantsVideo[0].rtcPeer.videoEnabled = false;
      participantsVideo[0].rtcPeer.audioEnabled = false;
    }
    setParticipantsName(
      participantsName.map((playerName, idx) =>
        playerName === deadPlayerName ? "사망자" : playerName
      )
    );
  };

  // 직업 배정
  const onRoleAssign = (msg) => {
    setUserRole(msg.job);
    setEndTime(15);
    setMessage(`당신은 ${msg.job}입니다.`);
    setIsGameStart(true);
  };

  // 아침 토론 시작
  const onStartMorning = (msg) => {
    setDateCount(msg.dayCount);
    msg.aliveUsers.map((playerName, idx) => {
      if (playerName === participantsName[0]) {
        participantsVideo[0].rtcPeer.videoEnabled = true;
        participantsVideo[0].rtcPeer.audioEnabled = true;
      }
    });
    if (msg.result !== null) {
      treatDeadPerson(msg.result);
    }
    setVoteState((prevVoteState) => {
      return {
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
      };
    });

    setEndTime(120);
    if (msg.dayCount === 1) {
      console.log("아침 시작");
      setMessage(`낮이 되었습니다. 
      2분 동안 마피아가 누구일지 토론하세요.`);
    } else {
      if (msg.result === null) {
        setMessage(`낮이 되었습니다. 밤 사이 아무도 죽지 않았습니다.
        2분 동안 마피아가 누구일지 토론하세요.`);
      } else {
        setMessage(`낮이 되었습니다. 밤 사이 ${msg.result}가 죽었했습니다. 
          2분 동안 마피아가 누구일지 토론하세요.`);
      }
    }
  };

  // 아침 투표 시작
  const onStartMorningVote = (msg) => {
    setEndTime(90);
    setMessage(`90초 동안 마피아로 생각되는 사람을 찾아 투표해주세요.`);
  };

  // 낮 투표 or 밤 투표
  const onVote = (clickIndex) => {
    if (isVotable && !isDeadPlayer) {
      if (isNight === true) {
        if (userRole === "MAFIA" || userRole === "DOCTOR") {
          const numClickIndex = Number(clickIndex);
          console.log("clickIndex", clickIndex, typeof clickIndex);
          setVoteState((prevVoteState) => {
            return {
              ...prevVoteState,
              0: {
                ...prevVoteState[0],
                choice: [numClickIndex],
              },
              [numClickIndex]: {
                ...prevVoteState[numClickIndex],
                0: true,
              },
              // [Number(prevVoteState[0].choice)]: {
              //   ...prevVoteState[prevVoteState[0].choice],
              //   0: false
              // },
            };
          });
          const message = {
            id: "nightVote",
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
      } else {
        const numClickIndex = Number(clickIndex);
        console.log("clickIndex", clickIndex, typeof clickIndex);
        setVoteState((prevVoteState) => {
          return {
            ...prevVoteState,
            0: {
              ...prevVoteState[0],
              choice: [numClickIndex],
            },
            [numClickIndex]: {
              ...prevVoteState[numClickIndex],
              0: true,
            },
            // [Number(prevVoteState[0].choice)]: {
            //   ...prevVoteState[prevVoteState[0].choice],
            //   0: false
            // },
          };
        });
        const message = {
          id: "citizenVote",
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
    }
  };

  // 투표 메세지 전달
  const onVoteFromServer = (msg) => {
    const userIndex = participantsName.indexOf(msg.username);
    const selectIndex = participantsName.indexOf(msg.select);

    setVoteState((prevVoteState) => {
      return {
        ...prevVoteState,
        [userIndex]: {
          ...prevVoteState[userIndex],
          choice: [selectIndex],
        },
        [selectIndex]: {
          ...prevVoteState[selectIndex],
          [userIndex]: true,
        },
      };
    });
    console.log(
      participantsName[userIndex],
      " vote to ",
      participantsName[selectIndex]
    );
  };

  // 최후의 변론 시작
  const onStartFinalSpeech = (msg) => {
    const selectedUserIndex = participantsName.indexOf(msg.pointedUser);
    setSelectedUserName(participantsName[selectedUserIndex]);
    setSelectedUserVideo(participantsVideo[selectedUserIndex]);
    setPlayerName([]);
    setPlayerName(
      participantsName.filter((playerName) => playerName !== msg.pointedUser)
    );
    setEndTime(30);
    setMessage(
      `낮 투표 결과 지목된 ${msg.pointedUser}는 30초간 최후의 변론을 해주세요.`
    );
  };

  // 사형 투표 시작
  const onStartExecutionVote = (msg) => {
    if (msg.pointedUser === participantsName[0]) {
      setIsVotable(false);
    }
    setEndTime(60);
    setMessage(
      `60초 동안 ${msg.pointedUser} 을 살릴지(왼쪽) or 죽일지(오른쪽) 투표하세요!`
    );
  };

  // 밤 투표 시작
  // Received message: {"eventType":"game","id":"startNightVote","data":{"roomId":"dbdc1786-e2d5-48c0-974b-6e05a8edd632","result":null,"endTime":"2022-02-17T14:39:42.408337","aliveUsers":["Useromb9cvtagaj","Userdhgdkqzx8hs","Useri1bmmzu2chp","Usersvgez9f9n1d"],"votable":true,"coworker":["Useromb9cvtagaj","Useromb9cvtagaj"]}}
  const onStartNightVote = (msg) => {
    participantsVideo[0].rtcPeer.videoEnabled = false;
    participantsVideo[0].rtcPeer.audioEnabled = false;
    setEndTime(90);
    if (msg.result === "") {
      setMessage(`밤이 되었습니다. 마피아와 의사는 투표해주세요.`);
    } else if (msg.result === null) {
      setMessage(
        `밤이 되었습니다. 사형 투표 결과 아무도 죽지 않았습니다. 마피아와 의사는 투표해주세요.`
      );
    } else {
      setMessage(
        `밤이 되었습니다. 사형 투표 결과 ${msg.result}가 죽었습니다. 마피아와 의사는 투표해주세요.`
      );
    }
  };

  // 사형에 찬성하는 버튼 클릭시 호출
  const onVoteAgree = async () => {
    // 본인이 사형 투표 당사자면 투표 못하게 하는 코드 추가해야 함
    if (isVotable && !isDeadPlayer) {
      setVoteStateFinal((prevVoteStateFinal) => {
        return {
          ...prevVoteStateFinal,
          agree: {
            ...prevVoteStateFinal.agree,
            0: true,
          },
          disagree: {
            ...prevVoteStateFinal.disagree,
            0: false,
          },
        };
      });
      const message = {
        id: "executionVote",
        phase: "citizenVote",
        roomId: roomId,
        username: participantsName[0],
        select: selectedUserName,
        agreeToDead: true,
      };
      sendGameMessage(message);
      console.log(participantsName[0], " vote for the approval of death");
    }
  };

  const onVoteAgreeFromServer = async (msg) => {
    const userIndex = Number(participantsName.indexOf(msg.username));
    setVoteStateFinal((prevVoteStateFinal) => {
      return {
        ...prevVoteStateFinal,
        agree: {
          ...prevVoteStateFinal.agree,
          [userIndex]: true,
        },
        disagree: {
          ...prevVoteStateFinal.disagree,
          [userIndex]: false,
        },
      };
    });
    console.log(participantsName[userIndex], " vote for the approval of death");
  };

  // 사형에 반대하는 버튼 클릭시 호출
  const onVoteDisAgree = async () => {
    // 본인이 사형 투표 당사자면 투표 못하게 하는 코드 추가해야 함
    if (isVotable && !isDeadPlayer) {
      setVoteStateFinal((prevVoteStateFinal) => {
        return {
          ...prevVoteStateFinal,
          agree: {
            ...prevVoteStateFinal.agree,
            0: false,
          },
          disagree: {
            ...prevVoteStateFinal.disagree,
            0: true,
          },
        };
      });
      const message = {
        id: "executionVote",
        phase: "citizenVote",
        roomId: roomId,
        username: participantsName[0],
        select: selectedUserName,
        agreeToDead: false,
      };
      sendGameMessage(message);
      console.log(participantsName[0], " vote for the rejection of death");
    }
  };

  const onVoteDisAgreeFromServer = async (msg) => {
    const userIndex = Number(participantsName.indexOf(msg.username));
    setVoteStateFinal((prevVoteStateFinal) => {
      return {
        ...prevVoteStateFinal,
        agree: {
          ...prevVoteStateFinal.agree,
          [userIndex]: false,
        },
        disagree: {
          ...prevVoteStateFinal.disagree,
          [userIndex]: true,
        },
      };
    });
    console.log(
      participantsName[userIndex],
      " vote for the rejection of death"
    );
  };

  const onEnd = async (msg) => {
    await setGameWinner(msg);
    setIsGameEnd(true);
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
            roomId: "",
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

        let parsedMessageId = parsedMessage.id;
        if (parsedMessageId === undefined) {
          switch (parsedMessage.data.id) {
            case "madeVote":
              if (parsedMessage.data.agreeToDead === true) {
                onVoteAgreeFromServer(parsedMessage.data);
              } else {
                onVoteDisAgreeFromServer(parsedMessage.data);
              }
              break;

            default:
              console.error("Unrecognized message", parsedMessage);
          }
        } else {
          // 최후의 변론 또는 사형 투표일 땐 사형투표 그리드
          // 그 외엔 일반 게임 그리드
          if (
            parsedMessage.id === "startFinalSpeech" ||
            parsedMessage.id === "startExecutionVote" ||
            (parsedMessage.id === "madeVote" &&
              parsedMessage.data.id === "executionVote")
          ) {
            setIsExecutionGrid(true);
          } else {
            setIsExecutionGrid(false);
          }

          // 아침 투표, 사형 투표, 밤 투표일 땐 투표가능
          // 그 외엔 투표 불가능
          if (
            parsedMessage.id === "startExecutionVote" ||
            parsedMessage.id === "startMorningVote" ||
            parsedMessage.id === "startNightVote" ||
            parsedMessage.id === "citizenVote" ||
            parsedMessage.id === "madeVote"
          ) {
            setIsVotable(true);
          } else {
            setIsVotable(false);
          }

          // 밤투표일 땐 밤
          // 그 외엔 낮
          if (
            parsedMessage.id === "startNightVote" ||
            (parsedMessage.id === "citizenVote" &&
              parsedMessage.data.id === "NightVote") ||
            parsedMessage.id === "NightVote"
          ) {
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

            // 준비 메세지 전달
            case "ready":
              onDeliverReady(parsedMessage);
              break;

            // 직업 배정
            case "roleAssign":
              onRoleAssign(parsedMessage.data);
              break;

            // 아침 토론
            case "startMorning":
              // 밤 투표 결과 초기화
              setVoteState((prevVoteState) => {
                return {
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
                };
              });
              onStartMorning(parsedMessage.data);
              break;

            // 아침 투표
            case "startMorningVote":
              onStartMorningVote(parsedMessage.data);
              break;

            // 아침 투표 전달
            case "citizenVote":
              if (parsedMessage.data.id === "MorningVote") {
                console.log("onVoteFromServer 실행");
                onVoteFromServer(parsedMessage.data);
              }
              break;

            // 최후의 변론
            case "startFinalSpeech":
              // 아침 투표 결과 초기화
              setVoteState((prevVoteState) => {
                return {
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
                };
              });
              onStartFinalSpeech(parsedMessage.data);
              break;

            // 사형 투표
            case "startExecutionVote":
              onStartExecutionVote(parsedMessage.data);
              break;

            // 사형 투표 전달
            case "madeVote":
              if (parsedMessage.data.agreeToDead === true) {
                onVoteAgreeFromServer(parsedMessage.data);
              } else {
                onVoteDisAgreeFromServer(parsedMessage.data);
              }
              break;

            // 밤 투표
            case "startNightVote":
              setVoteState((prevVoteState) => {
                return {
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
                };
              });
              setVoteStateFinal((prevVoteStateFinal) => {
                return {
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
                };
              });
              onStartNightVote(parsedMessage.data);
              break;

            // 게임 종료
            case "end":
              const winnerJob = parsedMessage.data.result.winner.job;
              console.log("winnerJob", winnerJob);
              onEnd(winnerJob);
              console.log("gameWinner", gameWinner);
              break;

            default:
              console.log("Unrecognized message", parsedMessage);
              break;
          }
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

  return (
    <StyledContainer>
      {!join && <Home onBtnClick={onBtnClick} />}
      {join && (
        <div>
          {isGameEnd && (
            <GameResult
              participantsVideo={participantsVideo}
              participantsName={participantsName}
              gameWinner={gameWinner}
            />
          )}

          {/* 게임 진행 */}
          {!isGameEnd && isGameStart && (
            <div>
              <GameNav
                // 날짜
                dateCount={dateCount}
                endTime={endTime}
                clickClose={clickClose}
                isNight={isNight}
                isVotable={isVotable}
              />
              <h1>직업: {userRole}</h1>
              <header>
                {!isExecutionGrid && (
                  <VideoRoom
                    dateCount={dateCount}
                    isNight={isNight}
                    onVote={onVote}
                    voteState={voteState}
                    participantsVideo={participantsVideo}
                    participantsName={participantsName}
                    isGameStart={isGameStart}
                    message={message}
                    isDeadPlayer={isDeadPlayer}
                  />
                )}
                {/* 최후의 변론 */}
                {isExecutionGrid && (
                  <FinalArgument
                    isVotable={isVotable}
                    voteStateFinal={voteStateFinal}
                    onVoteAgree={onVoteAgree}
                    onVoteDisAgree={onVoteDisAgree}
                    selectedUserName={selectedUserName}
                    selectedUserVideo={selectedUserVideo}
                    playerName={playerName}
                    participantsName={participantsName}
                    participantsVideo={participantsVideo}
                    message={message}
                    isDeadPlayer={isDeadPlayer}
                  />
                )}
              </header>
            </div>
          )}

          {/* 게임 진행 X (게임 시작 전) */}
          {!isGameEnd && !isGameStart && (
            <div>
              <WaitingNav roomId={roomId} clickClose={clickClose} />

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
