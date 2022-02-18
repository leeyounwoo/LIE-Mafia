import React, { useState, useEffect } from "react";
import styled from "styled-components";
import Timer from "./Timer";
import { Button } from "react-bootstrap";

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

const StyledTimer = styled.div``;

function GameNav({ dateCount, endTime, clickClose, isNight, isVotable }) {
  const [value, setValue] = useState(0);

  useEffect(() => {
    setValue(0);
  }, [endTime]);

  useEffect(() => {
    const countup = setInterval(() => {
      if (parseInt(value) < endTime) {
        setValue(parseInt(value) + 1);
      }
      if (parseInt(value) === endTime) {
        clearInterval(countup);
      }
    }, 1000);
    return () => clearInterval(countup);
  }, [value]);

  return (
    <StyledNav>
      <div style={{ paddingLeft: "3vw" }}>
        <h3>Lie, Mafia</h3>
      </div>
      {isNight && (
        <div style={{ textAlign: "center" }}>
          <h1>Night {dateCount}</h1>
        </div>
      )}
      {!isNight && (
        <div style={{ textAlign: "center" }}>
          <h1>DAY {dateCount}</h1>
        </div>
      )}
      <StyledTimer>
        <Timer endTime={endTime} />
      </StyledTimer>
      <StyledBar>
        <progress
          style={{ width: "80%", justifyContent: "center" }}
          value={value}
          max={endTime}
        />
      </StyledBar>
      <div style={{ paddingRight: "2vw" }}>
        <Button onClick={clickClose}>방 나가기</Button>
      </div>
    </StyledNav>
  );
}
export default GameNav;
