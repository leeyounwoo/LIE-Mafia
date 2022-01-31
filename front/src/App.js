import "./App.css";
import VideoRoom from "./components/videoRoom/videoRoom";

function App({ signalApp }) {
  return (
    <div className="App">
      <header className="App-header">
        {
          <>
            <VideoRoom signalApp={signalApp}></VideoRoom>
          </>
        }
      </header>
    </div>
  );
}

export default App;
