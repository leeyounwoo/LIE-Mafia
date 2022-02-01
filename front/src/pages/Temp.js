import FinalPage from "./FinalPage";
import SignalApp from "../services/finalclient";

const signalApp = new SignalApp(window.location.pathname.split("/").pop());
console.log("주소", window.location.pathname.split("/").pop());

function Temp() {
  return (
    <div>
      <FinalPage signalApp={signalApp} />;<h1>Hello</h1>
    </div>
  );
}
export default Temp;
