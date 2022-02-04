import React, { useEffect, useRef } from "react";
import styles from "./video.module.css";

const UserCam = ({ keys, participant }) => {
  const videoRef = useRef(null);
  console.log("participant", participant.type);
  useEffect(() => {
    videoRef.current.srcObject =
      participant.type === "local"
        ? participant.rtcPeer.getLocalStream()
        : participant.rtcPeer.getRemoteStream();
    // console.log("비디오", videoRef);
  });

  return (
    <div className={styles.container}>
      <li className={styles.li} key={keys}>
        <h1>{participant.type}</h1>
        <video className={styles.video} ref={videoRef} autoPlay></video>
      </li>
    </div>
  );
};

export default UserCam;
