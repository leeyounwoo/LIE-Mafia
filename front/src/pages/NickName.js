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
  const [flipped, setFlipped] = useState(false);

  useEffect(() => {
    console.log(roomId);
    socket.onopen = () => {
      console.log("연결");
    };
  }, []);

  useEffect(() => {
    RoomValidCheck();
  }, [roomId]);

  const onFlip = () => setFlipped(current => !current);

  const onChangeNickName = event => {
    setNickName(event.target.value);
    //console.log(event.target.value);
    // console.log(nickName);
  };

  const onClickEnter = () => {
    // console.log(roomNum);
    history.push({
      pathname: roomId === "0" ? `/room/${roomNum}` : `/room/${roomId}`,
      state: { nickName: nickName },
    });
  };
  const RoomValidCheck = () => {
    axios
      .get(`http://i6c209.p.ssafy.io:8080/room/${roomId}/`)
      .then(response => {
        console.log(response.data);
      })
      .catch(error => {
        console.log(error);
      });
  };
  const NickNameCheck = () => {
    console.log(nickName);
    axios
      .get(`http://i6c209.p.ssafy.io:8080/room/${roomId}/username/${nickName}`)
      // {
      //   url: `http://i6c209.p.ssafy.io:8080/room/${roomId}/username/${nickName}`,
      //   method: "get",
      //   baseURL: "http://localhost:8080",
      // }

      .then(response => {
        console.log(response.data);
        // response.data.map(obj => (
        //   <div key={obj.id}>{obj.id !== nickName && onFlip}</div>
        // ));
      })
      .catch(error => {
        console.log(error);
        console.log("이미 있는 닉네임입니다.");
      });
  };

  const onClickCheck = event => {
    event.preventDefault();

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
    socket.onmessage = event => {
      console.log(event.data);
      setRoomNum(event.data);
    };
    NickNameCheck();
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
      <button disabled={false} onClick={onClickEnter}>
        입장하기
      </button>
    </div>
  );
}
export default NickName;
