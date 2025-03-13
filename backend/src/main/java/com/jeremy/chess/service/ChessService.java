package com.jeremy.chess.service;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.Lobby;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChessService {
    private static final Logger logger = LoggerFactory.getLogger(ChessService.class);
    private final Map<String, Lobby> lobbies = new HashMap<>();

    public Lobby createLobby() {
        Lobby lobby = new Lobby();
        lobbies.put(lobby.getId(), lobby);
        return lobby;
    }

    public ArrayList<String> getBoardState(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            logger.info("Returning board state for lobby {}: {}", lobbyId, lobby.getBoardState());
            return lobby.getBoardState();
        } else {
            logger.warn("Lobby {} not found", lobbyId);
            return null;
        }
    }

    public ArrayList<String> makeMove(String lobbyId, ChessMove chessMove) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            logger.warn("Lobby {} not found", lobbyId);
            return null;
        }

        try {
            ArrayList<String> boardState = lobby.getBoardState();
            
            // Update the board state based on the move
            int sourceIndex = chessMove.getSourceIndex();
            int targetIndex = chessMove.getTargetIndex();
            String piece = boardState.get(sourceIndex);
            
            boardState.set(sourceIndex, ""); // Clear the source square
            boardState.set(targetIndex, piece); // Move the piece to the target square
            
            lobby.setBoardState(boardState);
            logger.info("Move made in lobby {}: {} to {}", lobbyId, chessMove.getFrom(), chessMove.getTo());
            logger.info("Updated board state for lobby {}: {}", lobbyId, boardState);
            return boardState;
        } catch (Exception e) {
            logger.error("Error making move in lobby {}: {}", lobbyId, e.getMessage());
            return null;
        }
    }

    public Collection<Lobby> getLobbies() {
        return lobbies.values();
    }
}
