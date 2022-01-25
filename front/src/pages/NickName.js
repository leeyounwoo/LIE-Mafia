import axios from "axios";
import React, { useState, useEffect } from "react";
import { useParams, useHistory } from "react-router-dom";
import styled from "styled-components";

const Box = styled.div`
  border: 1px solid;
  width: 20rem;
  padding: 30px;
`;

function NickName() {
  let history = useHistory();

  const socket = new WebSocket("wss://3.37.1.251:8443/groupcall");

  useEffect(() => {
    // console.log(nickName);
    socket.onopen = event => {
      console.log("연결");
    };
    return socket.close();
  }, []);

  const [flipped, setFlipped] = useState(false);
  let { roomId } = useParams();

  const [nickName, setNickName] = useState("");

  const onChangeNickName = event => {
    setNickName(event.target.value);
    //console.log(event.target.value);
    // console.log(nickName);
  };

  const onFlip = () => setFlipped(current => !current);

  const onClick = () => {
    socket.send(
      JSON.stringify({
        eventType: "connection",
        actionType: roomId === "0" ? "createRoom" : "joinRoom",
        roomId: "",
        username: nickName,
      })
    );

    console.log(
      JSON.stringify({
        eventType: "connection",
        actionType: roomId === "0" ? "createRoom" : "joinRoom",
        roomId: "",
        username: nickName,
      })
    );
    socket.onmessage = event => {
      console.log(JSON.parse(event.data));
    };
    history.push({
      pathname: "/room",
      state: { nickName: nickName },
    });
  };

  const onClickCheck = () => {
    // axios 통신으로 닉네임 중복확인 보내기
    axios
      .get("url")
      .then(response => {
        console.log(response.data);
        response.data.map(obj => (
          <div key={obj.id}>{obj.id !== nickName && onFlip}</div>
        ));
      })
      .catch(error => {
        console.log(error);
      });
  };

  return (
    <div>
      <img alt="logo" src="../../img/logo.png" />
      <Box>
        <form className="nickname-form">
          <label htmlFor="nickName">닉네임 :</label>
          <input
            value={nickName}
            type="text"
            id="nickName"
            onChange={onChangeNickName}
            placeholder="닉네임을 입력하세요."
          />
          <button onClick={onClickCheck}>확인</button>
        </form>
      </Box>
      <button disabled={false} onClick={onClick}>
        입장하기
      </button>
    </div>
  );
}
export default NickName;
