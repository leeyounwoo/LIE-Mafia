import axios from "axios";
import React, { useState } from "react";
import { useParams } from "react-router";
import styled from "styled-components";

const Box = styled.div`
  border: 1px solid;
  width: 20rem;
  padding: 30px;
`;

function NickName() {
  const { roomId } = useParams();
  console.log(roomId);

  const [nickName, setNickName] = useState("");

  const onChangeNickName = event => {
    setNickName(event.target.value);
    //console.log(event.target.value);
  };

  const onClick = () => {
    // axios 통신으로 닉네임 중복확인 보내기
    axios
      .get("")
      .then(response => {
        console.log(response.data);
        response.data.map(obj => (
          <div key={obj.id}>
            {obj.id !== nickName && <button>입장하기</button>}
          </div>
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
          닉네임 :
          <input
            value={nickName}
            type="text"
            id="nickName"
            onChange={onChangeNickName}
            placeholder="닉네임을 입력하세요."
          />
          <button onClick={onClick}>확인</button>
        </form>
      </Box>
      <button>입장하기</button>
    </div>
  );
}
export default NickName;
