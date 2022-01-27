import React from "react";
import styled from "styled-components";

const StyledChat = styled.div`
  border: 1px solid;

  width: 25%;
  box-sizing: border-box;
`;

function Chat() {
  return (
    <StyledChat>
      <div>Chat</div>
    </StyledChat>
  );
}
export default Chat;
