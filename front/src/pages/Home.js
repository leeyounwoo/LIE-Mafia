import React, { useState } from "react";
import { useHistory } from "react-router-dom";

function Home() {
  let history = useHistory();

  return (
    <div>
      <img alt="logo" src="img/logo.png" />
      <button
        onClick={() => {
          history.push("/Liemafia/0");
        }}
      >
        방 만들기
      </button>
    </div>
  );
}

export default Home;
