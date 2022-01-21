import React from "react";
import { Route } from "react-router-dom";
import Home from "./pages/Home";
import NickName from "./pages/NickName";
import Room from "./pages/Room";

function App() {
  return (
    <div>
      <Route exact path="/">
        <Home />
      </Route>
      <Route path="/nickname/:roomID" component={NickName}>
        <NickName />
      </Route>
      <Route path="/room/:roomID/" component={Room}>
        <Room />
      </Route>
    </div>
  );
}

export default App;
