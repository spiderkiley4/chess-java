package com.jeremy.chess.service;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.Lobby;
import com.jeremy.chess.util.MoveValidator;
import com.jeremy.chess.util.ChessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service class that manages chess game lobbies and handles game logic.
 * 
 * @author Jeremy Kiley
 * @author ChatGPT
 */
@Service
public class ChessService {
    private static final Logger logger = LoggerFactory.getLogger(ChessService.class);
    private final Map<String, Lobby> lobbies = new HashMap<>();
    private final Map<String, String> playerColors = new HashMap<>();

    /**
     * Creates a new lobby with the specified name.
     * 
     * @param name The name of the lobby
     * @return The created lobby
     */
    public Lobby createLobby(String name) {
        logger.info("Creating lobby with name: {}", name);
        Lobby lobby = new Lobby(name);
        logger.info("Created lobby: id={}, name={}", lobby.getId(), lobby.getName());
        lobbies.put(lobby.getId(), lobby);
        return lobby;
    }

    /**
     * Creates a new lobby with a default name.
     * 
     * @return The created lobby
     */
    public Lobby createLobby() {
        return createLobby("Unnamed Lobby");
    }

    /**
     * Gets the current board state for a specific lobby.
     * 
     * @param lobbyId The ID of the lobby
     * @return A map of square positions to piece strings, or null if the lobby doesn't exist
     */
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

    /**
     * Makes a move in a specific lobby.
     * 
     * @param lobbyId The ID of the lobby
     * @param chessMove The move to make
     * @param playerId The ID of the player making the move
     * @return The updated board state after the move
     */
    public Map<String, String> makeMove(String lobbyId, ChessMove chessMove, String playerId) {
        try {
            Lobby lobby = lobbies.get(lobbyId);
            if (lobby == null) {
                throw new IllegalArgumentException("Lobby not found");
            }

            // Verify it's the player's turn
            boolean isWhiteTurn = lobby.isWhiteTurn();
            if ((isWhiteTurn && !playerId.equals(lobby.getWhitePlayerId())) ||
                (!isWhiteTurn && !playerId.equals(lobby.getBlackPlayerId()))) {
                logger.warn("Player {} attempted to move out of turn in lobby {}", playerId, lobbyId);
                return convertBoardStateToMap(lobby.getBoardState());
            }

            ArrayList<String> boardState = lobby.getBoardState();
            
            // Update the board state based on the move
            String piece = boardState.get(convertSquareToIndex(chessMove.getFrom()));
            if (piece.isEmpty()) {
                logger.warn("No piece at source square {} in lobby {}", chessMove.getFrom(), lobbyId);
                return convertBoardStateToMap(boardState);
            }
            
            // Verify piece color matches player's color
            boolean isWhitePiece = piece.startsWith("w");
            if ((isWhitePiece && !playerId.equals(lobby.getWhitePlayerId())) ||
                (!isWhitePiece && !playerId.equals(lobby.getBlackPlayerId()))) {
                logger.warn("Player {} attempted to move opponent's piece in lobby {}", playerId, lobbyId);
                return convertBoardStateToMap(boardState);
            }

            // Prevent capturing own pieces
            String targetPiece = boardState.get(convertSquareToIndex(chessMove.getTo()));
            if (!targetPiece.isEmpty() && targetPiece.charAt(0) == piece.charAt(0)) {
                logger.warn("Player {} attempted to capture their own piece in lobby {}", playerId, lobbyId);
                return convertBoardStateToMap(boardState);
            }

            // Validate the move
            if (!MoveValidator.isValidMove(piece, chessMove.getFrom(), chessMove.getTo(), boardState)) {
                logger.warn("Invalid move from {} to {} by player {} in lobby {}", chessMove.getFrom(), chessMove.getTo(), playerId, lobbyId);
                return convertBoardStateToMap(boardState);
            }

            // Check if the move gets the player out of check
            ArrayList<String> tempBoard = new ArrayList<>(boardState);
            tempBoard.set(convertSquareToIndex(chessMove.getFrom()), "");
            tempBoard.set(convertSquareToIndex(chessMove.getTo()), piece);
            
            if (lobby.isInCheck(isWhiteTurn) && lobby.isInCheck(isWhiteTurn, tempBoard)) {
                logger.warn("Move from {} to {} by player {} in lobby {} does not get out of check", 
                    chessMove.getFrom(), chessMove.getTo(), playerId, lobbyId);
                return convertBoardStateToMap(boardState);
            }

            // Handle promotion
            String newPiece = piece;
            if (chessMove.getPromotion() != null) {
                char color = isWhitePiece ? 'w' : 'b';
                if ((isWhitePiece && chessMove.getTo().charAt(1) == '8' && piece.endsWith("P")) ||
                    (!isWhitePiece && chessMove.getTo().charAt(1) == '1' && piece.endsWith("P"))) {
                    newPiece = color + chessMove.getPromotion();
                    logger.info("Promoting {} pawn to {}", color, chessMove.getPromotion());
                }
            }

            // Make the move
            boardState.set(convertSquareToIndex(chessMove.getFrom()), "");
            boardState.set(convertSquareToIndex(chessMove.getTo()), newPiece);

            // Handle en passant capture
            if (piece.endsWith("P")) {
                int fromIndex = convertSquareToIndex(chessMove.getFrom());
                int toIndex = convertSquareToIndex(chessMove.getTo());
                int direction = isWhitePiece ? -1 : 1;
                
                // Check if this was a double pawn move
                if (Math.abs(toIndex - fromIndex) == 16) {
                    lobby.setLastMovedPawnSquare(chessMove.getTo());
                }
                
                // Check if this was an en passant capture
                if (Math.abs(toIndex - fromIndex) == 7 || Math.abs(toIndex - fromIndex) == 9) {
                    int capturedPawnIndex = toIndex - 8 * direction; // The square the captured pawn is on
                    String capturedPiece = boardState.get(capturedPawnIndex);
                    if (capturedPiece.equals(isWhitePiece ? "bP" : "wP")) {
                        boardState.set(capturedPawnIndex, "");
                        logger.info("En passant capture in lobby {}: {} captures pawn at {}", 
                            lobbyId, piece, ChessUtils.indexToNotation(capturedPawnIndex));
                    }
                }
            }
            
            // Update board state and toggle turn
            lobby.setBoardState(boardState);
            logger.info("Move made in lobby {}: {} to {}, next turn: {}", 
                lobbyId, chessMove.getFrom(), chessMove.getTo(), 
                lobby.isWhiteTurn() ? "white" : "black");
            
            return convertBoardStateToMap(boardState);
        } catch (Exception e) {
            logger.error("Error making move in lobby {}: {}", lobbyId, e.getMessage());
            return null;
        }
    }

    public Collection<Lobby> getLobbies() {
        return lobbies.values();
    }

    public Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    public void joinLobby(String lobbyId, String playerId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            logger.info("Player {} joined lobby {}", playerId, lobbyId);
        }
    }

    public void disconnect(String lobbyId, String playerId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            playerColors.remove(playerId);
            lobbies.remove(lobbyId);
            logger.info("Player {} disconnected from lobby {}", playerId, lobbyId);
        } else {
            logger.warn("Lobby {} not found for disconnection", lobbyId);
        }
    }

    public String getWhitePlayerId(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        return lobby != null ? lobby.getWhitePlayerId() : null;
    }

    public String getBlackPlayerId(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        return lobby != null ? lobby.getBlackPlayerId() : null;
    }

    /**
     * Claims a color for a player in a specific lobby.
     * 
     * @param lobbyId The ID of the lobby
     * @param playerId The ID of the player
     * @param color The color to claim ("white" or "black")
     * @return true if the color was successfully claimed, false otherwise
     */
    public boolean claimColor(String lobbyId, String playerId, String color) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            logger.warn("Attempt to claim color in non-existent lobby: {}", lobbyId);
            return false;
        }

        // If player already has the other color, prevent claiming both colors
        if ("white".equalsIgnoreCase(color) && playerId.equals(lobby.getBlackPlayerId())) {
            logger.warn("Player {} attempted to claim white while already being black", playerId);
            return false;
        }
        if ("black".equalsIgnoreCase(color) && playerId.equals(lobby.getWhitePlayerId())) {
            logger.warn("Player {} attempted to claim black while already being white", playerId);
            return false;
        }

        if ("white".equalsIgnoreCase(color)) {
            if (lobby.getWhitePlayerId() == null || lobby.getWhitePlayerId().equals(playerId)) {
                lobby.setWhitePlayerId(playerId);
                logger.info("Player {} claimed white in lobby {}", playerId, lobbyId);
                return true;
            }
        } else if ("black".equalsIgnoreCase(color)) {
            if (lobby.getBlackPlayerId() == null || lobby.getBlackPlayerId().equals(playerId)) {
                lobby.setBlackPlayerId(playerId);
                logger.info("Player {} claimed black in lobby {}", playerId, lobbyId);
                return true;
            }
        }
        logger.warn("Player {} failed to claim {} in lobby {}", playerId, color, lobbyId);
        return false;
    }

    /**
     * Checks if it is currently the white player's turn in a specific lobby.
     * 
     * @param lobbyId The ID of the lobby
     * @return true if it is white's turn, false otherwise
     */
    public boolean isWhiteTurn(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        return lobby != null && lobby.isWhiteTurn();
    }

    /**
     * Converts a board state ArrayList to a map of square positions to piece strings.
     * 
     * @param boardState The board state as an ArrayList
     * @return A map of square positions to piece strings
     */
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

    /**
     * Converts a chess square notation to a board index.
     * 
     * @param square The square in chess notation (e.g., "e4")
     * @return The corresponding board index (0-63)
     */
    private int convertSquareToIndex(String square) {
        int file = square.charAt(0) - 'a';
        int rank = 8 - Character.getNumericValue(square.charAt(1));
        return rank * 8 + file;
    }
}
