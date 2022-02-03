import React from "react";
import { Route, Switch } from "react-router-dom";
import Home from "./pages/Home";
import NickName from "./pages/NickName";
import Error from "./pages/Error";
import GameResult from "./pages/GameResult";
import "bootstrap/dist/css/bootstrap.min.css";
import Game from "./pages/Game1";
// import Game from "./pages/GameTry"

function App() {
  return (
    <div>
      <Switch>
        <Route exact path="/" component={Home} />
        <Route path="/Liemafia/:roomId">
          <NickName />
        </Route>
        <Route path="/room/:roomId">
          <Game />
        </Route>
        <Route path="/gameresult" component={GameResult} />
        <Route component={Error} />
      </Switch>
    </div>
  );
}

export default App;
