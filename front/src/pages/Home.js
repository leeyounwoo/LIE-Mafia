import { useState } from "react";
import { Link } from "react-router-dom";

function Home() {
  const [roomId, setRoomId] = useState("");

  const onChangeRoomId = (event) => {
    setRoomId(event.target.value);
    // console.log(roomId);
  };

  // const onSubmit = (event) => {
  //   event.preventDefault();
  //   console.log(roomId);
  // };

  return (
    <div>
      <label htmlFor="roomId" />
      <input
        value={roomId}
        id="roomId"
        type="number"
        onChange={onChangeRoomId}
        placeholder="방 번호를 입력하세요."
      />
      <h4>
        <Link to={`/nickname/${roomId}`}>방 만들기</Link>
      </h4>
    </div>
  );
}

export default Home;
