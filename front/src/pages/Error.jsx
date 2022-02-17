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
