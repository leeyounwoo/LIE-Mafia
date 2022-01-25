import { useRef, useEffect } from "react";

export default function UserCam() {
  // 비디오 태그에 어떤 화상을 보여줄지 저장하는 변수
  const videoRef = useRef(null);

  useEffect(() => {
    const getUserMedia = async () => {
      try {
        // 내 화상화면을 저장하는 stream
        const stream = await navigator.mediaDevices.getUserMedia({
          video: true,
        });
        // 비디오 태그에 어떤 화상을 보여줄지 저장하는 변수에 내 화상화면을 저장
        videoRef.current.srcObject = stream;
      } catch (err) {
        console.log(err);
      }
    };
    // 화상화면을 저장하는 함수
    getUserMedia();
  }, []);

  return (
    <div>
      <h1>사용자</h1>
      <video ref={videoRef} autoPlay />
    </div>
  );
}
