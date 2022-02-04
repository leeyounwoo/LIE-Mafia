import React from "react";
import { useHistory } from "react-router-dom";
import styled from "styled-components";

const StyledNav = styled.div`
  display: flex;
  justify-content: space-between;
  border: 1px solid;
  padding: 20px;
  box-sizing: border-box;
`;

function WaitingNav(props) {
  let history = useHistory();
  const roomId = props.roomId;

  return (
    <StyledNav>
      <span>Lie, Mafia</span>
      <span>초대링크 : localhost:3000/room/{roomId} </span>
      <button
        onClick={() => {
          history.push("/");
        }}
      >
        방 나가기
      </button>
    </StyledNav>
  );
}
export default WaitingNav;
