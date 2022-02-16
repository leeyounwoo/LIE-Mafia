import React from "react";
import WaitingGrid from "../components/Cam/WaitingGrid";
import styled from "styled-components";
import { Button } from "react-bootstrap";
import { useHistory } from "react-router-dom";

const StyledMsg = styled.div`
  background-color: #f5f5f5;
  width: 50vw;
  height: 20vh;
  margin: 0 auto;
  text-align: center;
  line-height: 20vh;
  font-size: xx-large;
  font-weight: bold;
`;
const StyledBtn = styled.div`
  display: flex;
  justify-content: right;
  margin: 10px 20px 10px 0;
`;


function GameResult() {
  let history = useHistory();

  return (
    <div>
      <StyledBtn>
        <Button onClick={()=>{history.push("/room/0")}}>다시하기</Button>
      </StyledBtn>
      <StyledMsg>MAFIA 승리!</StyledMsg>
      <WaitingGrid />
    </div>
  )
}
export default GameResult;
