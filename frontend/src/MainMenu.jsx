import React, { useState, useEffect } from "react";
import { Chessboard } from "react-chessboard";

const ChessBoard = ({ lobbyId }) => {
    const [position, setPosition] = useState("start");
    const [error, setError] = useState(null);

    // Fetch the board state from the backend
    const fetchBoardState = async () => {
        try {
            const response = await fetch(`http://localhost:8080/game/state/${lobbyId}`);
            if (!response.ok) {
                throw new Error("Failed to fetch board state");
            }
            const fen = await response.text();
            setPosition(fen);
        } catch (error) {
            console.error("Error fetching board state:", error);
            setError("Error fetching board state");
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

            if (!response.ok) {
                throw new Error("Failed to send move");
            }

            const fen = await response.text();
            if (fen !== "Invalid move!") {
                setPosition(fen);
            } else {
                console.log("Illegal move!");
            }
        } catch (error) {
            console.error("Failed to send move:", error);
            setError("Failed to send move");
        }
    };

    return (
        <div style={{ width: "500px", margin: "auto" }}>
            {error && <div style={{ color: "red" }}>{error}</div>}
            <Chessboard position={position} onPieceDrop={handleMove} />
        </div>
    );
};

const MainMenu = () => {
    const [lobbyId, setLobbyId] = useState(null);
    const [lobbies, setLobbies] = useState([]);

    const createLobby = async () => {
        try {
            const response = await fetch("http://localhost:8080/game/lobby", {
                method: "POST",
            });
            if (!response.ok) {
                throw new Error("Failed to create lobby");
            }
            const lobby = await response.json();
            setLobbyId(lobby.id);
        } catch (error) {
            console.error("Error creating lobby:", error);
        }
    };

    const fetchLobbies = async () => {
        try {
            const response = await fetch("http://localhost:8080/game/lobbies");
            if (!response.ok) {
                throw new Error("Failed to fetch lobbies");
            }
            const lobbies = await response.json();
            setLobbies(lobbies);
        } catch (error) {
            console.error("Error fetching lobbies:", error);
        }
    };

    useEffect(() => {
        fetchLobbies();
    }, []);

    return (
        <div>
            {lobbyId ? (
                <ChessBoard lobbyId={lobbyId} />
            ) : (
                <div>
                    <button onClick={createLobby}>Create Lobby</button>
                    <h2>Available Lobbies</h2>
                    <ul>
                        {lobbies.map((lobby) => (
                            <li key={lobby.id}>
                                <button onClick={() => setLobbyId(lobby.id)}>
                                    Join Lobby {lobby.id}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default MainMenu;
