import { useState } from "react";
import Login from "./components/login/login";
import VideoRoom from "./components/videoRoom/videoRoom";

function App({ signalApp }) {
  const [name, setName] = useState("");

  const [tmp, setTmp] = useState(false);
  const onBtnClick = (name) => {
    setName(name);
  };
  const btn = () => {
    setTmp((prev) => !prev);
  };

  return (
    <div className="App">
      <header className="App-header">
        {!name && <Login onBtnClick={onBtnClick}></Login>}
        {name && (
          <>
            <VideoRoom signalApp={signalApp}></VideoRoom>
          </>
        )}
      </header>
    </div>
  );
}

export default App;
