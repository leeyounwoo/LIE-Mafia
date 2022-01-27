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
  const socket = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
  let history = useHistory();
  let { roomId } = useParams();

  const [roomNum, setRoomNum] = useState("");
  const [nickName, setNickName] = useState("");
  const [enter, setEnter] = useState(true);
  const [authority, setAuthority] = useState("LEADER");

  const RoomValidCheck = () => {
    axios
      .get(`http://i6c209.p.ssafy.io:8080/room/${roomId}/`)
      .then(response => {
        console.log(response.data);
        setAuthority("PLAYER");
      })
      .catch(error => {
        console.log(error);
        // alert("없는 방 입니다.");
      });
  };

  useEffect(() => {
    socket.onopen = () => {
      console.log("연결");
    };
    roomId !== "0" && RoomValidCheck();
  }, []);

  const onChangeNickName = event => {
    setNickName(event.target.value);
  };

  const NickNameCheck = () => {
    axios
      .get(`http://i6c209.p.ssafy.io:8080/room/${roomId}/username/${nickName}`)

      .then(response => {
        console.log(response.data);
        setEnter(false);
        alert("사용가능한 닉네임입니다.");
      })
      .catch(error => {
        console.log(error);
        setEnter(true);
        alert("이미 있는 닉네임입니다.");
      });

    // actionType : create 일때만 옴
    // socket.onmessage = event => {
    //   console.log(event.data);
    //   setRoomNum(event.data);
    // };

    console.log(nickName);
  };

  const SocketSend = () => {
    socket.send(
      JSON.stringify({
        eventType: "connection",
        actionType: roomId === "0" ? "create" : "join",
        roomId: roomId === "0" ? "" : roomId,
        username: nickName,
      })
    );
    console.log(
      JSON.stringify({
        eventType: "connection",
        actionType: roomId === "0" ? "create" : "join",
        roomId: roomId === "0" ? "" : roomId,
        username: nickName,
      })
    );
  };
  const pass = () => {
    setEnter(false);
    SocketSend();
    socket.onmessage = event => {
      console.log(event.data);
      setRoomNum(event.data);
    };
  };

  const onClickCheck = event => {
    event.preventDefault();

    roomId === "0"
      ? nickName === ""
        ? alert("닉네임을 입력해주세요")
        : pass()
      : nickName !== ""
      ? NickNameCheck()
      : alert("닉네임을 입력해주세요");
  };

  const onClickEnter = () => {
    console.log(authority);
    SocketSend();
    history.push({
      pathname: roomId === "0" ? `/room/${roomNum}` : `/room/${roomId}`,
      state: { nickName: nickName },
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
      <button onClick={onClickEnter} disabled={enter}>
        입장하기
      </button>
    </div>
  );
}
export default NickName;
