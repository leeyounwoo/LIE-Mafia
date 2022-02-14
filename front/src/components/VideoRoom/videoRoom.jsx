import React, { useState } from "react";
import UserCam from "../UserCam/userCam";
import { Container, Row, Col } from "react-bootstrap";
import styled from "styled-components";
import styles from "./videoRoom.css";
import Message from "../Message/message";

const VideoGrid = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  column-gap: 2vw;
  padding: 20px 10px;
`;
const MessageGrid = styled.div`
  grid-column: 1/4;
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
  dateCount,
  isNight,
  onVote,
  voteState,
  participantsVideo,
  participantsName,
  isGameStart,
  message
}) => {
  const onClick = (event) => {
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
    onVote(clickIndex);
  };

  // 사용자 인덱스 배열
  const userIndexArray1 = ["0", "1", "2"];
  const userIndexArray2 = ["3", "4", "5"];

  return (
    <VideoGrid>
      {/* 사용자 인덱스 배열 순회하면서 사용자 비디오 컴포넌트 렌더링 */}
      {userIndexArray1.map((userIndex, idx) => {
        return (
          // 사용자 화면 상자 어디를 클릭해도 onVote 함수 호출
          // id를 이용하여 사용자 화면 상자 색상 지정
          <Container
            onClick={onClick}
            id={`userContainerUsual${userIndex}`}
            key={idx}
          >
            {/* 비디오 화면을 상자 가운데에 배치 */}
            <Row className="justify-content-md-center">
              <Col md="auto" id={userIndex}>
                <UserCam
                  index={`video-${userIndex}`}
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
      {isGameStart && (<MessageGrid>
        <Message message={message} />
      </MessageGrid>)}
      {userIndexArray2.map((userIndex, idx) => {
        return (
          // 사용자 화면 상자 어디를 클릭해도 onVote 함수 호출
          // id를 이용하여 사용자 화면 상자 색상 지정
          <Container
            onClick={onClick}
            id={`userContainerUsual${userIndex}`}
            key={idx + 3}
          >
            {/* 비디오 화면을 상자 가운데에 배치 */}
            <Row className="justify-content-md-center">
              <Col md="auto" id={userIndex}>
                <UserCam
                  index={`video-${userIndex}`}
                  participant={participantsVideo[idx + 3]}
                  participantName={participantsName[idx + 3]}
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
