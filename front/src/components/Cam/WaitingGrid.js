import React from "react";
import Cam from "./Cam";
import styled from "styled-components";

const Container = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  column-gap: 2vw;
  row-gap: 15vh;
  padding: 20px 10px;
`;
const StyledCam1 = styled.div`
  background-color: red;
`;
const StyledCam2 = styled.div`
  background-color: orange;
`;
const StyledCam3 = styled.div`
  background-color: yellow;
`;
const StyledCam4 = styled.div`
  background-color: #20c997;
`;
const StyledCam5 = styled.div`
  background-color: #0d6efd;
`;
const StyledCam6 = styled.div`
  background-color: #6f42c1;
`;

function WaitingGrid() {
  return (
    <Container>
      <StyledCam1>
        <Cam name="user1" />
      </StyledCam1>
      <StyledCam2>
        <Cam name="user2" />
      </StyledCam2>
      <StyledCam3>
        <Cam name="user3" />
      </StyledCam3>
      <StyledCam4>
        <Cam name="user4" />
      </StyledCam4>
      <StyledCam5>
        <Cam name="user5" />
      </StyledCam5>
      <StyledCam6>
        <Cam name="user6" />
      </StyledCam6>
    </Container>
  );
}
export default WaitingGrid;
