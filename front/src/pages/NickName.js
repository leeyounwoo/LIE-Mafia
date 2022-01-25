import { useState } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router-dom";

function NickName() {
  const { roomID } = useParams();
  const [isConfirmed, setIsConfirmed] = useState(false);
  const [nickName, setNickName] = useState("");

  const onChangeNickName = (event) => {
    setNickName(event.target.value);
  };

  const onClick = (event) => {
    console.log(nickName);
    setIsConfirmed(true);
  };

  return (
    <div>
      <h1>NickName</h1>
      {isConfirmed ? (
        <div>
          <h3>닉네임: {nickName}</h3>
          <Link to={`/groupcall/${roomID}`}>입장하기</Link>
        </div>
      ) : (
        <div>
          <label htmlFor="nickName" />
          <input
            value={nickName}
            id="nickName"
            type="text"
            onChange={onChangeNickName}
            placeholder="닉네임을 입력하세요."
          />
          <button onClick={onClick}>확인</button>
        </div>
      )}
    </div>
  );
}
export default NickName;
