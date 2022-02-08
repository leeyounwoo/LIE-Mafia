import React, { useEffect, useRef, useState } from 'react';
import Chat from '../components/Chat/Chat';
import Footer from '../components/Footer/footer';
import Navbar from '../components/Navbar/navbar';
import WaitingGrid from '../components/Cam/WaitingGrid';
import styled from 'styled-components';

const Main = styled.div`
    display:flex;
    flex-wrap: nowrap;
`;

function Waiting(){
    const [num, setNum] = useState(window.location.pathname.split("/").pop());
    const [authority, setAuthority] = useState("LEADER");
    const [username, setUsername] = useState(`user${Math.random().toString(36).substr(2, 11)}`);
    const [socketConnect, setSocketConnect] = useState(false);
    const [sendMsg, setSendMsg] = useState(false);
    const [participants, setParticipants] = useState([]);
    
    const webSocketUrl ="ws://i6c209.p.ssafy.io:8080/connect";
    let ws = useRef(null);

    useEffect(()=>{
        if (!ws.current) {
            ws.current = new WebSocket(webSocketUrl);
            ws.current.onopen = () => {
                console.log("연결");
                setSocketConnect(true);
            }
            ws.current.onclose = (error) => {
                console.log("disconnect", error)
            }
            ws.current.onerror = (error) => {
                console.log("error", error)
            }

            ws.current.onmessage = e => {
                // eslint-disable-next-line default-case
                switch (JSON.parse(e.data).id) {
                    case "existingParticipants":
                        console.log(JSON.parse(e.data));
                        console.log(e.data);
                        setNum(JSON.parse(e.data).data.roomId);
                        setParticipants([...Object.keys(JSON.parse(e.data).data.participants),JSON.parse(e.data).user.username]);
                        if (JSON.parse(e.data).user.authority === "PLAYER"){
                            setAuthority("PLAYER")
                        }
                        break;
                    case "newParticipant":
                        console.log(JSON.parse(e.data))
                        break;
                }
            }
        }
    },[])

    useEffect(()=>{
        if (socketConnect) {
            ws.current.send(JSON.stringify({
                id: num==="0" ? "create" : "join",
                username: username,
                roomId: num==="0" ? "" : num,
            }));
            console.log(JSON.stringify({
                id: num==="0" ? "create" : "join",
                username: username,
                roomId: num==="0" ? "" : num,
            }));
            setSendMsg(true);
        }
    }, [socketConnect])



    return (
        <div>
            <Navbar num={num}/>   
            <Main>
                <WaitingGrid username={username} participants={participants}/>
                <Chat />
            </Main>       
            <Footer authority={authority} num={num} username={username} />
        </div>
    )
}
export default Waiting;