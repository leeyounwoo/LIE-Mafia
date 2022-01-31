import { WebRtcPeer } from "kurento-utils";

class SignalApp {
  constructor(props) {
    this.ws = new WebSocket("wss://3.37.1.251:8443/groupcall");
    this.name = props.name;
    this.room = props.room;
    this.subscribers_name = [];
    this.Subscribers_video = [];

    this.ws.onopen = () => {
      console.log("join");
      let message = {
        id: "joinRoom",
        name: this.name,
        room: "",
      };
      this.sendMessage(message);
    };

    this.ws.onmessage = function (message) {
      var parsedMessage = JSON.parse(message.data);

      console.info("Received message: " + message.data);

      switch (parsedMessage.id) {
        // 현재 서버에 연결된 사용자 정보를 가져온다.
        case "existingParticipants":
          this.onExistingParticipants(parsedMessage);
          break;

        case "newParticipantArrived":
          this.onNewParticipant(parsedMessage);
          break;

        // case "participantLeft":
        //   onParticipantLeft(parsedMessage);
        //   break;

        case "receiveVideoAnswer":
          this.receiveVideoResponse(parsedMessage);
          break;

        case "iceCandidate":
          this.subscribers_video[
            this.subscribers_name.indexOf(parsedMessage.name)
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

    this.ws.onclose = (event) => {
      console.log(event);
    };

    this.ws.onerror = (error) => {
      console.log(error);
    };
  }

  sendMessage(message) {
    var jsonMessage = JSON.stringify(message);
    console.log("Sending message: " + jsonMessage);
    this.ws.send(jsonMessage);
  }

  async receiveVideo(sender) {
    let user = {
      name: sender.name,
      type: "remote",
      rtcPeer: null,
    };

    // setSubscrivers_name()
    // subscribers_name.push(name);
    // subscribers_video.push(user);
    this.subscribers_name.push(sender.name);
    this.subscribers_video.push(user);
    // setSubscribers_name([...subscribers_name, name]);
    // setSubscribers_video([...subscribers_video, user]);

    let options = {
      onicecandidate: (candidate) => {
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: sender.name,
        };
        this.sendMessage(message);
      },
    };

    user.rtcPeer = WebRtcPeer.WebRtcPeerRecvonly(options);
    user.rtcPeer.generateOffer((err, offerSdp) => {
      if (err) {
        console.error(err);
      }
      let msg = {
        id: "receiveVideoForm",
        sendes: sender.name,
        sdpOffer: offerSdp,
      };
      this.sendMessage(msg);
    });
  }

  async onExistingParticipants(msg) {
    let user = {
      name: msg.name,
      type: "local",
      rtcPeer: null,
    };

    // subscribers_name.push(name);
    // subscribers_video.push(user);

    this.subscribers_name.push(msg.name);
    this.subscribers_video.push(user);

    // setSubscribers_name([...subscribers_name, name]);
    // setSubscribers_video([...subscribers_video, user]);

    console.log(msg);
    console.log(msg.name + " registered in room " + this.room);

    let options = {
      onicecandidate: (candidate) => {
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: msg.name,
        };
        this.sendMessage(message);
      },
    };

    // this.offerToReceiveVideo = function (error, offerSdp, wp) {
    //   if (error) return console.error("sdp offer error");

    //   console.log("Invoking SDP offer callback function");

    //   var msg = { id: "receiveVideoFrom", sender: name, sdpOffer: offerSdp };

    //   sendMessage(msg);
    // };

    user.rtcPeer = WebRtcPeer.WebRtcPeerSendonly(options);
    user.rtcPeer.generateOffer((err, offerSdp) => {
      if (err) {
        console.error(err);
      }
      let msg = {
        id: "receiveVideoForm",
        sendes: this.name,
        sdpOffer: offerSdp,
      };
      this.sendMessage(msg);
    });

    msg.data.forEach((existingUser) => {
      this.receiveVideo(existingUser.name);
    });
  }

  onNewParticipant(request) {
    this.receiveVideo(request.name);
  }

  receiveVideoResponse(result) {
    this.subscribers_video[
      this.subscribers_name.indexOf(result.name)
    ].rtcPeer.processAnswer(
      result.sdpAnswer,

      function (error) {
        if (error) return console.error(error);
      }
    );
  }

  // this.subscribers_name = [];
  // this.subscribers_name = subscribers_name;
  // this.subscribers_video = [];
  // this.subscribers_video = subscribers_video;
  // console.log("client1", subscribers_name);
  // console.log("client2", this.subscribers_name);
}

export default SignalApp;
