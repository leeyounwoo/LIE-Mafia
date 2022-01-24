import React, { useEffect } from "react";
import { useParams } from "react-router-dom";

function Room() {
  const { roomId } = useParams();
  const ws = new WebSocket("wss://3.37.1.251:8443/groupcall");

  useEffect(() => {
    ws.onopen = () => {
      console.log("ws opened");
      ws.send(
        JSON.stringify({
          id: "joinRoom",
          name: "33",
          room: roomId,
        })
      );
    };
    ws.onmessage = (e) => {
      const message = JSON.parse(e.data);
      console.log("e", message);
    };
    ws.onerror = (error) => {
      console.log(error.message);
    };
    return () => {
      ws.onclose = (event) => {
        if (event.wasClean) {
          console.log("종료");
        } else {
          console.log("error");
        }
      };
      // ws.close();
    };
  }, []);

  ws.onclose = () => {
    console.log("ws closed");
  };

  return <div>hooks + ws</div>;
}
export default Room;
