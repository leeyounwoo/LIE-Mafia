import React from "react";
import { Route } from "react-router-dom";
import Home from "./pages/Home";
import NickName from "./pages/NickName";
import Room from "./pages/Room";
import MyVideo from "./pages/MyVideo";

function App() {
  return (
    <div>
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
    </div>
  );
}

export default App;
