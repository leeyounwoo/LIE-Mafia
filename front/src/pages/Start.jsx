import React, {useState, useEffect, useRef} from 'react';
import GameNav from '../components/Navbar/gameNav'
import GameGrid from '../components/Cam/GameGrid';

function Start (){
    const [socketConnect, setSocketConnect] = useState(false);
    const [sendMsg, setSendMsg] = useState(false);

    const webSocketUrl = "ws://i6c209.p.ssafy.io:8081/game";
    let ws = useRef(null);

    useEffect(()=>{
        if (!ws.current) {
            ws.current = new WebSocket(webSocketUrl);
            ws.current.onopen = () => {
                console.log('게임');
                setSocketConnect(true);
            };
            ws.current.onclose = error => {
                console.log("disconnect", error);
            };
            ws.current.onerror = error => {
                console.log("error", error);
            };
            ws.current.onmessage = e => {
                console.log(JSON.parse(e.data))
            }
        }
    },[])

    useEffect(()=>{
        if (socketConnect) {
            ws.current.send(JSON.stringify({
                "id": ""
            }))
        }
    }, [socketConnect])

    return (
        <div>
            <GameNav />
            <GameGrid />
        </div>
    )
}
export default Start;