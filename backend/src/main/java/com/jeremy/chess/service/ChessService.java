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

    public Map<String, String> getBoardState(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            logger.info("Returning board state for lobby {}: {}", lobbyId, lobby.getBoardState());
            return convertBoardStateToMap(lobby.getBoardState());
        } else {
            logger.warn("Lobby {} not found", lobbyId);
            return null;
        }
    }

    public Map<String, String> makeMove(String lobbyId, ChessMove chessMove) {
        try {
            Lobby lobby = lobbies.get(lobbyId);
            if (lobby == null) {
                throw new IllegalArgumentException("Lobby not found");
            }
            ArrayList<String> boardState = lobby.getBoardState();
            
            // Update the board state based on the move
            int sourceIndex = chessMove.getSourceIndex();
            int targetIndex = chessMove.getTargetIndex();
            String piece = boardState.get(sourceIndex);
            
            boardState.set(sourceIndex, ""); // Clear the source square
            if (chessMove.getPromotion() != null && piece.equals("wP") && targetIndex < 8) {
                piece = chessMove.getPromotion(); // Promote white pawn
            } else if (chessMove.getPromotion() != null && piece.equals("bP") && targetIndex >= 56) {
                piece = chessMove.getPromotion(); // Promote black pawn
            }
            boardState.set(targetIndex, piece); // Move the piece to the target square
            
            lobby.setBoardState(boardState);
            logger.info("Move made in lobby {}: {} to {}", lobbyId, chessMove.getFrom(), chessMove.getTo());
            logger.info("Updated board state for lobby {}: {}", lobbyId, boardState);
            return convertBoardStateToMap(boardState);
        } catch (Exception e) {
            logger.error("Error making move in lobby {}: {}", lobbyId, e.getMessage());
            return null;
        }
    }

    public Collection<Lobby> getLobbies() {
        return lobbies.values();
    }

    public void disconnect(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobbies.remove(lobbyId);
            logger.info("Lobby {} removed due to user disconnection", lobbyId);
        } else {
            logger.warn("Lobby {} not found for disconnection", lobbyId);
        }
    }

    private Map<String, String> convertBoardStateToMap(ArrayList<String> boardState) {
        Map<String, String> boardMap = new HashMap<>();
        String[] squares = {
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        };
        for (int i = 0; i < boardState.size(); i++) {
            if (!boardState.get(i).isEmpty()) {
                boardMap.put(squares[i], boardState.get(i));
            }
        }
        return boardMap;
    }
}
