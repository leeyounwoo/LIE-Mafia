import React from "react";
import UserCam from "../UserCam/userCam";
// import { Container, Row, Col } from "react-bootstrap";
import styled from "styled-components";
// import styles from "./videoRoom.module.css";

const Container = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  column-gap: 2vw;
  row-gap: 15vh;
  padding: 20px 10px;
`;
const StyledCam1 = styled.div`
  background-color: red;
`;
const StyledCam2 = styled.div`
  background-color: orange;
`;
const StyledCam3 = styled.div`
  background-color: yellow;
`;
const StyledCam4 = styled.div`
  background-color: #20c997;
`;
const StyledCam5 = styled.div`
  background-color: #0d6efd;
`;
const StyledCam6 = styled.div`
  background-color: #6f42c1;
`;

const VideoRoom = ({ participantsVideo, participantsName }) => {
  let participantCnt = participantsVideo.length;

  return (
    <Container>
      <StyledCam1>
        <div id="0">
          {!(participantCnt >= 1) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 1 && (
            <UserCam
              keys={participantsVideo.id}
              participant={participantsVideo[0]}
            />
          )}
        </div>
      </StyledCam1>
      <StyledCam2>
        <div id="1">
          {!(participantCnt >= 2) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 2 && (
            <UserCam
              keys={participantsVideo.id}
              participant={participantsVideo[1]}
            />
          )}
        </div>
      </StyledCam2>
      <StyledCam3>
        <div id="2">
          {!(participantCnt >= 3) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 3 && (
            <UserCam
              keys={participantsVideo.id}
              participant={participantsVideo[2]}
            />
          )}
        </div>
      </StyledCam3>
      <StyledCam4>
        <div id="3">
          {!(participantCnt >= 4) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 4 && (
            <UserCam
              keys={participantsVideo.id}
              participant={participantsVideo[3]}
            />
          )}
        </div>
      </StyledCam4>
      <StyledCam5>
        <div id="4">
          {!(participantCnt >= 5) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 5 && (
            <UserCam
              keys={participantsVideo.id}
              participant={participantsVideo[4]}
            />
          )}
        </div>
      </StyledCam5>
      <StyledCam6>
        <div id="5">
          {!(participantCnt >= 6) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 6 && (
            <UserCam
              keys={participantsVideo.id}
              participant={participantsVideo[5]}
            />
          )}
        </div>
      </StyledCam6>
    </Container>
  );
};

export default VideoRoom;
