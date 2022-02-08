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
  width: 10vw;
  padding-top: 55vh;
`;

function Home({ join, onBtnClick }) {
  const onNewBtnClick = () => {
    onBtnClick();
  };

  return (
    <Container>
      <img alt="logo" src="/img/logo.png" />
      <StyledBtn>
        <Button onClick={onNewBtnClick}>방 만들기</Button>
      </StyledBtn>
    </Container>
  );
}
export default Home;
