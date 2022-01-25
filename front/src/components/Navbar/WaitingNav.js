import React from "react";
import { useHistory } from "react-router-dom";
import styled from "styled-components";

const StyledNav = styled.div`
  border: 1px solid;
  padding: 20px;
`;

function WaitingNav() {
  let history = useHistory();

  return (
    <StyledNav>
      <span>Lie, Mafia</span>
      <span>초대링크 : abcdefg </span>
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
