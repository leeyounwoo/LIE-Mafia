import React, { useState, useEffect } from "react";
import styled from "styled-components";

const StyledFooter = styled.div`
  border: 1px solid;
  padding: 20px;
  clear: both;
  width: 100%;
`;

function WaitingFooter(props) {
  const [button, setButton] = useState("");
  const authority = props.authority;
  useEffect(() => {
    console.log(authority);
    authority === "LEADER" ? (
      <button>{setButton("start")}</button>
    ) : (
      <button>{setButton("ready")}</button>
    );
    // 룸id는 상관없고 서버에서 player라고 했는지 방장이라고 했는지로 판단
  }, [authority]);

  return (
    <StyledFooter>
      <button>{button}</button>
    </StyledFooter>
  );
}
export default WaitingFooter;
