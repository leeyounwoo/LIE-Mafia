import React, { useState } from "react";
import UserCam from "../UserCam/userCam";
import { Container, Row, Col } from "react-bootstrap";
import styles from "./finalArgument.css";
import Message from "../Message/message";
import { BsFillHandThumbsUpFill } from "react-icons/bs";
import { BsFillHandThumbsDownFill } from "react-icons/bs";

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
  voteStateFinal,
  onVoteAgree,
  onVoteDisAgree,
  selectedUserName,
  selectedUserVideo,
  playerName,
  participantsName,
  participantsVideo,
  isVotable,
  message,
  isDeadPlayer,
}) => {
  const onClickAgree = () => {
    onVoteAgree();
  };

  const onClickDisAgree = () => {
    onVoteDisAgree();
  };

  return (
    <Container>
      <Row>
        {/* 사형 투표에 참여하는 모든 사용자 */}
        {playerName.map((player, idx) => {
          return (
            <div id={`userContainer${participantsName.indexOf(player)}`}>
              <Col md="auto" id={participantsName.indexOf(player)} key={idx}>
                <UserCam
                  index={`video-${participantsName.indexOf(player)}`}
                  participant={
                    participantsVideo[participantsName.indexOf(player)]
                  }
                  participantName={
                    participantsName[participantsName.indexOf(player)]
                  }
                />
              </Col>
            </div>
          );
        })}
      </Row>
      <Row>
        <Message message={message} />
      </Row>
      <Row className="justify-content-md-center">
        <Col>
          {isVotable && (
            <BsFillHandThumbsUpFill size="50" onClick={onClickDisAgree}>
              찬성
            </BsFillHandThumbsUpFill>
          )}
          <ul>
            {voteStateFinal["agree"]["0"] && (
              <button style={userColor0}>{participantsName[0]}</button>
            )}
            {voteStateFinal["agree"]["1"] && (
              <button style={userColor1}>{participantsName[1]}</button>
            )}
            {voteStateFinal["agree"]["2"] && (
              <button style={userColor2}>{participantsName[2]}</button>
            )}
            {voteStateFinal["agree"]["3"] && (
              <button style={userColor3}>{participantsName[3]}</button>
            )}
            {voteStateFinal["agree"]["4"] && (
              <button style={userColor4}>{participantsName[4]}</button>
            )}
            {voteStateFinal["agree"]["5"] && (
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
          {isVotable && (
            <BsFillHandThumbsDownFill size="50" onClick={onClickAgree}>
              반대
            </BsFillHandThumbsDownFill>
          )}
          <ul>
            {voteStateFinal["disagree"]["0"] && (
              <button style={userColor0}>{participantsName[0]}</button>
            )}
            {voteStateFinal["disagree"]["1"] && (
              <button style={userColor1}>{participantsName[1]}</button>
            )}
            {voteStateFinal["disagree"]["2"] && (
              <button style={userColor2}>{participantsName[2]}</button>
            )}
            {voteStateFinal["disagree"]["3"] && (
              <button style={userColor3}>{participantsName[3]}</button>
            )}
            {voteStateFinal["disagree"]["4"] && (
              <button style={userColor4}>{participantsName[4]}</button>
            )}
            {voteStateFinal["disagree"]["5"] && (
              <button style={userColor5}>{participantsName[5]}</button>
            )}
          </ul>
        </Col>
      </Row>
    </Container>
  );
};

export default FinalArgument;
