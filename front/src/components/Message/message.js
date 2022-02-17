import React from "react";
import styled from "styled-components";
import Parser from "html-react-parser";
import { Card } from "react-bootstrap";

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
  word-break: break-all;
`;

function Message({ message }) {
  return (
    <StyledMsg className="Card">
      <Card.Body>
        <Card.Text>{message}</Card.Text>
      </Card.Body>
    </StyledMsg>
  );
}
export default Message;
