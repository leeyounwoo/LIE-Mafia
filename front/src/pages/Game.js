import Room from "../components/Room/room";
import SignalApp from "../services/websocket";

const signalApp = new SignalApp(window.location.pathname.split("/").pop());
console.log("주소", window.location.pathname.split("/").pop());

function Game() {
  return (
    <div>
      <Room signalApp={signalApp} />;<h1>Hello</h1>
    </div>
  );
}
export default Game;
