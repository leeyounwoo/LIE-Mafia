import React from "react";
import UserCam from "../UserCam/userCam";
import { Container, Row, Col } from "react-bootstrap";
import styles from "./RoomGrid.module.css";

const VideoRoom = ({ participantsVideo, participantsName }) => {
  let participantCnt = participantsVideo.length;

  return (
    <Container fluid>
      <Row>
        <Col>
          <div id="0">
            {!participantCnt < 1 && (
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
        </Col>
        <Col>
          <div id="1">
            {!participantCnt < 2 && (
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
        </Col>
        <Col>
          <div id="2">
            {!participantCnt < 3 && (
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
        </Col>
      </Row>
      <Row>
        <Col></Col>
        <Col>
          <h1>공지사항 들어오는 공간</h1>
          <div className={styles.infobox}>
            <div className={styles.infotextwrap}>
              <span className={styles.infotext}>메세지</span>
            </div>
            <div className={styles.policetextwrap}>
              <span className={styles.policetext}>서브메세지</span>
            </div>
          </div>
        </Col>
        <Col></Col>
      </Row>
      <Row>
        <Col>
          <div id="3">
            {!participantCnt < 4 && (
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
        </Col>
        <Col>
          <div id="4">
            {!participantCnt < 5 && (
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
        </Col>
        <Col>
          <div id="5">
            {!participantCnt < 6 && (
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
        </Col>
      </Row>
    </Container>
  );
};

export default VideoRoom;
