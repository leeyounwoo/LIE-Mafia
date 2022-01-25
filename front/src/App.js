import React from "react";
import { Route, Switch } from "react-router-dom";
import Home from "./pages/Home";
import NickName from "./pages/NickName";
import Room from "./pages/Room";
import Error from "./pages/Error";
import GameResult from "./pages/GameResult";

function App() {
  return (
    <div>
<<<<<<< HEAD
      <Route exact path="/">
        <Home />
      </Route>
      <Route path="/nickname/:roomID" component={NickName}>
        <NickName />
      </Route>
      <Route path="/groupcall/:roomID" component={Room}>
        <Room />
      </Route>
      <Route path="/myvideo" component={MyVideo}>
        <MyVideo />
      </Route>
=======
      <Switch>
        <Route exact path="/" component={Home} />
        <Route path="/Liemafia/:roomId" component={NickName} />
        <Route path="/groupcall" component={Room} />
        <Route path="/gameresult" component={GameResult} />
        <Route component={Error} />
      </Switch>
>>>>>>> 1cc4f271745299476c5b4072090a0e4a0393faba
    </div>
  );
}

export default App;
