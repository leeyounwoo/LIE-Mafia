import React from "react";
import WaitingNav from "../components/Navbar/WaitingNav";
import WaitingFooter from "../components/Footer/WaitingaFooter";
import Chat from "../components/Chat/index";
import { useLocation, useParams } from "react-router-dom";
import styled from "styled-components";

const StyledContainer = styled.div`
  height: 100vh;
`;

function RoomSb() {
  let { roomId } = useParams();
  const location = useLocation();
  const authority = location.state.authority;

  console.log(authority);
  return (
    <StyledContainer>
      <WaitingNav roomId={roomId} />
      {/* <div>{nickName}</div> */}
      {/* <div>{roomNum}</div> */}
      <Chat />
      <WaitingFooter authority={authority} />
    </StyledContainer>
  );
}

export default RoomSb;
