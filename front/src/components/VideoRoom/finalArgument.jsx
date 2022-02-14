import React, { useState } from "react";
import UserCam from "../UserCam/userCam";
import { Container, Row, Col } from "react-bootstrap";
import styles from "./finalArgument.css";

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

const FinalArgument = ({
  selectedUserName,
  selectedUserVideo,
  playerName,
  participantsName,
  participantsVideo,
  isVotable,
}) => {
  // 투표 상황을 보여주는 voteState
  // 투표 상황이 True가 될 때 마다 초기화해줘야 함 (아직 구현 X)
  const [voteState, setVoteState] = useState({
    agree: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
    },
    disagree: {
      0: false,
      1: false,
      2: false,
      3: false,
      4: false,
      5: false,
    },
  });

  // 사형에 찬성하는 버튼 클릭시 호출
  // 서버로 메세지 보내는 부분 구현해야 함
  const onVoteAgree = () => {
    // 본인이 사형 투표 당사자면 투표 못하게 하는 코드 추가해야 함
    if (isVotable) {
      let newVoteState = JSON.parse(JSON.stringify(voteState));
      console.log(participantsName[0], " vote for the approval of death");
      newVoteState["agree"][0] = true;
      newVoteState["disagree"][0] = false;
      setVoteState(newVoteState);
    }
  };

  // 사형에 반대하는 버튼 클릭시 호출
  // 서버로 메세지 보내는 부분 구현해야 함
  const onVoteDisAgree = () => {
    // 본인이 사형 투표 당사자면 투표 못하게 하는 코드 추가해야 함
    if (isVotable) {
      let newVoteState = JSON.parse(JSON.stringify(voteState));
      console.log(participantsName[0], " vote for the rejection of death");
      newVoteState["disagree"][0] = true;
      newVoteState["agree"][0] = false;
      setVoteState(newVoteState);
    }
  };

  return (
    <Container>
      <Row>
        {/* 사형 투표에 참여하는 모든 사용자 */}
        {playerName.forEach((player, idx) => {
          return (
            <Container id={`userContainer${participantsName.indexOf(player)}`}>
              <Row className="justify-content-md-center">
                <Col md="auto" id={participantsName.indexOf(player)}>
                  <UserCam
                    index={`video-${participantsName.indexOf(player)}`}
                    keys={participantsVideo.id}
                    participant={
                      participantsVideo[participantsName.indexOf(player)]
                    }
                    participantName={
                      participantsName[participantsName.indexOf(player)]
                    }
                  />
                </Col>
              </Row>
            </Container>
          );
        })}
      </Row>
      <Row>
        <h1>메세지 공간</h1>
      </Row>
      <Row className="justify-content-md-center">
        <Col>
          <button onClick={onVoteAgree}>찬성</button>
          <ul>
            {voteState["agree"]["0"] && (
              <button style={userColor0}>{participantsName[0]}</button>
            )}
            {voteState["agree"]["1"] && (
              <button style={userColor1}>{participantsName[1]}</button>
            )}
            {voteState["agree"]["2"] && (
              <button style={userColor2}>{participantsName[2]}</button>
            )}
            {voteState["agree"]["3"] && (
              <button style={userColor3}>{participantsName[3]}</button>
            )}
            {voteState["agree"]["4"] && (
              <button style={userColor4}>{participantsName[4]}</button>
            )}
            {voteState["agree"]["5"] && (
              <button style={userColor5}>{participantsName[5]}</button>
            )}
          </ul>
        </Col>
        <Col xs={5}>
          <div>
            {/* 지목당한 사용자 */}
            <Container
              id={`selectedUserContainer${participantsName.indexOf(
                selectedUserName
              )}`}
            >
              <Row className="justify-content-md-center">
                <Col md="auto" id={participantsName.indexOf(selectedUserName)}>
                  <UserCam
                    index={`video-${participantsName.indexOf(
                      selectedUserName
                    )}`}
                    keys={participantsVideo.id}
                    participant={
                      participantsVideo[
                        participantsName.indexOf(selectedUserName)
                      ]
                    }
                    participantName={
                      participantsName[
                        participantsName.indexOf(selectedUserName)
                      ]
                    }
                  />
                </Col>
              </Row>
            </Container>
          </div>
        </Col>
        <Col>
          <button onClick={onVoteDisAgree}>반대</button>
          <ul>
            {voteState["disagree"]["0"] && (
              <button style={userColor0}>{participantsName[0]}</button>
            )}
            {voteState["disagree"]["1"] && (
              <button style={userColor1}>{participantsName[1]}</button>
            )}
            {voteState["disagree"]["2"] && (
              <button style={userColor2}>{participantsName[2]}</button>
            )}
            {voteState["disagree"]["3"] && (
              <button style={userColor3}>{participantsName[3]}</button>
            )}
            {voteState["disagree"]["4"] && (
              <button style={userColor4}>{participantsName[4]}</button>
            )}
            {voteState["disagree"]["5"] && (
              <button style={userColor5}>{participantsName[5]}</button>
            )}
          </ul>
        </Col>
      </Row>
    </Container>
  );
};

export default FinalArgument;
