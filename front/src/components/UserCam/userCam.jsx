import React, { useEffect, useRef } from "react";
import styles from "./userCam.module.css";
import styled from "styled-components";

const User = styled.div`
  box-sizing: border-box;
  width: 24vw;
  height: 30vh;
  border: 1px solid black;
`;

const UserCam = ({ key, participant, index }) => {
  const videoRef = useRef(null);
  useEffect(() => {
    waitForParticipantAdd(videoRef.current.srcObject, function () {});
  });

  function waitForParticipantAdd(srcObject, callback) {
    setTimeout(function () {
      if (videoRef.current.srcObject !== null) {
        if (callback !== undefined) {
          callback();
        }
        return;
      } else {
        videoRef.current.srcObject =
          participant.type === "local"
            ? participant.rtcPeer.getLocalStream()
            : participant.rtcPeer.getRemoteStream();
        waitForParticipantAdd(srcObject, callback);
      }
    }, 5);
  }

  return (
    <User>
      <li className={styles.li} key={key}>
        <h1>{participant.name}</h1>
        <video
          className={styles.video}
          id={index}
          ref={videoRef}
          autoPlay
          width={420}
        ></video>
      </li>
    </User>
  );
};

export default UserCam;
