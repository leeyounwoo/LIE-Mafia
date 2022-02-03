import { useEffect, useState } from "react";
import VideoRoom from "../components/VideoRoom/videoRoomTry";
import { WebRtcPeer } from "kurento-utils";
// import { useParams } from "react-router-dom";

function Game() {
  const ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
  const [participantsName, setParticipantsName] = useState([]);
  const [participantsVideo, setParticipantsVideo] = useState([]);
  // const roomId = useParams("roomId")
  const roomId = window.location.pathname.split("/").pop();
  const username = `user${roomId}`;

  useEffect(() => {
    ws.onopen = () => {
      console.log("연결");
      let message = "";
      if (roomId === "0") {
        message = {
          id: "create",
          username: username,
        };
      } else {
        message = {
          id: "join",
          username: username,
          roomId: roomId,
        };
      }
      if (message !== "") {
        sendMessage(message);
      }
    };

    ws.onmessage = (message) => {
      var parsedMessage = JSON.parse(message.data);
      console.info("Received message: " + message.data);

      switch (parsedMessage.id) {
        case "existingParticipants":
          onExistingParticipants(parsedMessage);
          break;

        case "newParticipantArrived":
          onNewParticipant(parsedMessage);
          break;

        case "receiveVideoAnswer":
          onReceiveVideoAnswer(parsedMessage);
          break;

        case "iceCandidate":
          onAddIceCandidate(parsedMessage);
          break;

        default:
          console.error("Unrecognized message", parsedMessage);
      }
    };

    ws.onerror = (error) => {
      console.log(error);
    };

    ws.onclose = (event) => {
      console.log(event);
    };
  }, []);

  function sendMessage(message) {
    const jsonMessage = JSON.stringify(message);
    ws.send(jsonMessage);
    console.log("Sending message: " + jsonMessage);
  }

  function receiveVideo(sender) {
    let user = {
      name: sender,
      type: "remote",
      rtcPeer: null,
    };

    setParticipantsName((participantsName) => [...participantsName, sender]);
    setParticipantsVideo((participantsVideo) => [...participantsVideo, user]);

    let options = {
      onicecandidate: (candidate) => {
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: sender,
        };
        sendMessage(message);
      },
      configuration: {
        iceServers: [
          {
            urls: "turn:3.38.118.187:3478?transport=udp",

            username: "ssafy",

            credential: "1234",
          },
        ],
      },
    };

    user.rtcPeer = WebRtcPeer.WebRtcPeerRecvonly(options);
    user.rtcPeer.generateOffer((err, offerSdp) => {
      if (err) {
        console.error(err);
      }
      let msg = {
        id: "receiveVideoFrom",
        sender: sender,
        sdpOffer: offerSdp,
      };
      sendMessage(msg);
    });
  }

  function onExistingParticipants(msg) {
    let user = {
      name: username,
      type: "local",
      rtcPeer: null,
    };

    setParticipantsName((participantsName) => [...participantsName, username]);
    setParticipantsVideo((participantsVideo) => [...participantsVideo, user]);

    console.log(username + " registered in room " + msg.data.roomId);

    var constraints = {
      audio: false,
      video: {
        mandatory: {
          maxWidth: 320,

          maxFrameRate: 15,

          minFrameRate: 15,
        },
      },
    };

    let options = {
      mediaConstraints: constraints,
      onicecandidate: (candidate) => {
        console.log("Local candidate" + JSON.stringify(candidate));
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: username,
        };
        sendMessage(message);
      },
    };

    user.rtcPeer = WebRtcPeer.WebRtcPeerSendonly(options);
    user.rtcPeer.generateOffer((err, offerSdp) => {
      if (err) {
        console.error(err);
      }
      let message = {
        id: "receiveVideoFrom",

        sender: username,
        sdpOffer: offerSdp,
      };
      sendMessage(message);
    });

    for (let [msgUserName] of Object.entries(msg.data.participants)) {
      if (msgUserName !== username) {
        console.log("receive", msgUserName);
        receiveVideo(msgUserName);
      }
    }
  }

  const onNewParticipant = (msg) => {
    receiveVideo(msg.name);
  };

  const onReceiveVideoAnswer = (msg) => {
    console.log(msg);

    participantsVideo[participantsName.indexOf(msg.name)].rtcPeer.processAnswer(
      msg.sdpAnswer
    );
  };

  const onAddIceCandidate = (msg) => {
    participantsVideo[
      participantsName.indexOf(msg.name)
    ].rtcPeer.addIceCandidate(msg.candidate);
  };

  const [name, setName] = useState(true);
  const onBtnClick = (name) => {
    setName(false);
  };
  // console.log(participants_name);

  return (
    <div className="App">
      <header className="App-header">
        {name && <button onClick={onBtnClick}></button>}
        {!name && (
          <>
            <h1>참가자 수: {participantsName.length}</h1>
            <VideoRoom
              participantsVideo={participantsVideo}
              participantsName={participantsName}
            ></VideoRoom>
          </>
        )}
      </header>
    </div>
  );
}
export default Game;
