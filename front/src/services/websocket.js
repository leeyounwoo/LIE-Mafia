import { WebRtcPeer } from "kurento-utils";

class SignalApp {
  constructor(roomId) {
    this.ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
    this._participants = {};
    this.roomId = roomId;
    this.userName = `user${this.roomId}`;
    this.user = {};
    console.log("내부 방번호", this.roomId);
    console.log("내부 방번호", typeof this.roomId);

    if (this.roomId === "0") {
      this.ws.onopen = () => {
        console.log("연결");
        var message = {
          id: "create",
          username: this.userName,
        };

        this.sendMessage(message);
      };
    } else {
      this.ws.onopen = () => {
        console.log("연결");
        var message = {
          id: "join",
          username: this.userName,
          roomId: this.roomId,
        };

        this.sendMessage(message);
      };
    }

    this.ws.onmessage = (message) => {
      var parsedMessage = JSON.parse(message.data);
      console.info("Received message: " + message.data);

      switch (parsedMessage.id) {
        case "existingParticipants":
          this.onExistingParticipants(parsedMessage);
          break;

        case "newParticipantArrived":
          this.onNewParticipant(parsedMessage);
          break;

        case "receiveVideoAnswer":
          this.onReceiveVideoAnswer(parsedMessage);
          console.log(this._participants);
          break;

        case "iceCandidate":
          this.onAddIceCandidate(parsedMessage);
          break;

        default:
          console.error("Unrecognized message", parsedMessage);
      }
    };

    this.ws.onerror = (error) => {
      console.log(error);
    };

    this.ws.onclose = (event) => {
      console.log(event);
    };
  }

  sendMessage(message) {
    var jsonMessage = JSON.stringify(message);
    this.ws.send(jsonMessage);
    console.log("Sending message: " + jsonMessage);
  }

  async receiveVideo(sender) {
    let user = {
      name: sender,
      type: "remote",
      rtcPeer: null,
    };

    this._participants[sender] = user;

    let options = {
      onicecandidate: (candidate) => {
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: sender,
        };
        this.sendMessage(message);
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
      this.sendMessage(msg);
    });
  }

  async onExistingParticipants(msg) {
    let user = {
      name: this.userName,
      // name: msg.name,
      type: "local",
      rtcPeer: null,
    };

    this._participants[this.userName] = user;
    // this._participants[msg.name] = user;

    console.log(msg);
    console.log(this.userName + " registered in room " + msg.room);

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
        // console.log("Local candidate" + JSON.stringify(candidate));
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: this.userName,
        };
        this.sendMessage(message);
      },
    };

    user.rtcPeer = WebRtcPeer.WebRtcPeerSendonly(options);
    user.rtcPeer.generateOffer((err, offerSdp) => {
      if (err) {
        console.error(err);
      }
      let message = {
        id: "receiveVideoFrom",

        sender: this.userName,
        sdpOffer: offerSdp,
      };
      console.log(message);
      this.sendMessage(message);
    });

    for (let [username] of Object.entries(msg.data.participants)) {
      console.log("username", username);
      if (username !== this.userName) {
        console.log("receive", username);
        this.receiveVideo(username);
      }
    }

    // msg.data.forEach((existingUser) => {
    //   this.receiveVideo(existingUser.name);
    // });
  }

  onNewParticipant(msg) {
    this.receiveVideo(msg.name);
  }

  onReceiveVideoAnswer(msg) {
    console.log(msg);
    this._participants[msg.name].rtcPeer.processAnswer(msg.sdpAnswer);
  }

  onAddIceCandidate(msg) {
    this._participants[msg.name].rtcPeer.addIceCandidate(msg.candidate);
  }
}

export default SignalApp;
