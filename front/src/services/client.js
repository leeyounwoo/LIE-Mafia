import kurentoUtils from "kurento-utils";

function SignalApp({ nickName }) {
  const ws = new WebSocket("wss://3.37.1.251:8443/groupcall");
  // 자기 자신
  const PARTICIPANT_MAIN_CLASS = "participant main";
  // 다른 사용자
  const PARTICIPANT_CLASS = "participant";
  // 사용자 닉네임 저장하는 배열
  let subscribers_name = [];
  // 사용자 participant 저장하는 배열
  let subscribers_video = [];
  // 방 번호
  var room = "";
  // 닉네임
  var name = nickName;
  ws.onopen = () => {
    console.log(nickName);

    var message = {
      id: "joinRoom",
      name: nickName,
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

      case "participantLeft":
        onParticipantLeft(parsedMessage);
        break;

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
}
