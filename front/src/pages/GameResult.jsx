import React from "react";
import WaitingGrid from "../components/Cam/WaitingGrid";
import VideoRoom from "../components/VideoRoom/videoRoom";
import styled from "styled-components";

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

function GameResult({ participantsVideo, participantsName, gameWinner }) {
  console.log("결과" + gameWinner);
  return (
    <div>
      <StyledMsg>{gameWinner} 승리!</StyledMsg>
    </div>
  );
}
export default GameResult;
