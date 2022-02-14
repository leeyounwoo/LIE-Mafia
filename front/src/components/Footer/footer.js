import React, { useState, useEffect, useRef } from "react";
import { useHistory } from "react-router-dom";
import { Button } from "react-bootstrap";
import styled from "styled-components";
import { BsFillCameraVideoOffFill } from "react-icons/bs";
import { BsFillCameraVideoFill } from "react-icons/bs";
import { BsFillMicMuteFill } from "react-icons/bs";
import { BsFillMicFill } from "react-icons/bs";

const StyledFooter = styled.div`
  display: grid;
  border: 1px solid;
  padding: 20px;
  box-sizing: border-box;
  grid-template-columns: 10% 10% 20% 20% 20% 10% 10%;
`;
const StyleCam = styled.div`
  margin: auto;
`;
const StyledBtn = styled.div`
  grid-column: 4 / 5;
  margin: auto;
`;

function WaitingFooter(props) {
  const [socketConnect, setSocketConnect] = useState(false);
  const [sendMsg, setSendMsg] = useState(false);
  let history = useHistory();

  // const webSocketUrl = "ws://i6c209.p.ssafy.io:8081/game";
  const webSocketUrl = "ws://52.79.223.21:8001/ws";
  let game = useRef(null);

  useEffect(() => {
    if (!game.current) {
      game.current = new WebSocket(webSocketUrl);
      game.current.onopen = () => {
        console.log("게임");
        setSocketConnect(true);
      };
      game.current.onclose = error => {
        console.log("disconnect", error);
      };
      game.current.onerror = error => {
        console.log("error", error);
      };
      game.current.onmessage = e => {
        console.log(JSON.parse(e.data));
      };
    }
  }, []);

  useEffect(() => {
    if (sendMsg === true) {
      console.log(sendMsg);
      history.push(`/start/${props.roomId}`);
    }
  }, [sendMsg]);

  const onClickReady = () => {
    if (socketConnect) {
      game.current.send(
        JSON.stringify({
          eventType: "game",
          data: {
            id: "ready",
            roomId: props.roomId,
            username: props.username,
          },
        })
      );
      console.log(
        JSON.stringify({
          eventType: "game",
          data: {
            id: "ready",
            roomId: props.roomId,
            username: props.username,
          },
        })
      );
      game.current.onmessage = e => {
        console.log(JSON.parse(e.data));
      };
    }
  };

  const onClickStart = () => {
    if (socketConnect) {
      console.log(props.username);
      game.current.send(
        JSON.stringify({
          eventType: "game",
          data: {
            id: "start",
            roomId: props.roomId,
            username: props.username,
          },
        })
      );
      // setSendMsg(true);
      console.log(
        JSON.stringify({
          eventType: "game",
          data: {
            id: "start",
            roomId: props.roomId,
            username: props.username,
          },
        })
      );
      game.current.onmessage = e => {
        // console.log(JSON.parse(e.data));
        console.log(e.data);
      };
    }
  };

  const [localCamera, setLocalCamera] = useState(true);
  const [localMute, setLocalMute] = useState(true);

  const handleCameraClick = () => {
    console.log(localCamera ? "로컬 화면 끄기" : "로컬 화면 켜기");
    props.onClickCamera();
    setLocalCamera(!localCamera);
  };

  const handleMuteClick = () => {
    console.log(localMute ? "음성 끄기" : "음성 켜기");
    props.onClickMute();
    setLocalMute(!localMute);
  };
  return (
    <StyledFooter>
      <StyleCam onClick={handleCameraClick}>
        {localCamera ? (
          <BsFillCameraVideoOffFill size="50" />
        ) : (
          <BsFillCameraVideoFill size="50" />
        )}
      </StyleCam>
      <div onClick={handleMuteClick}>
        {localMute ? (
          <BsFillMicMuteFill size="50" />
        ) : (
          <BsFillMicFill size="50" />
        )}
      </div>
      <StyledBtn>
        {props.authority === "LEADER" ? (
          <Button onClick={onClickStart} size="lg">
            Start
          </Button>
        ) : (
          <Button onClick={onClickReady} size="lg">
            Ready
          </Button>
        )}
      </StyledBtn>
    </StyledFooter>
  );
}
export default WaitingFooter;
