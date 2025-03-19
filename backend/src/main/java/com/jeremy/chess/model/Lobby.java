package com.jeremy.chess.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jeremy.chess.util.ChessUtils;
import com.jeremy.chess.util.MoveValidator;

/**
 * Represents a chess game lobby that manages the game state, players, and move validation.
 * 
 * @author Jeremy Kiley
 * @author ChatGPT
 */
public class Lobby {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    private String id;
    private String name;
    private String whitePlayerId;
    private String blackPlayerId;
    private boolean isWhiteTurn = true;
    private ArrayList<String> initialBoard = new ArrayList<>(Arrays.asList(
            "bR", "bN", "bB", "bQ", "bK", "bB", "bN", "bR",
             "bP", "bP", "bP", "bP", "bP", "bP", "bP", "bP",
             "", "", "", "", "", "", "", "",
             "", "", "", "", "", "", "", "",
             "", "", "", "", "", "", "", "",
             "", "", "", "", "", "", "", "",
             "wP", "wP", "wP", "wP", "wP", "wP", "wP", "wP",
             "wR", "wN", "wB", "wQ", "wK", "wB", "wN", "wR"
    ));
    private ArrayList<String> boardState;
    private String winningTeam = null;
    private String gameEndReason = null;
    private int moveCount = 0;
    private ArrayList<String> previousPositions = new ArrayList<>();
    private String lastMovedPawnSquare = null;  // Track square of last moved pawn for en passant

    /**
     * Default constructor that creates a new lobby with a random UUID.
     */
    public Lobby() {
        this.id = UUID.randomUUID().toString();
        this.name = "Unnamed Lobby";
        this.boardState = new ArrayList<>(initialBoard);
        logger.info("Lobby created with ID: {}", id);
        logger.info("Initial board state: {}", boardState);
    }

    /**
     * Constructor that creates a new lobby with a specified name.
     * 
     * @param name The name of the lobby
     */
    public Lobby(String name) {
        this();
        this.name = name;
    }

    /**
     * Gets the lobby's unique identifier.
     * 
     * @return The lobby ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the lobby's name.
     * 
     * @return The lobby name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the lobby's name.
     * 
     * @param name The new name for the lobby
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the current state of the chess board.
     * 
     * @return The board state as an ArrayList of piece strings
     */
    public ArrayList<String> getBoardState() {
        return boardState;
    }

    /**
     * Sets the state of the chess board and checks for game over conditions.
     * 
     * @param boardState The new board state
     */
    public void setBoardState(ArrayList<String> boardState) {
        this.boardState = boardState;
        logger.info("Board state updated: {}", boardState);
        checkGameOver();
        this.isWhiteTurn = !this.isWhiteTurn; // Toggle turn after checking for game over
        this.lastMovedPawnSquare = null; // Reset en passant square after each move
    }

    /**
     * Gets the square of the last moved pawn (for en passant validation).
     * 
     * @return The square in chess notation, or null if no pawn was moved
     */
    public String getLastMovedPawnSquare() {
        return lastMovedPawnSquare;
    }

    /**
     * Sets the square of the last moved pawn (for en passant validation).
     * 
     * @param square The square in chess notation
     */
    public void setLastMovedPawnSquare(String square) {
        this.lastMovedPawnSquare = square;
    }

    /**
     * Checks for various game over conditions and updates the game state accordingly.
     */
    private void checkGameOver() {
        // Check for king capture
        boolean whiteKingPresent = boardState.contains("wK");
        boolean blackKingPresent = boardState.contains("bK");

        if (!whiteKingPresent || !blackKingPresent) {
            winningTeam = whiteKingPresent ? "White" : "Black";
            gameEndReason = "King captured";
            logger.info("Game over! {} king captured. {} wins!", whiteKingPresent ? "Black" : "White", winningTeam);
            return;
        }

        // Check for checkmate
        if (isInCheck(isWhiteTurn) && !hasLegalMoves(isWhiteTurn)) {
            winningTeam = isWhiteTurn ? "Black" : "White";
            gameEndReason = "Checkmate";
            logger.info("Game over! {} wins by checkmate!", winningTeam);
            return;
        }

        // Check for stalemate
        if (!isInCheck(isWhiteTurn) && !hasLegalMoves(isWhiteTurn)) {
            winningTeam = "Draw";
            gameEndReason = "Stalemate";
            logger.info("Game over! Stalemate!");
            return;
        }

        // Check for insufficient material
        if (isInsufficientMaterial()) {
            winningTeam = "Draw";
            gameEndReason = "Insufficient material";
            logger.info("Game over! Draw due to insufficient material!");
            return;
        }

        // Check for threefold repetition
        String currentPosition = String.join(",", boardState);
        previousPositions.add(currentPosition);
        if (previousPositions.size() > 10) {
            previousPositions.remove(0);
        }
        if (isThreefoldRepetition()) {
            winningTeam = "Draw";
            gameEndReason = "Threefold repetition";
            logger.info("Game over! Draw due to threefold repetition!");
            return;
        }

        // Check for fifty-move rule
        moveCount++;
        if (moveCount >= 100) { // 50 moves = 100 half-moves
            winningTeam = "Draw";
            gameEndReason = "Fifty-move rule";
            logger.info("Game over! Draw due to fifty-move rule!");
            return;
        }
    }

    /**
     * Checks if the specified player is in check.
     * 
     * @param isWhite Whether to check the white player
     * @return true if the player is in check, false otherwise
     */
    public boolean isInCheck(boolean isWhite) {
        return isInCheck(isWhite, boardState);
    }

    /**
     * Checks if the specified player is in check on a given board state.
     * 
     * @param isWhite Whether to check the white player
     * @param board The board state to check
     * @return true if the player is in check, false otherwise
     */
    public boolean isInCheck(boolean isWhite, ArrayList<String> board) {
        // Find king position
        int kingIndex = -1;
        for (int i = 0; i < board.size(); i++) {
            String piece = board.get(i);
            if (piece.equals(isWhite ? "wK" : "bK")) {
                kingIndex = i;
                break;
            }
        }
        if (kingIndex == -1) return false;

        // Convert index to chess notation
        String kingSquare = ChessUtils.indexToNotation(kingIndex);

        // Check if any opponent piece can capture the king
        for (int i = 0; i < board.size(); i++) {
            String piece = board.get(i);
            if (!piece.isEmpty() && piece.charAt(0) == (isWhite ? 'b' : 'w')) {
                String fromSquare = ChessUtils.indexToNotation(i);
                if (MoveValidator.isValidMove(piece, fromSquare, kingSquare, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the specified player has any legal moves available.
     * 
     * @param isWhite Whether to check the white player
     * @return true if the player has legal moves, false otherwise
     */
    private boolean hasLegalMoves(boolean isWhite) {
        // Try all possible moves for all pieces
        for (int i = 0; i < boardState.size(); i++) {
            String piece = boardState.get(i);
            if (!piece.isEmpty() && piece.charAt(0) == (isWhite ? 'w' : 'b')) {
                String fromSquare = ChessUtils.indexToNotation(i);
                for (int j = 0; j < boardState.size(); j++) {
                    String toSquare = ChessUtils.indexToNotation(j);
                    // Skip if trying to move to the same square
                    if (i == j) continue;
                    
                    // Skip if trying to capture own piece
                    String targetPiece = boardState.get(j);
                    if (!targetPiece.isEmpty() && targetPiece.charAt(0) == piece.charAt(0)) continue;
                    
                    if (MoveValidator.isValidMove(piece, fromSquare, toSquare, boardState)) {
                        // Try the move
                        ArrayList<String> tempBoard = new ArrayList<>(boardState);
                        tempBoard.set(j, piece);
                        tempBoard.set(i, "");
                        
                        // Check if the move gets us out of check
                        if (!isInCheck(isWhite, tempBoard)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the current position has insufficient material for checkmate.
     * 
     * @return true if there is insufficient material, false otherwise
     */
    private boolean isInsufficientMaterial() {
        int whitePieces = 0;
        int blackPieces = 0;
        boolean hasWhiteBishop = false;
        boolean hasBlackBishop = false;
        boolean hasWhiteKnight = false;
        boolean hasBlackKnight = false;

        for (String piece : boardState) {
            if (!piece.isEmpty()) {
                if (piece.charAt(0) == 'w') {
                    whitePieces++;
                    if (piece.charAt(1) == 'B') hasWhiteBishop = true;
                    if (piece.charAt(1) == 'N') hasWhiteKnight = true;
                } else {
                    blackPieces++;
                    if (piece.charAt(1) == 'B') hasBlackBishop = true;
                    if (piece.charAt(1) == 'N') hasBlackKnight = true;
                }
            }
        }

        // King vs King
        if (whitePieces == 1 && blackPieces == 1) return true;

        // King and Bishop vs King and Bishop (same colored squares)
        if (whitePieces == 2 && blackPieces == 2 && hasWhiteBishop && hasBlackBishop) {
            // Check if bishops are on same colored squares
            int whiteBishopIndex = -1;
            int blackBishopIndex = -1;
            for (int i = 0; i < boardState.size(); i++) {
                String piece = boardState.get(i);
                if (piece.equals("wB")) whiteBishopIndex = i;
                if (piece.equals("bB")) blackBishopIndex = i;
            }
            // Only check bishop positions if both were found
            if (whiteBishopIndex != -1 && blackBishopIndex != -1) {
                boolean whiteBishopOnWhite = (whiteBishopIndex / 8 + whiteBishopIndex % 8) % 2 == 0;
                boolean blackBishopOnWhite = (blackBishopIndex / 8 + blackBishopIndex % 8) % 2 == 0;
                if (whiteBishopOnWhite == blackBishopOnWhite) return true;
            }
        }

        // King and Knight vs King and Knight
        if (whitePieces == 2 && blackPieces == 2 && hasWhiteKnight && hasBlackKnight) return true;

        // King and Bishop vs King
        if ((whitePieces == 2 && blackPieces == 1 && hasWhiteBishop) ||
            (whitePieces == 1 && blackPieces == 2 && hasBlackBishop)) return true;

        // King and Knight vs King
        if ((whitePieces == 2 && blackPieces == 1 && hasWhiteKnight) ||
            (whitePieces == 1 && blackPieces == 2 && hasBlackKnight)) return true;

        return false;
    }

    /**
     * Checks if the current position has occurred three times (threefold repetition).
     * 
     * @return true if there is threefold repetition, false otherwise
     */
    private boolean isThreefoldRepetition() {
        String currentPosition = String.join(",", boardState);
        int count = 0;
        for (String position : previousPositions) {
            if (position.equals(currentPosition)) {
                count++;
                if (count >= 3) return true;
            }
        }
        return false;
    }

    /**
     * Gets the reason why the game ended.
     * 
     * @return The game end reason, or null if the game is still in progress
     */
    public String getGameEndReason() {
        return gameEndReason;
    }

    /**
     * Checks if the game is over.
     * 
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return winningTeam != null;
    }

    /**
     * Gets the winning team.
     * 
     * @return "White", "Black", "Draw", or null if the game is still in progress
     */
    public String getWinningTeam() {
        return winningTeam;
    }

    /**
     * Gets the ID of the white player.
     * 
     * @return The white player's ID, or null if no white player has joined
     */
    public String getWhitePlayerId() {
        return whitePlayerId;
    }

    /**
     * Gets the ID of the black player.
     * 
     * @return The black player's ID, or null if no black player has joined
     */
    public String getBlackPlayerId() {
        return blackPlayerId;
    }

    /**
     * Checks if it is currently the white player's turn.
     * 
     * @return true if it is white's turn, false otherwise
     */
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    /**
     * Adds a player to the lobby.
     * 
     * @param playerId The ID of the player to add
     */
    public void addPlayer(String playerId) {
        if (whitePlayerId == null) {
            whitePlayerId = playerId;
            logger.info("White player joined: {}", playerId);
        } else if (blackPlayerId == null && !playerId.equals(whitePlayerId)) {
            blackPlayerId = playerId;
            logger.info("Black player joined: {}", playerId);
        }
    }

    /**
     * Checks if a player can make a move in the current turn.
     * 
     * @param playerId The ID of the player to check
     * @return true if the player can move, false otherwise
     */
    public boolean canPlayerMove(String playerId) {
        if (playerId == null) return false;
        return (isWhiteTurn && playerId.equals(whitePlayerId)) ||
               (!isWhiteTurn && playerId.equals(blackPlayerId));
    }

    /**
     * Checks if the lobby is full (has both players).
     * 
     * @return true if the lobby is full, false otherwise
     */
    public boolean isFull() {
        return whitePlayerId != null && blackPlayerId != null;
    }

    /**
     * Sets the white player's ID.
     * 
     * @param playerId The ID of the white player
     */
    public void setWhitePlayerId(String playerId) {
        this.whitePlayerId = playerId;
        logger.info("White player set to: {}", playerId);
    }

    /**
     * Sets the black player's ID.
     * 
     * @param playerId The ID of the black player
     */
    public void setBlackPlayerId(String playerId) {
        this.blackPlayerId = playerId;
        logger.info("Black player set to: {}", playerId);
    }
}