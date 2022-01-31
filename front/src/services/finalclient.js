import { WebRtcPeer } from "kurento-utils";

class SignalApp {
  constructor() {
    // const regex = /(^https?):\/\/\w+(:[0-9]*)?\/?/;
    // const lastIdx = regex.exec(window.location.href)[0].length;
    // this._currentLocation = window.location.href.slice(lastIdx - 1);

    this.ws = new WebSocket("wss://3.37.1.251:8443/groupcall");
    // this.ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
    this.roomName = 0;
    this._participants = {};
    this.userName = `user${Object.keys(this._participants).length}`;
    this.user = {};

    this.ws.onopen = () => {
      console.log("연결");
      var message = {
        id: "joinRoom",
        name: this.userName,
        room: this.roomName,
      };

      this.sendMessage(message);
    };

    // this.ws.onopen = () => {
    //   console.log("연결");
    //   var message = {
    //     id: "create",
    //     username: this.userName,
    //   };

    //   this.sendMessage(message);
    // };

    // this.ws.onopen = () => {
    //   var message = {
    //     id: "createUser",
    //     // 서버에서 사용 안함
    //     // currRoom: this._currentLocation,
    //   };
    //   this.sendMessage(message);
    // };

    this.ws.onmessage = (message) => {
      var parsedMessage = JSON.parse(message.data);
      console.info("Received message: " + message.data);

      switch (parsedMessage.id) {
        // case "userCreated":
        //   this.requestRoom(parsedMessage);
        //   break;

        // case "roomCreated":
        //   this.joinRoom(parsedMessage);
        //   break;

        case "existingParticipants":
          this.onExistingParticipants(parsedMessage);
          break;

        case "newParticipantArrived":
          this.onNewParticipant(parsedMessage);
          break;

        case "receiveVideoAnswer":
          this.onReceiveVideoAnswer(parsedMessage);
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

  // requestRoom(msg) {
  //   const room =
  //     this._currentLocation.length > 1 ? this._currentLocation.slice(1) : "";
  //   this.userName = msg.userName;

  //   var message = {
  //     id: "createRoom",
  //     room: room,
  //   };

  //   this.sendMessage(message);
  // }

  // joinRoom(msg) {
  //   this.roomName = msg.roomName;

  //   var message = {
  //     id: "joinRoom",
  //     userName: this.userName,
  //     roomName: this.roomName,
  //   };

  //   this.sendMessage(message);
  // }

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

    this._participants[msg.name] = user;

    console.log(msg);
    console.log(msg.name + " registered in room " + msg.room);

    let options = {
      onicecandidate: (candidate) => {
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: user.name,
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
        id: "receiveVideoForm",
        sendes: msg.name,
        sdpOffer: offerSdp,
      };
      this.sendMessage(message);
    });

    msg.data.forEach((existingUser) => {
      this.receiveVideo(existingUser.name);
    });
  }

  onNewParticipant(msg) {
    this.receiveVideo(msg.name);
  }

  receiveVideoResponse(result) {
    this._participants[result.name].rtcPeer.processAnswer(result.sdpAnswer);
  }

  onReceiveVideoAnswer(msg) {
    this._participants[msg.name].rtcPeer.processAnswer(msg.sdpAnswer);
  }

  onAddIceCandidate(msg) {
    this._participants[msg.name].rtcPeer.addIceCandidate(msg.candidate);
  }

  // get participants() {
  //   return this._participants;
  // }
  // get currentLocation() {
  //   return `${window.location.href}${this.roomName}`;
  // }
}

export default SignalApp;
