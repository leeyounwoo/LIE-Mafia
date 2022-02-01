import React, { useEffect } from "react";
import Video from "../video/video";
import styles from "./videoRoom.module.css";

const VideoRoom = ({ signalApp }) => {
  // let participants = {};
  // useEffect(() => {
  console.log("비디오룸 시그널 앱", signalApp);
  let participants = {};
  if (signalApp._participants) {
    // eslint-disable-next-line react-hooks/exhaustive-deps
    console.log("signalApp");
    participants = signalApp._participants;
  }
  console.log("participants", participants);
  // });
  return (
    <div className={styles.container}>
      <ul>
        <h1>참가자 수: {Object.keys(participants).length}</h1>
        {/* <h2>{}</h2> */}
        {Object.entries(participants).map((participant) => {
          return (
            <>
              <Video
                keys={participant[1].name}
                participant={participant}
              ></Video>
              <h3>닉네임: {participant[1].name}</h3>
            </>
          );
        })}
      </ul>
    </div>
  );
};

export default VideoRoom;
