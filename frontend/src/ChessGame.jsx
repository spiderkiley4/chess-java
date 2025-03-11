import React, { useState, useEffect } from "react";
import { Chessboard } from "react-chessboard";

const ChessBoard = ({ lobbyId }) => {
    const [position, setPosition] = useState("start");

    // Fetch the board state from the backend
    const fetchBoardState = async () => {
        try {
            const response = await fetch(`http://localhost:8080/game/state/${lobbyId}`);
            const fen = await response.text();
            setPosition(fen);
        } catch (error) {
            console.error("Error fetching board state:", error);
        }
    };

    useEffect(() => {
        fetchBoardState(); // Load the board when the component mounts
    }, [lobbyId]);

    // Send move to backend and update board state
    const handleMove = async (sourceSquare, targetSquare) => {
        try {
            const response = await fetch(`http://localhost:8080/game/move/${lobbyId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ from: sourceSquare, to: targetSquare }),
            });

            const fen = await response.text();
            if (fen !== "Invalid move!") {
                setPosition(fen);
            } else {
                console.log("Illegal move!");
            }
        } catch (error) {
            console.error("Failed to send move:", error);
        }
    };

    return (
        <div style={{ width: "500px", margin: "auto" }}>
            <Chessboard position={position} onPieceDrop={handleMove} />
        </div>
    );
};

const MainMenu = () => {
    const [lobbyId, setLobbyId] = useState(null);

    const createLobby = async () => {
        try {
            const response = await fetch("http://localhost:8080/game/lobby", {
                method: "POST",
            });
            const lobby = await response.json();
            setLobbyId(lobby.id);
        } catch (error) {
            console.error("Error creating lobby:", error);
        }
    };

    return (
        <div>
            {lobbyId ? (
                <ChessBoard lobbyId={lobbyId} />
            ) : (
                <button onClick={createLobby}>Create Lobby</button>
            )}
        </div>
    );
};

export default MainMenu;
