import React from "react";
import styled from "styled-components";
import { Button } from "react-bootstrap";

const StyledNav = styled.div`
  display: flex;
  justify-content: space-between;
  border: 1px solid;
  box-sizing: border-box;
  padding-top: 2vh;
  padding-bottom: 2vh;
`;

function WaitingNav(props) {
  return (
    <StyledNav>
      <div style={{ paddingLeft: "2vw" }}>
        <h3>Lie, Mafia</h3>
      </div>
      <div>
        <h4>초대링크 : lie-mafia.site:3000/room/{props.roomId}</h4>
      </div>
      <div style={{ paddingRight: "2vw" }}>
        <Button onClick={props.clickClose}>방 나가기</Button>
      </div>
    </StyledNav>
  );
}
export default WaitingNav;
