import React from "react";
import Video from "../video/video";
import styles from "./videoRoom.module.css";

const VideoRoom = ({ signalApp }) => {
  return (
    <div className={styles.container}>
      <ul>
        {Object.entries(signalApp.participants).map((participant) => {
          return (
            <>
              <Video keys={participant[1].id} participant={participant}></Video>
              <h3>{participant[1].id}</h3>
            </>
          );
        })}
      </ul>
    </div>
  );
};

export default VideoRoom;
