import axios from "axios";
import React, { useState } from "react";
import { useParams, useHistory } from "react-router-dom";
import styled from "styled-components";

const Box = styled.div`
  border: 1px solid;
  width: 20rem;
  padding: 30px;
`;

function NickName() {
  let history = useHistory();
  const [flipped, setFlipped] = useState(false);
  const { roomId } = useParams();
  //console.log(roomId);

  const [nickName, setNickName] = useState("");

  const onChangeNickName = (event) => {
    setNickName(event.target.value);
    //console.log(event.target.value);
    console.log(nickName);
  };

  const onFlip = () => setFlipped((current) => !current);

  const onClick = () => {
    history.push({
      pathname: "/groupcall",
      state: { nickName: nickName },
    });
    //console.log(nickName);
  };

  const onClickCheck = () => {
    // axios 통신으로 닉네임 중복확인 보내기
    // axios
    //   .get("")
    //   .then(response => {
    //     console.log(response.data);
    //     response.data.map(obj => (
    //       <div key={obj.id}>{obj.id !== nickName && onFlip}</div>
    //     ));
    //   })
    //   .catch(error => {
    //     console.log(error);
    //   });
    console.log(nickName);
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
