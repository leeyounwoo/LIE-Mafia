import { useState } from "react";
import SignalApp from "../services/websocket";
import VideoRoom from "../components/VideoRoom/videoRoom";

const signalApp = new SignalApp(window.location.pathname.split("/").pop());
console.log("주소", window.location.pathname.split("/").pop());

function Game() {
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
export default Game;
