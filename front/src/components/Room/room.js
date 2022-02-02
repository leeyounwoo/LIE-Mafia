import { useState } from "react";
import VideoRoom from "../VideoRoom/videoRoom";

function Room({ signalApp }) {
  const [name, setName] = useState(true);
  const onBtnClick = (name) => {
    setName(false);
  };
  console.log("앱 시그널앱", signalApp);

  return (
    <div className="App">
      <header className="App-header">
        {name && <button onClick={onBtnClick}></button>}
        {!name && (
          <>
            <VideoRoom signalApp={signalApp}></VideoRoom>
          </>
        )}
      </header>
    </div>
  );
}

export default Room;
