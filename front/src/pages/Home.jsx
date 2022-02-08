import React from 'react';
import { useHistory } from 'react-router-dom';
import { Button } from "react-bootstrap";
import styled from 'styled-components'

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

function Home(){
    let history = useHistory();

    return (
        <Container>
            <img alt="logo" src="	http://localhost:3000/img/logo.png" />
            <StyledBtn>
                <Button onClick={() => { history.push("/game/0"); }}>
                    방 만들기
                </Button>
            </StyledBtn>
        </Container>
    )
}
export default Home;