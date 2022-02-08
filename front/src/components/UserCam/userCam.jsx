import React, { useEffect, useRef } from "react";
import styles from "./userCam.module.css";

const UserCam = ({ keys, participant }) => {
  const videoRef = useRef(null);
  useEffect(() => {
    waitForParticipantAdd(videoRef.current.srcObject, function () {
      console.log(participant.type);
      console.log(videoRef.current.srcObject);
    });
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
    <div className={styles.container}>
      <li className={styles.li} key={keys}>
        <h1>{participant.name}</h1>
        <video className={styles.video} ref={videoRef} autoPlay></video>
      </li>
    </div>
  );
};

export default UserCam;
