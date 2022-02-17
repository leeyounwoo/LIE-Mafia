import React from "react";
import styled from "styled-components";

const StyledMsg = styled.div`
  background-color: #f5f5f5;
  box-sizing: border-box;
  border: 1px solid gray;
  width: 50vw;
  height: 25vh;
  text-align: center;
  line-height: 25vh;
  font-size: xx-large;
  font-weight: bold;
`;

function Message({ message }) {
  return <StyledMsg>{message}</StyledMsg>;
}
export default Message;
