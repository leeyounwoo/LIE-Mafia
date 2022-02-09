import React from "react";
import styled from "styled-components";

const User = styled.div`
  box-sizing: border-box;
  width: 24vw;
  height: 30vh;
  border: 1px solid black;
`;

function Cam(props) {
  return (
    <User
      onClick={() => {
        console.log(props.name);
      }}
    >
      {props.name}
    </User>
  );
}
export default Cam;
