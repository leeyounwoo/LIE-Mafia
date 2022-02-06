import React, { useEffect, useRef } from "react";
import styles from "./userCam.module.css";

const UserCam = ({ keys, participant }) => {
  const videoRef = useRef(null);
  useEffect(() => {
    videoRef.current.srcObject =
      participant.type === "local"
        ? participant.rtcPeer.getLocalStream()
        : participant.rtcPeer.getRemoteStream();
  });
  console.log(participant.rtcPeer);

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
