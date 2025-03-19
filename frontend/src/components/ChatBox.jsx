import React, { useState, useEffect, useRef } from 'react';
import WebSocketService from '../services/WebSocketService';

const ChatBox = ({ lobbyId, playerColor, isWhiteTurn }) => {
    const [messages, setMessages] = useState([]);
    const [inputMessage, setInputMessage] = useState('');
    const [playerName, setPlayerName] = useState('Player ' + Math.floor(Math.random() * 1000));
    const messagesEndRef = useRef(null);

    useEffect(() => {
        // Subscribe to chat messages
        WebSocketService.subscribeToChatMessages(lobbyId, handleChatMessage);

        return () => {
            WebSocketService.unsubscribeFromChat(lobbyId);
        };
    }, [lobbyId]);

    const handleChatMessage = (message) => {
        if (message.type === 'CHAT' || message.type === 'MOVE_COMMAND') {
            setMessages(prev => [...prev, message]);
            scrollToBottom();
        }
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const sendMessage = (e) => {
        e.preventDefault();
        if (!inputMessage.trim()) return;

        // Check if it's the player's turn
        if (playerColor && ((playerColor === 'white' && !isWhiteTurn) || (playerColor === 'black' && isWhiteTurn))) {
            alert("It's not your turn!");
            return;
        }

        const chatMessage = {
            playerName: `${playerName} (${playerColor})`,
            content: inputMessage,
        };

        WebSocketService.sendChatMessage(lobbyId, chatMessage);
        setInputMessage('');
    };

    return (
        <div className="chat-box" style={styles.container}>
            <div className="messages" style={styles.messages}>
                {messages.map((msg, index) => (
                    <div key={index} style={styles.message}>
                        <strong>{msg.playerName}:</strong> {msg.content}
                    </div>
                ))}
                <div ref={messagesEndRef} />
            </div>
            <form onSubmit={sendMessage} style={styles.form}>
                <input
                    type="text"
                    value={inputMessage}
                    onChange={(e) => setInputMessage(e.target.value)}
                    placeholder={playerColor ? 
                        `Type a message or chess move (e.g., "Knight to f3")...` : 
                        "Join a color to chat and make moves..."}
                    style={styles.input}
                    disabled={!playerColor}
                />
                <button 
                    type="submit" 
                    style={{
                        ...styles.button,
                        opacity: playerColor ? 1 : 0.5,
                        cursor: playerColor ? 'pointer' : 'not-allowed'
                    }}
                    disabled={!playerColor}
                >
                    Send
                </button>
            </form>
        </div>
    );
};

const styles = {
    container: {
        width: '300px',
        height: '400px',
        border: '1px solid #ccc',
        borderRadius: '4px',
        display: 'flex',
        flexDirection: 'column',
        margin: '10px',
    },
    messages: {
        flex: 1,
        overflowY: 'auto',
        padding: '10px',
        backgroundColor: 'black',
    },
    message: {
        marginBottom: '8px',
        wordBreak: 'break-word',
    },
    form: {
        display: 'flex',
        padding: '10px',
        borderTop: '1px solid #ccc',
    },
    input: {
        flex: 1,
        marginRight: '8px',
        padding: '8px',
        borderRadius: '4px',
        border: '1px solid #ccc',
    },
    button: {
        padding: '8px 16px',
        backgroundColor: '#007bff',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
    },
};

export default ChatBox; 