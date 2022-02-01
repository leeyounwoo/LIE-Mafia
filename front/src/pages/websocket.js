const ws = new WebSocket("ws://i6c209.p.ssafy.io:8080/connect");
ws.onopen = () => {
  console.log("연결");
};
export default ws;
