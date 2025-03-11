import { useEffect } from "react";
import io from "socket.io-client";

const socket = io("http://localhost:8080");

const useWebSocket = () => {
    useEffect(() => {
        socket.on("move", (data) => {
            console.log("New move received:", data);
        });

        return () => socket.disconnect();
    }, []);
};

export default useWebSocket;
