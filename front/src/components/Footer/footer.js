import React, { useState, useEffect, useRef } from "react";
import { useHistory } from "react-router-dom";
import { Button } from "react-bootstrap";
import styled from "styled-components";

const StyledFooter = styled.div`
  border: 1px solid;
  padding: 20px;
  box-sizing: border-box;
  button {
    display: block;
    margin: auto;
  }
`;

function WaitingFooter(props) {
  const [socketConnect, setSocketConnect] = useState(false);
  const [sendMsg, setSendMsg] = useState(false);
  const changePage = useRef("");
  let history = useHistory();

  const webSocketUrl = "ws://i6c209.p.ssafy.io:8081/game";
  let ws = useRef(null);

  useEffect(() => {
    if (!ws.current) {
      ws.current = new WebSocket(webSocketUrl);
      ws.current.onopen = () => {
        console.log("게임");
        setSocketConnect(true);
      };
      ws.current.onclose = (error) => {
        console.log("disconnect", error);
      };
      ws.current.onerror = (error) => {
        console.log("error", error);
      };
      ws.current.onmessage = (e) => {
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
      ws.current.send(
        JSON.stringify({
          id: "ready",
          roomId: props.roomId,
          username: props.username,
        })
      );
      console.log(
        JSON.stringify({
          id: "ready",
          roomId: props.roomId,
          username: props.username,
        })
      );
      ws.current.onmessage = (e) => {
        console.log(JSON.parse(e.data));
      };
    }
  };

  const onClickStart = () => {
    if (socketConnect) {
      ws.current.send(
        JSON.stringify({
          id: "start",
          roomId: props.roomId,
          username: props.username,
        })
      );
      setSendMsg(true);
      console.log(
        JSON.stringify({
          id: "start",
          roomId: props.roomId,
          username: props.username,
        })
      );
      ws.current.onmessage = (e) => {
        // console.log(JSON.parse(e.data));
        console.log(e.data);
      };
    }
  };

  return (
    <StyledFooter>
      <h1>{props.authority}</h1>
      {props.authority === "LEADER" ? (
        <Button onClick={onClickStart}>Start</Button>
      ) : (
        <Button onClick={onClickReady}>Ready</Button>
      )}
    </StyledFooter>
  );
}
export default WaitingFooter;
