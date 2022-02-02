import React from "react";
import UserCam from "../UserCam/userCam";
import styles from "./videoRoom.module.css";

const VideoRoom = ({ signalApp }) => {
  return (
    <div className={styles.container}>
      <ul>
        <h1>참가자 수: {Object.keys(signalApp._participants).length}</h1>
        {Object.entries(signalApp._participants).map((participant) => {
          return (
            <>
              <UserCam
                keys={participant[1].name}
                participant={participant}
              ></UserCam>
              <h3>닉네임: {participant[1].name}</h3>
            </>
          );
        })}
      </ul>
    </div>
  );
};

export default VideoRoom;
