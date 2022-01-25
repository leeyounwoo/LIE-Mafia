import React from "react";
import styled from "styled-components";

const StyledFooter = styled.div`
  border: 1px solid;
  padding: 20px;
`;

function WaitingFooter() {
  return (
    <StyledFooter>
      <div>Footer</div>
    </StyledFooter>
  );
}
export default WaitingFooter;
