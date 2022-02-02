import React, { useEffect, useRef } from "react";
import styles from "./video.module.css";

const UserCam = ({ keys, participant }) => {
  const videoRef = useRef(null);
  useEffect(() => {
    videoRef.current.srcObject =
      participant[1].type === "local"
        ? participant[1].rtcPeer.getLocalStream()
        : participant[1].rtcPeer.getRemoteStream();
    // console.log("비디오", videoRef);
  });

  return (
    <div className={styles.container}>
      <li className={styles.li} key={keys}>
        <h1>{participant[1].type}</h1>
        <video className={styles.video} ref={videoRef} autoPlay></video>
      </li>
    </div>
  );
};

export default UserCam;
