import React from "react";

function Chat() {
  let socket = new WebSocket("wss://3.37.1.251:8443/groupcall");

  socket.onopen = function (e) {
    console.log("연결");
    socket.send(
      JSON.stringify({
        id: "joinRoom",
        name: "33",
        room: "1",
      })
    );
  };
  socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(`data : ${e.data}`);
  };

  socket.onclose = function (event) {
    if (event.wasClean) {
      console.log("종료");
    } else {
      console.log("error");
    }
  };
  socket.onerror = function (error) {
    console.log(`${error.message}`);
  };
  return <div>성공</div>;
}
export default Chat;
