import React from "react";
import { Route, Switch } from "react-router-dom";
import Error from "./pages/Error";
import GameResult from "./pages/GameResult";
import "bootstrap/dist/css/bootstrap.min.css";
import Game from "./pages/Game";
import Waiting from "./pages/Waiting";
import Start from "./pages/Start";

function App() {
  return (
    <div>
      <Switch>
        <Route path="/game/:num" component={Waiting} />
        <Route path="/room/:roomId" component={Game} />
        <Route path="/gameresult" component={GameResult} />
        <Route path="/start/:roomId" component={Start} />
        <Route component={Error} />
      </Switch>
    </div>
  );
}

export default App;
