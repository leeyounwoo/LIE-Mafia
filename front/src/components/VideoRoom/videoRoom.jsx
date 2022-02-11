import React, { useState } from "react";
import UserCam from "../UserCam/userCam";
import { Container, Row, Col } from "react-bootstrap";
import styled from "styled-components";
import styles from "./videoRoom.css";

const VideoGrid = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  column-gap: 2vw;
  row-gap: 15vh;
  padding: 20px 10px;
`;

const buttonColor0 = {
  background: "red",
};
const buttonColor1 = {
  background: "orange",
};
const buttonColor2 = {
  background: "yellow",
};
const buttonColor3 = {
  background: "#20c997",
};
const buttonColor4 = {
  background: "#0d6efd",
};
const buttonColor5 = {
  background: "#6f42c1",
};

const VideoRoom = ({
  participantsVideo,
  participantsName,
  isVotable,
  isNight,
}) => {
  // 투표 상황을 보여주는 voteState
  // 투표 상황이 True가 될 때 마다 초기화해줘야 함 (아직 구현 X)
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

  // 투표
  const onVote = (event) => {
    let clickIndex = event.target.id[event.target.id.length - 1];
    // 클릭 이벤트를 사용해서 클릭한 컴포넌트의 index 를 찾는다.
    if (clickIndex) {
      console.log(event.target.id[event.target.id.length - 1]);
      // Container 클릭 했을 때
    } else {
      clickIndex =
        event.target.parentElement.id[event.target.parentElement.id.length - 1];
      // 제목을 클릭했을 때
      if (clickIndex) {
        console.log(
          event.target.parentElement.id[
            event.target.parentElement.id.length - 1
          ]
        );
        // Row 클릭했을 때
      } else {
        clickIndex = event.target.firstElementChild.id;
        console.log(event.target.firstElementChild.id);
      }
    }

    // voteState를 갱신시켜줄 newVoteState
    let newVoteState = JSON.parse(JSON.stringify(voteState));
    console.log(participantsName[0], " vote to ", participantsName[clickIndex]);

    // 사용자가 이전에 선택했던 컴포넌트
    const prevChoice = newVoteState[0]["choice"];
    // 이전에 선택했던 컴포넌트가 있고 그 값이 true 일 경우 false로 바꿔줌
    if (prevChoice !== "" && newVoteState[prevChoice][0]) {
      newVoteState[prevChoice][0] = false;
    }
    // 사용자가 선택한 값을 선택한 컴포넌트로 갱신
    newVoteState[0]["choice"] = clickIndex;
    // 해당 컴포넌트의 사용자 이름 보일 수 있도록 true로 바꿔줌
    newVoteState[clickIndex][0] = true;
    setVoteState(newVoteState);
  };

  // 사용자 인덱스 배열
  const userIndexArray = ["0", "1", "2", "3", "4", "5"];

  return (
    <VideoGrid>
      {/* 사용자 인덱스 배열 순회하면서 사용자 비디오 컴포넌트 렌더링 */}
      {userIndexArray.map((userIndex, idx) => {
        return (
          // 사용자 화면 상자 어디를 클릭해도 onVote 함수 호출
          // id를 이용하여 사용자 화면 상자 색상 지정
          <Container onClick={onVote} id={`userContainerUsual${userIndex}`}>
            {/* 비디오 화면을 상자 가운데에 배치 */}
            <Row className="justify-content-md-center">
              <Col md="auto" id={userIndex}>
                <UserCam
                  index={`video-${userIndex}`}
                  keys={participantsVideo.id}
                  participant={participantsVideo[idx]}
                  participantName={participantsName[idx]}
                />
                <ul>
                  {voteState[userIndex]["0"] && (
                    <button style={buttonColor0}>{participantsName[0]}</button>
                  )}
                  {voteState[userIndex]["1"] && (
                    <button style={buttonColor1}>{participantsName[1]}</button>
                  )}
                  {voteState[userIndex]["2"] && (
                    <button style={buttonColor2}>{participantsName[2]}</button>
                  )}
                  {voteState[userIndex]["3"] && (
                    <button style={buttonColor3}>{participantsName[3]}</button>
                  )}
                  {voteState[userIndex]["4"] && (
                    <button style={buttonColor4}>{participantsName[4]}</button>
                  )}
                  {voteState[userIndex]["5"] && (
                    <button style={buttonColor5}>{participantsName[5]}</button>
                  )}
                </ul>
              </Col>
            </Row>
          </Container>
        );
      })}
    </VideoGrid>
  );
};

export default VideoRoom;
