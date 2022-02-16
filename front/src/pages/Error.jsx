import React from "react";
import { useHistory } from "react-router-dom";
import { Button } from "react-bootstrap";
import styled from "styled-components";

const StyledMsg = styled.div`
  margin-bottom: 20vh;
  font-size: 10vh;
  font-weight: bold;
`;
const Container = styled.div`
  display: grid;
  justify-content: center;
  justify-items: center;
  margin-top: 15vh;
`;

function Error() {
  const message = {
    eventType: "game",
    id: "roleAssign",
    data: {
      roomId: "68141fdf-5b00-4ed5-b6bf-e69a5aa8de41",
      players: [
        "User9uu1nc5t55h",
        "Userlzl9tsswvn",
        "Userdswj5lki1n6",
        "User2gvjlwfolta",
      ],
      job: "CITIZEN",
      endTime: "2022-02-16T14:31:18.832723",
    },
  };
  let history = useHistory();

  return (
    <Container>
      <StyledMsg>Lie, Mafia</StyledMsg>
      <Button
        size="lg"
        onClick={() => {
          history.push("/room/0");
        }}
      >
        Start
      </Button>
    </Container>
  );
}
export default Error;
