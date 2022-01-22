import { useRef, useEffect } from "react";
// import React, {Component} from 'react';

export default function MyVideo() {
  const videoRef = useRef(null);

  useEffect(() => {
    const getUserMedia = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: true,
        });
        videoRef.current.srcObject = stream;
      } catch (err) {
        console.log(err);
      }
    };
    getUserMedia();
  }, []);

  return (
    <div>
      <h1>로컬에 있는 화상 화면은 나와요!</h1>
      <video ref={videoRef} autoPlay />
    </div>
  );
}
