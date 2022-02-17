import React from "react";
import { Button } from "react-bootstrap";
import styled from "styled-components";

const Container = styled.div`
  display: grid;
  justify-content: center;
  justify-items: center;
  margin-top: 5vh;
`;
const StyledBtn = styled.div`
  margin-top: 55vh;
`;
const StyledMsg = styled.div`
  margin-bottom: 20vh;
  font-size: 10vh;
  font-weight: bold;
`;

function Home({ join, onBtnClick }) {
  const onNewBtnClick = () => {
    onBtnClick();
  };

  return (
    <Container>
      <StyledMsg>Lie, Mafia</StyledMsg>
      <StyledBtn>
        {window.location.pathname.split("/").pop() === "0" ? (
          <Button onClick={onNewBtnClick}>방 만들기</Button>
        ) : (
          <Button onClick={onNewBtnClick}>방 입장하기</Button>
        )}
      </StyledBtn>
    </Container>
  );
}
export default Home;
