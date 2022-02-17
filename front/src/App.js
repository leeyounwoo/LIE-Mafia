import React from "react";
import { Route, Switch } from "react-router-dom";
import Error from "./pages/Error";
import GameResult from "./pages/GameResult";
import "bootstrap/dist/css/bootstrap.min.css";
import Game from "./pages/Game";

function App() {
  return (
    <div>
      <Switch>
        <Route path="/room/:roomId" component={Game} />
        <Route path="/gameresult" component={GameResult} />
        <Route component={Error} />
      </Switch>
    </div>
  );
}

export default App;
