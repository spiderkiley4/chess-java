import React, { useState, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import WebSocketService from "./services/WebSocketService";

const ChessBoard = ({ lobbyId }) => {
    const [position, setPosition] = useState("start");
    const [error, setError] = useState(null);
    const [isWhiteTurn, setIsWhiteTurn] = useState(true);
    const [playerColor, setPlayerColor] = useState(null);
    const [players, setPlayers] = useState({ whitePlayerId: "", blackPlayerId: "" });
    const [chatMessages, setChatMessages] = useState([]);
    const [chatInput, setChatInput] = useState("");
    const [gameOver, setGameOver] = useState(false);
    const [winningTeam, setWinningTeam] = useState(null);
    const [gameEndReason, setGameEndReason] = useState(null);
    const [inCheck, setInCheck] = useState(false);

    const handleGameMessage = (message) => {
        console.log('Received game message:', message);
        
        // Update turn state from any message that includes it
        if (message.whiteTurn !== undefined) {
            console.log('Updating turn state:', message.whiteTurn);
            setIsWhiteTurn(message.whiteTurn);
        }

        // Update game end state if available
        if (message.gameOver !== undefined) {
            setGameOver(message.gameOver);
            setWinningTeam(message.winningTeam);
            setGameEndReason(message.gameEndReason);
        }

        // Update check status if available
        if (message.inCheck !== undefined) {
            setInCheck(message.inCheck);
        }

        if (message.type === "MOVE") {
            const boardState = message.content;
            console.log('Setting new position:', boardState);
            setPosition(boardState || "start");
        } else if (message.type === "STATE") {
            console.log('Received game state:', message.content);
            const { boardState, players } = message.content;
            setPosition(boardState || "start");
            setPlayers(players);
            updatePlayerColor(players);
        } else if (message.type === "PLAYERS") {
            console.log('Received players update:', message.content);
            const players = message.content;
            setPlayers(players);
            updatePlayerColor(players);
        } else if (message.type === "CHAT") {
            console.log('Received chat message:', message.content);
            setChatMessages(prev => [...prev, message.content]);
        }
    };

    const updatePlayerColor = (players) => {
        const sessionId = WebSocketService.getSessionId();
        console.log('Checking player color:', {
            sessionId,
            whitePlayerId: players.whitePlayerId,
            blackPlayerId: players.blackPlayerId
        });
        if (sessionId === players.whitePlayerId) {
            console.log('Setting player as white');
            setPlayerColor('white');
        } else if (sessionId === players.blackPlayerId) {
            console.log('Setting player as black');
            setPlayerColor('black');
        } else {
            console.log('Player has no color assigned');
            setPlayerColor(null);
        }
    };

    const handleMove = (sourceSquare, targetSquare, piece) => {
        try {
            console.log('Move attempt:', {
                piece,
                sourceSquare,
                targetSquare,
                playerColor,
                isWhiteTurn,
                players
            });

            // Check if it's the player's turn
            const isWhitePiece = piece.charAt(0) === 'w';
            const canMove = (isWhitePiece && playerColor === 'white' && isWhiteTurn) || 
                          (!isWhitePiece && playerColor === 'black' && !isWhiteTurn);
            
            console.log('Move validation:', {
                isWhitePiece,
                playerColor,
                isWhiteTurn,
                canMove,
                turnCheck: isWhitePiece ? isWhiteTurn : !isWhiteTurn,
                colorCheck: isWhitePiece ? playerColor === 'white' : playerColor === 'black'
            });

            if ((isWhitePiece && playerColor !== 'white') || (!isWhitePiece && playerColor !== 'black')) {
                console.log('Move rejected: wrong piece color');
                setError("You can only move your own pieces");
                return false;
            }

            if ((isWhitePiece && !isWhiteTurn) || (!isWhitePiece && isWhiteTurn)) {
                console.log('Move rejected: wrong turn');
                setError("It's not your turn");
                return false;
            }

            console.log(`Attempting move: ${piece} from ${sourceSquare} to ${targetSquare}`);
            const move = {
                from: sourceSquare,
                to: targetSquare,
                promotion: null
            };

            const success = WebSocketService.sendMove(lobbyId, move);
            if (!success) {
                setError("Failed to send move - connection issue");
                return false;
            }
            console.log('Move sent successfully');
            return true;
        } catch (error) {
            console.error('Error handling move:', error);
            setError("Failed to process move");
            return false;
        }
    };

    const handlePromotionPieceSelect = (piece, promoteFromSquare, promoteToSquare) => {
        try {
            // Check if it's the player's turn
            const isWhitePiece = piece.charAt(0) === 'w';
            if ((isWhitePiece && playerColor !== 'white') || (!isWhitePiece && playerColor !== 'black')) {
                setError("You can only promote your own pieces");
                return false;
            }

            if ((isWhitePiece && !isWhiteTurn) || (!isWhitePiece && isWhiteTurn)) {
                setError("It's not your turn");
                return false;
            }

            console.log(`Promotion move: ${piece} from ${promoteFromSquare} to ${promoteToSquare}`);
            const move = {
                from: promoteFromSquare,
                to: promoteToSquare,
                promotion: piece.charAt(1)
            };

            const success = WebSocketService.sendMove(lobbyId, move);
            if (!success) {
                setError("Failed to send promotion move - connection issue");
                return false;
            }
            return true;
        } catch (error) {
            console.error('Error handling promotion:', error);
            setError("Failed to process promotion");
            return false;
        }
    };

    const isPieceDraggable = (piece) => {
        if (!playerColor) return false;
        const isWhitePiece = piece.charAt(0) === 'w';
        return (isWhitePiece && playerColor === 'white' && isWhiteTurn) ||
               (!isWhitePiece && playerColor === 'black' && !isWhiteTurn);
    };

    const sendChatMessage = (e) => {
        e.preventDefault();
        if (!chatInput.trim()) return;

        const message = {
            playerName: `Player (${playerColor})`,
            content: chatInput
        };

        WebSocketService.sendChatMessage(lobbyId, message);
        setChatInput("");
    };

    useEffect(() => {
        console.log('ChessBoard mounted with lobbyId:', lobbyId);
        
        // Connect to WebSocket when component mounts
        WebSocketService.connect(() => {
            console.log('WebSocket connected, subscribing to game updates...');
            // Subscribe to game updates after connection is established
            WebSocketService.subscribe(lobbyId, handleGameMessage);
            // Join the game
            WebSocketService.joinGame(lobbyId);
        });

        const handleBeforeUnload = () => {
            WebSocketService.disconnect();
        };

        window.addEventListener("beforeunload", handleBeforeUnload);

        return () => {
            console.log('ChessBoard unmounting...');
            window.removeEventListener("beforeunload", handleBeforeUnload);
            WebSocketService.unsubscribe(lobbyId);
            WebSocketService.disconnect();
        };
    }, [lobbyId]);

    return (
        <div className="flex gap-5 max-w-7xl mx-auto p-5">
            <div className="flex-1">
                {error && <div className="text-red-500 mb-3">{error}</div>}
                <div className="mb-3">
                    {!playerColor && (
                        <div className="mb-3">
                            <button 
                                onClick={() => WebSocketService.claimColor(lobbyId, "white")}
                                disabled={players.whitePlayerId !== ""}
                                className={`mr-3 px-4 py-2 rounded-md border ${
                                    players.whitePlayerId !== "" 
                                    ? "bg-gray-200 text-gray-500 cursor-not-allowed" 
                                    : "bg-white hover:bg-gray-50 border-gray-300"
                                }`}
                            >
                                Play as White
                            </button>
                            <button 
                                onClick={() => WebSocketService.claimColor(lobbyId, "black")}
                                disabled={players.blackPlayerId !== ""}
                                className={`px-4 py-2 rounded-md border ${
                                    players.blackPlayerId !== "" 
                                    ? "bg-gray-200 text-gray-500 cursor-not-allowed" 
                                    : "bg-white hover:bg-gray-50 border-gray-300"
                                }`}
                            >
                                Play as Black
                            </button>
                        </div>
                    )}
                    {playerColor && (
                        <div>
                            <p className="text-lg">You are playing as {playerColor}</p>
                            <p className="text-lg">Current turn: {isWhiteTurn ? "White" : "Black"}</p>
                            {inCheck && !gameOver && (
                                <div className="mt-2 p-2 bg-red-100 border border-red-200 rounded-md">
                                    <p className="text-red-700 font-semibold">CHECK!</p>
                                </div>
                            )}
                            {gameOver && (
                                <div className="mt-3 p-4 bg-blue-100 border border-blue-200 rounded-md">
                                    <p className="text-lg font-semibold">Game Over!</p>
                                    <p>{winningTeam === "Draw" ? "Game ended in a draw" : `${winningTeam} wins!`}</p>
                                    <p className="text-sm text-gray-600">Reason: {gameEndReason}</p>
                                </div>
                            )}
                        </div>
                    )}
                </div>
                <Chessboard
                    position={position}
                    onPieceDrop={handleMove}
                    onPromotionPieceSelect={handlePromotionPieceSelect}
                    boardOrientation={playerColor === "black" ? "black" : "white"}
                    arePiecesDraggable={!gameOver && isPieceDraggable}
                />
            </div>
            <div className="flex-none w-72 flex flex-col h-[500px] border border-gray-200 rounded-lg bg-white">
                <div className="flex-1 overflow-y-auto p-3 flex flex-col-reverse">
                    <div>
                        {chatMessages.map((msg, index) => (
                            <div key={index} className="mb-2 p-2 bg-gray-50 rounded">
                                <strong>{msg.playerName}:</strong> {msg.content}
                            </div>
                        ))}
                    </div>
                </div>
                <form onSubmit={sendChatMessage} className="border-t border-gray-200 p-3 flex gap-2">
                    <input
                        type="text"
                        value={chatInput}
                        onChange={(e) => setChatInput(e.target.value)}
                        placeholder="Type a message..."
                        className="flex-1 px-3 py-2 rounded border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <button 
                        type="submit"
                        disabled={!playerColor}
                        className={`px-4 py-2 rounded border ${
                            playerColor 
                            ? "bg-blue-500 text-white hover:bg-blue-600 border-blue-600" 
                            : "bg-gray-200 text-gray-500 cursor-not-allowed border-gray-300"
                        }`}
                    >
                        Send
                    </button>
                </form>
            </div>
        </div>
    );
};

const MainMenu = () => {
    const [lobbyId, setLobbyId] = useState(null);
    const [lobbies, setLobbies] = useState([]);
    const [lobbyName, setLobbyName] = useState("");

    const createLobby = async () => {
        try {
            const response = await fetch(`http://localhost:8080/game/lobby${lobbyName ? `?name=${encodeURIComponent(lobbyName)}` : ''}`, {
                method: "POST",
            });
            if (!response.ok) {
                throw new Error("Failed to create lobby");
            }
            const lobby = await response.json();
            console.log(lobby);
            setLobbyId(lobby.id);
        } catch (error) {
            console.error("Error creating lobby:", error);
        }
    };

    useEffect(() => {
        // Connect to WebSocket for lobby updates
        WebSocketService.connect(() => {
            WebSocketService.subscribeToLobbies((updatedLobbies) => {
                setLobbies(updatedLobbies);
            });
        });

        // Initial fetch of lobbies
        fetch("http://localhost:8080/game/lobbies")
            .then(response => response.json())
            .then(data => setLobbies(data))
            .catch(error => console.error("Error fetching lobbies:", error));

        return () => {
            WebSocketService.unsubscribeFromLobbies();
        };
    }, []);

    return (
        <div className="w-screen h-screen mx-auto p-5">
            {lobbyId ? (
                <ChessBoard lobbyId={lobbyId} />
            ) : (
                <div>
                    <div className="mb-5">
                        <input
                            type="text"
                            value={lobbyName}
                            onChange={(e) => setLobbyName(e.target.value)}
                            placeholder="Enter lobby name"
                            className="mr-3 px-3 py-2 rounded border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                        <button 
                            onClick={createLobby}
                            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                        >
                            Create Lobby
                        </button>
                    </div>
                    <h2 className="text-2xl font-bold mb-4">Available Lobbies</h2>
                    <ul className="space-y-3 list-none">
                        {lobbies.map((lobby) => (
                            <li key={lobby.id}>
                                <button 
                                    onClick={() => setLobbyId(lobby.id)}
                                    className="w-full text-left p-4 rounded-lg border border-gray-200 bg-white hover:bg-gray-50 transition-colors duration-150"
                                >
                                    <div className="block text-lg font-semibold m-2">{lobby.name}</div>
                                    <div className="text-sm text-gray-600">ID: {lobby.id.substring(0, 8)}</div>
                                    <div>Players: {(lobby.whitePlayerId ? 1 : 0) + (lobby.blackPlayerId ? 1 : 0)}/2</div>
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
