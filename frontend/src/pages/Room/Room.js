import React from "react";
import Stomp from "webstomp-client";

function Room() {
  let socket = new WebSocket("wss://3.37.1.251:8443/groupcall");
  let client = Stomp.over(socket);
  client.connect({}, function (frame) {
    console.log("연결 성공", frame);
  });
}

export default Room;
