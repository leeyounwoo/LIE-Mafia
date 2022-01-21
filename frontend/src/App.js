import React from "react";
import Room from "./router/Room";
import Chat from "./components/Chat/Chat";
import { Route, Switch } from "react-router-dom";

function App() {
  return (
    <div>
      <Switch>
        <Route exact path="/" component={Room}>
          <Room />
        </Route>
        <Route path="/chat" component={Chat}>
          <Chat />
        </Route>
        <Route path="/:id">
          <div>오류</div>
        </Route>
      </Switch>
    </div>
  );
}

export default App;
