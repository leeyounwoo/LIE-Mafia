import React from "react";
import styled from "styled-components";
import Timer from "./Timer";
import { ProgressBar } from "react-bootstrap";

const StyledNav = styled.div`
  display: grid;
  grid-template-columns: 15% 70% 15%;
  border: 1px solid;
  box-sizing: border-box;
  align-items: end;
`;

const StyledBar = styled.div`
  grid-column-start: 1;
  grid-column-end: 4;
`;

const StyledTimer = styled.div`
  padding-left: 5vw;
`;

function gameNav() {
  return (
    <StyledNav>
      <div style={{ paddingLeft: "3vw" }}>
        <h3>Lie, Mafia</h3>
      </div>
      <div style={{ textAlign: "center" }}>
        <h1>DAY 1</h1>
      </div>
      <StyledTimer>
        <Timer />
      </StyledTimer>
      <StyledBar>
        <ProgressBar animated now={45} />
      </StyledBar>
    </StyledNav>
  );
}
export default gameNav;
