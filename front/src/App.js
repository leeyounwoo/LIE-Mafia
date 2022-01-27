import React from "react";
import { Route, Switch } from "react-router-dom";
import Home from "./pages/Home";
import NickName from "./pages/NickName";
import Room from "./pages/Room";
import Error from "./pages/Error";
import GameResult from "./pages/GameResult";
import "bootstrap/dist/css/bootstrap.min.css";

function App() {
  return (
    <div>
      <Switch>
        <Route exact path="/" component={Home} />
        <Route path="/Liemafia/:roomId" component={NickName} />
        <Route path="/room/:roomId" component={Room} />
        <Route path="/gameresult" component={GameResult} />
        <Route component={Error} />
      </Switch>
    </div>
  );
}

export default App;
