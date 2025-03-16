import React from 'react';
import Board from './Board';
import ChatBox from './ChatBox';

const Game = ({ lobbyId }) => {
    return (
        <div style={styles.container}>
            <Board lobbyId={lobbyId} />
            <ChatBox lobbyId={lobbyId} />
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