import React from "react";
import styled from "styled-components";

const StyledMsg = styled.div`
  background-color: #f5f5f5;
  box-sizing: border-box;
  border: 1px solid gray;
  width: 50vw;
  height: 20vh;
  text-align: center;
  line-height: 20vh;
`;

function Message() {
  return <StyledMsg>Message</StyledMsg>;
}
export default Message;
