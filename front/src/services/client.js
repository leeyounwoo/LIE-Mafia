import { WebRtcPeer } from "kurento-utils";
import { useState } from "react";

function SignalApp({ name, room }) {
  const ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
  const [subscribers_name, setSubscribers_name] = useState([]);
  const [subscribers_video, setSubscribers_video] = useState([]);
  console.log("시작", subscribers_name);

  ws.onopen = () => {
    console.log("join");
    var message = {
      id: "joinRoom",
      name: name,
      room: "",
    };
    sendMessage(message);
  };

  ws.onmessage = function (message) {
    var parsedMessage = JSON.parse(message.data);

    console.info("Received message: " + message.data);

    switch (parsedMessage.id) {
      // 현재 서버에 연결된 사용자 정보를 가져온다.
      case "existingParticipants":
        onExistingParticipants(parsedMessage);
        break;

      case "newParticipantArrived":
        onNewParticipant(parsedMessage);
        break;

      // case "participantLeft":
      //   onParticipantLeft(parsedMessage);
      //   break;

      case "receiveVideoAnswer":
        receiveVideoResponse(parsedMessage);
        break;

      case "iceCandidate":
        subscribers_video[
          subscribers_name.indexOf(parsedMessage.name)
        ].rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
          if (error) {
            console.error("Error adding candidate: " + error);
            return;
          }
        });

        break;

      default:
        console.error("Unrecognized message", parsedMessage);
    }
  };

  ws.onclose = (event) => {
    console.log(event);
  };

  ws.onerror = (error) => {
    console.log(error);
  };

  function sendMessage(message) {
    var jsonMessage = JSON.stringify(message);
    console.log("Sending message: " + jsonMessage);
    ws.send(jsonMessage);
  }

  function receiveVideo(sender) {
    let user = {
      name: name,
      type: "remote",
      rtcPeer: null,
    };

    // setSubscrivers_name()
    // subscribers_name.push(name);
    // subscribers_video.push(user);
    setSubscribers_name([...subscribers_name, name]);
    setSubscribers_video([...subscribers_video, user]);

    let options = {
      onicecandidate: (candidate) => {
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: name,
        };
        sendMessage(message);
      },
    };

    user.rtcPeer = WebRtcPeer.WebRtcPeerRecvonly(options);
    user.rtcPeer.generateOffer((err, offerSdp) => {
      if (err) {
        console.error(err);
      }
      let msg = {
        id: "receiveVideoForm",
        sendes: name,
        sdpOffer: offerSdp,
      };
      sendMessage(msg);
    });
  }

  function onExistingParticipants(msg) {
    let user = {
      name: name,
      type: "local",
      rtcPeer: null,
    };

    // subscribers_name.push(name);
    // subscribers_video.push(user);

    setSubscribers_name([...subscribers_name, name]);
    setSubscribers_video([...subscribers_video, user]);

    console.log(msg);
    console.log(name + " registered in room " + room);

    let options = {
      onicecandidate: (candidate) => {
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: name,
        };
        sendMessage(message);
      },
    };

    user.rtcPeer = WebRtcPeer.WebRtcPeerSendonly(options);
    user.rtcPeer.generateOffer((err, offerSdp) => {
      if (err) {
        console.error(err);
      }
      let msg = {
        id: "receiveVideoForm",
        sendes: name,
        sdpOffer: offerSdp,
      };
      sendMessage(msg);
    });

    msg.data.forEach((existingUser) => {
      receiveVideo(existingUser.name);
    });
  }

  function onNewParticipant(request) {
    receiveVideo(request.name);
  }

  function receiveVideoResponse(result) {
    subscribers_video[
      subscribers_name.indexOf(result.name)
    ].rtcPeer.processAnswer(
      result.sdpAnswer,

      function (error) {
        if (error) return console.error(error);
      }
    );
  }

  this.subscribers_name = [];
  this.subscribers_name = subscribers_name;
  this.subscribers_video = [];
  this.subscribers_video = subscribers_video;
  console.log("client1", subscribers_name);
  console.log("client2", this.subscribers_name);
}

export default SignalApp;
