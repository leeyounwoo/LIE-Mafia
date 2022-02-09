import React, { useState } from "react";
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
const userColor0 = {
  background: "red",
};
const userColor1 = {
  background: "orange",
};
const userColor2 = {
  background: "yellow",
};
const userColor3 = {
  background: "#20c997",
};
const userColor4 = {
  background: "#0d6efd",
};
const userColor5 = {
  background: "#6f42c1",
};

const StyledCam0 = styled.div`
  background-color: red;
`;
const StyledCam1 = styled.div`
  background-color: orange;
`;
const StyledCam2 = styled.div`
  background-color: yellow;
`;
const StyledCam3 = styled.div`
  background-color: #20c997;
`;
const StyledCam4 = styled.div`
  background-color: #0d6efd;
`;
const StyledCam5 = styled.div`
  background-color: #6f42c1;
`;

const VideoRoom = ({ participantsVideo, participantsName }) => {
  let participantCnt = participantsVideo.length;

  const [voteState, setVoteState] = useState({
    0: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    1: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    2: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    3: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    4: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
    5: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
      choice: "",
    },
  });

  const onVote0 = () => {
    let newVoteState = JSON.parse(JSON.stringify(voteState));
    console.log(participantsName[0], " vote to ", participantsName[0]);
    const prevChoice = newVoteState[0]["choice"];
    if (prevChoice !== "" && newVoteState[prevChoice][0]) {
      newVoteState[prevChoice][0] = false;
    }
    newVoteState[0]["choice"] = 0;
    newVoteState[0][0] = true;
    setVoteState(newVoteState);
  };

  const onVote1 = () => {
    let newVoteState = JSON.parse(JSON.stringify(voteState));
    console.log(participantsName[0], " vote to ", participantsName[1]);
    const prevChoice = newVoteState[0]["choice"];
    if (prevChoice !== "" && newVoteState[prevChoice][0]) {
      newVoteState[prevChoice][0] = false;
    }
    newVoteState[0]["choice"] = 1;
    newVoteState[1][0] = true;
    setVoteState(newVoteState);
  };

  const onVote2 = () => {
    let newVoteState = JSON.parse(JSON.stringify(voteState));
    console.log(participantsName[0], " vote to ", participantsName[2]);
    const prevChoice = newVoteState[0]["choice"];
    if (prevChoice !== "" && newVoteState[prevChoice][0]) {
      newVoteState[prevChoice][0] = false;
    }
    newVoteState[0]["choice"] = 2;
    newVoteState[2][0] = true;
    setVoteState(newVoteState);
  };

  const onVote3 = () => {
    let newVoteState = JSON.parse(JSON.stringify(voteState));
    console.log(participantsName[0], " vote to ", participantsName[3]);
    const prevChoice = newVoteState[0]["choice"];
    if (prevChoice !== "" && newVoteState[prevChoice][0]) {
      newVoteState[prevChoice][0] = false;
    }
    newVoteState[0]["choice"] = 3;
    newVoteState[3][0] = true;
    setVoteState(newVoteState);
  };

  const onVote4 = () => {
    let newVoteState = JSON.parse(JSON.stringify(voteState));
    console.log(participantsName[0], " vote to ", participantsName[4]);
    const prevChoice = newVoteState[0]["choice"];
    if (prevChoice !== "" && newVoteState[prevChoice][0]) {
      newVoteState[prevChoice][0] = false;
    }
    newVoteState[0]["choice"] = 4;
    newVoteState[4][0] = true;
    setVoteState(newVoteState);
  };

  const onVote5 = () => {
    let newVoteState = JSON.parse(JSON.stringify(voteState));
    console.log(participantsName[0], " vote to ", participantsName[5]);
    const prevChoice = newVoteState[0]["choice"];
    if (prevChoice !== "" && newVoteState[prevChoice][0]) {
      newVoteState[prevChoice][0] = false;
    }
    newVoteState[0]["choice"] = 5;
    newVoteState[5][0] = true;
    setVoteState(newVoteState);
  };

  return (
    <Container>
      <StyledCam0 onClick={onVote0}>
        <div id="0">
          {!(participantCnt >= 1) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 1 && (
            <div>
              <UserCam
                index="video-0"
                keys={participantsVideo.id}
                participant={participantsVideo[0]}
              />
              <ul>
                {voteState["0"]["0"] && (
                  <button style={userColor0}>{participantsName[0]}</button>
                )}
                {voteState["0"]["1"] && (
                  <button style={userColor1}>{participantsName[1]}</button>
                )}
                {voteState["0"]["2"] && (
                  <button style={userColor2}>{participantsName[2]}</button>
                )}
                {voteState["0"]["3"] && (
                  <button style={userColor3}>{participantsName[3]}</button>
                )}
                {voteState["0"]["4"] && (
                  <button style={userColor4}>{participantsName[4]}</button>
                )}
                {voteState["0"]["5"] && (
                  <button style={userColor5}>{participantsName[5]}</button>
                )}
              </ul>
            </div>
          )}
        </div>
      </StyledCam0>
      <StyledCam1 onClick={onVote1}>
        <div id="1">
          {!(participantCnt >= 2) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 2 && (
            <div>
              <UserCam
                index="video-1"
                keys={participantsVideo.id}
                participant={participantsVideo[1]}
              />
              <ul>
                {voteState["1"]["0"] && (
                  <button style={userColor0}>{participantsName[0]}</button>
                )}
                {voteState["1"]["1"] && (
                  <button style={userColor1}>{participantsName[1]}</button>
                )}
                {voteState["1"]["2"] && (
                  <button style={userColor2}>{participantsName[2]}</button>
                )}
                {voteState["1"]["3"] && (
                  <button style={userColor3}>{participantsName[3]}</button>
                )}
                {voteState["1"]["4"] && (
                  <button style={userColor4}>{participantsName[4]}</button>
                )}
                {voteState["1"]["5"] && (
                  <button style={userColor5}>{participantsName[5]}</button>
                )}
              </ul>
            </div>
          )}
        </div>
      </StyledCam1>
      <StyledCam2 onClick={onVote2}>
        <div id="2">
          {!(participantCnt >= 3) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 3 && (
            <div>
              <UserCam
                index="video-2"
                keys={participantsVideo.id}
                participant={participantsVideo[2]}
              />
              <ul>
                {voteState["2"]["0"] && (
                  <button style={userColor0}>{participantsName[0]}</button>
                )}
                {voteState["2"]["1"] && (
                  <button style={userColor1}>{participantsName[1]}</button>
                )}
                {voteState["2"]["2"] && (
                  <button style={userColor2}>{participantsName[2]}</button>
                )}
                {voteState["2"]["3"] && (
                  <button style={userColor3}>{participantsName[3]}</button>
                )}
                {voteState["2"]["4"] && (
                  <button style={userColor4}>{participantsName[4]}</button>
                )}
                {voteState["2"]["5"] && (
                  <button style={userColor5}>{participantsName[5]}</button>
                )}
              </ul>
            </div>
          )}
        </div>
      </StyledCam2>
      <StyledCam3 onClick={onVote3}>
        <div id="3">
          {!(participantCnt >= 4) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 4 && (
            <div>
              <UserCam
                index="video-3"
                keys={participantsVideo.id}
                participant={participantsVideo[3]}
              />
              <ul>
                {voteState["3"]["0"] && (
                  <button style={userColor0}>{participantsName[0]}</button>
                )}
                {voteState["3"]["1"] && (
                  <button style={userColor1}>{participantsName[1]}</button>
                )}
                {voteState["3"]["2"] && (
                  <button style={userColor2}>{participantsName[2]}</button>
                )}
                {voteState["3"]["3"] && (
                  <button style={userColor3}>{participantsName[3]}</button>
                )}
                {voteState["3"]["4"] && (
                  <button style={userColor4}>{participantsName[4]}</button>
                )}
                {voteState["3"]["5"] && (
                  <button style={userColor5}>{participantsName[5]}</button>
                )}
              </ul>
            </div>
          )}
        </div>
      </StyledCam3>
      <StyledCam4 onClick={onVote4}>
        <div id="4">
          {!(participantCnt >= 5) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 5 && (
            <div>
              <UserCam
                index="video-4"
                keys={participantsVideo.id}
                participant={participantsVideo[4]}
              />
              <ul>
                {voteState["4"]["0"] && (
                  <button style={userColor0}>{participantsName[0]}</button>
                )}
                {voteState["4"]["1"] && (
                  <button style={userColor1}>{participantsName[1]}</button>
                )}
                {voteState["4"]["2"] && (
                  <button style={userColor2}>{participantsName[2]}</button>
                )}
                {voteState["4"]["3"] && (
                  <button style={userColor3}>{participantsName[3]}</button>
                )}
                {voteState["4"]["4"] && (
                  <button style={userColor4}>{participantsName[4]}</button>
                )}
                {voteState["4"]["5"] && (
                  <button style={userColor5}>{participantsName[5]}</button>
                )}
              </ul>
            </div>
          )}
        </div>
      </StyledCam4>
      <StyledCam5 onClick={onVote5}>
        <div id="5">
          {!(participantCnt >= 6) && (
            <img
              src="https://is5-ssl.mzstatic.com/image/thumb/Purple114/v4/55/a1/80/55a180c1-dcd7-c318-4940-2041af92dd71/source/512x512bb.jpg"
              alt="마피아 게임 툴즈 - 모바일 마피아 게임 by Youngseung Seo"
              width={400}
            ></img>
          )}
          {participantCnt >= 6 && (
            <div>
              <UserCam
                index="video-5"
                keys={participantsVideo.id}
                participant={participantsVideo[5]}
              />
              <ul>
                {voteState["5"]["0"] && (
                  <button style={userColor0}>{participantsName[0]}</button>
                )}
                {voteState["5"]["1"] && (
                  <button style={userColor1}>{participantsName[1]}</button>
                )}
                {voteState["5"]["2"] && (
                  <button style={userColor2}>{participantsName[2]}</button>
                )}
                {voteState["5"]["3"] && (
                  <button style={userColor3}>{participantsName[3]}</button>
                )}
                {voteState["5"]["4"] && (
                  <button style={userColor4}>{participantsName[4]}</button>
                )}
                {voteState["5"]["5"] && (
                  <button style={userColor5}>{participantsName[5]}</button>
                )}
              </ul>
            </div>
          )}
        </div>
      </StyledCam5>
    </Container>
  );
};

export default VideoRoom;
