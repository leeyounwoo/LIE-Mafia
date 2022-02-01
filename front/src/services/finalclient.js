import { WebRtcPeer } from "kurento-utils";

class SignalApp {
  constructor(roomId) {
    // const regex = /(^https?):\/\/\w+(:[0-9]*)?\/?/;
    // const lastIdx = regex.exec(window.location.href)[0].length;
    // this._currentLocation = window.location.href.slice(lastIdx - 1);

    // this.ws = new WebSocket("wss://3.37.1.251:8443/groupcall");
    this.ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
    // this.roomName = 0;
    this._participants = {};
    this.roomId = roomId;
    this.userName = `user${this.roomId}`;
    this.user = {};
    console.log("내부 방번호", this.roomId);
    console.log("내부 방번호", typeof this.roomId);

    // console.log(this.temp);

    // this.ws.onopen = () => {
    //   console.log("연결");
    //   var message = {
    //     id: "joinRoom",
    //     name: this.userName,
    //     room: this.roomName,
    //   };

    //   this.sendMessage(message);
    // }
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
    // this.ws.onopen = () => {
    //   console.log("연결");
    //   var message = {
    //     id: "join",
    //     username: this.userName,
    //     roomId: "1",
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
        console.log("Local candidate" + JSON.stringify(candidate));
        let message = {
          id: "onIceCandidate",
          candidate: candidate,
          name: user.name,
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
        id: "receiveVideoForm",
        sendes: sender.name,
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
        console.log("Local candidate" + JSON.stringify(candidate));
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
    // console.log(this._participants);
    this._participants[msg.name].rtcPeer.processAnswer(msg.sdpAnswer);
    // console.log(this._participants);
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
