import React, { useState, useEffect } from "react";

function Timer({ endTime }) {
  const [seconds, setSeconds] = useState(endTime);

  useEffect(() => {
    const countdown = setInterval(() => {
      if (parseInt(seconds) > 0) {
        setSeconds(parseInt(seconds) - 1);
      }
      if (parseInt(seconds) === 0) {
        clearInterval(countdown);
      }
    }, 1000);
    return () => clearInterval(countdown);
  }, [seconds]);

  return (
    <div>
      <div>
        <h2>{seconds}</h2>
      </div>
    </div>
  );
}
export default Timer;
