import React, { useState } from "react";
import { useHistory } from "react-router-dom";

function Home() {
  let history = useHistory();
  const [roomId, setRoomId] = useState(1);

  const onClick = () => {
    setRoomId(roomId + 1);
    //console.log(`/Liemafia/${roomId}`);
    history.push({
      pathname: `/Liemafia/${roomId}`,
      state: { roomId: roomId },
    });
  };

  return (
    <div>
      <img alt="logo" src="img/logo.png" />
      <button onClick={onClick}>방 만들기</button>
    </div>
  );
}

export default Home;
