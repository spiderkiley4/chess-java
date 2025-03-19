import React, { useState, useEffect } from 'react';
import Board from './Board';
import ChatBox from './ChatBox';
import WebSocketService from '../services/WebSocketService';

const Game = ({ lobbyId }) => {
    const [playerColor, setPlayerColor] = useState(null);
    const [isWhiteTurn, setIsWhiteTurn] = useState(true);

    useEffect(() => {
        const handleGameMessage = (message) => {
            if (message.type === "STATE" || message.type === "PLAYERS") {
                const players = message.content.players;
                if (players) {
                    // Determine player color based on player IDs
                    if (players.whitePlayerId === WebSocketService.getSessionId()) {
                        setPlayerColor('white');
                    } else if (players.blackPlayerId === WebSocketService.getSessionId()) {
                        setPlayerColor('black');
                    }
                }
            }
            if (message.whiteTurn !== undefined) {
                setIsWhiteTurn(message.whiteTurn);
            }
        };

        WebSocketService.subscribe(lobbyId, handleGameMessage);
        WebSocketService.joinGame(lobbyId);

        return () => {
            WebSocketService.unsubscribe(lobbyId);
        };
    }, [lobbyId]);

    return (
        <div style={styles.container}>
            <Board lobbyId={lobbyId} />
            <ChatBox 
                lobbyId={lobbyId} 
                playerColor={playerColor}
                isWhiteTurn={isWhiteTurn}
            />
        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'flex-start',
        padding: '20px',
        gap: '20px',
    },
};

export default Game; 