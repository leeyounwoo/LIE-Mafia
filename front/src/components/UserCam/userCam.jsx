import React, { useEffect, useRef } from "react";
import styles from "./userCam.module.css";

const UserCam = ({ participant, index, participantName }) => {
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
    <div>
      {participantName && <h1 id={index}>{participantName}</h1>}
      {!participantName && <h1 id={index}>Anonymous</h1>}
      <video
        className={styles.video}
        id={index}
        ref={videoRef}
        autoPlay
        width={420}
        height={370}
        poster="/img/image.png"
      ></video>
    </div>
  );
};

export default UserCam;
