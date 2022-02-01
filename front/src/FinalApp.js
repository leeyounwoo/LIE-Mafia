import { useState } from "react";
import "./App.css";
import VideoRoom from "./components/videoRoom/videoRoom";
// import VideoRoom from "./"

function FinalApp({ signalApp }) {
  const [name, setName] = useState(true);
  const onBtnClick = (name) => {
    setName(false);
  };
  console.log("앱 시그널앱", signalApp);
  console.log("주소", window.location.pathname.split("/").pop());

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

export default FinalApp;
