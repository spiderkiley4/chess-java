package com.jeremy.chess.util;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.Lobby;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Utility class for parsing natural language chess moves into ChessMove objects.
 * 
 * @author Jeremy Kiley
 * @author ChatGPT
 */
public class MoveParser {
    private static final Map<String, String> PIECE_NAMES = new HashMap<>();
    static {
        PIECE_NAMES.put("KNIGHT", "N");
        PIECE_NAMES.put("BISHOP", "B");
        PIECE_NAMES.put("ROOK", "R");
        PIECE_NAMES.put("QUEEN", "Q");
        PIECE_NAMES.put("KING", "K");
        PIECE_NAMES.put("PAWN", "P");
    }

    /**
     * Parses a natural language command into a ChessMove object.
     * 
     * @param command The natural language command (e.g., "Knight to c3")
     * @param isWhite Whether the move is for the white player
     * @param lobby The current game lobby containing the board state
     * @return A ChessMove object if the command is valid, null otherwise
     */
    public static ChessMove parseNaturalLanguage(String command, boolean isWhite, Lobby lobby) {
        command = command.toUpperCase().trim();
        
        // Pattern for commands like "Knight to c3" or "Pawn to e4"
        Pattern pattern = Pattern.compile("(KNIGHT|BISHOP|ROOK|QUEEN|KING|PAWN)\\s+TO\\s+([A-H][1-8])");
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            String piece = matcher.group(1);
            String targetSquare = matcher.group(2).toLowerCase();
            
            // Find the source square by looking for the specified piece
            String sourceSquare = findSourceSquare(piece, targetSquare, isWhite, lobby);
            if (sourceSquare != null) {
                return new ChessMove(sourceSquare, targetSquare, null);
            }
        }
        
        return null;
    }

    /**
     * Finds the source square for a piece given its target square.
     * 
     * @param pieceName The name of the piece (e.g., "KNIGHT")
     * @param targetSquare The target square in chess notation
     * @param isWhite Whether the move is for the white player
     * @param lobby The current game lobby containing the board state
     * @return The source square in chess notation, or null if not found
     */
    private static String findSourceSquare(String pieceName, String targetSquare, boolean isWhite, Lobby lobby) {
        ArrayList<String> boardState = lobby.getBoardState();
        String pieceType = PIECE_NAMES.get(pieceName);
        char pieceColor = isWhite ? 'w' : 'b';
        String pieceString = pieceColor + pieceType;

        // Find all pieces of the specified type and color
        for (int i = 0; i < boardState.size(); i++) {
            String piece = boardState.get(i);
            if (piece.equals(pieceString)) {
                String sourceSquare = ChessUtils.indexToNotation(i);
                // Check if this piece can legally move to the target square
                if (MoveValidator.isValidMove(pieceString, sourceSquare, targetSquare, boardState)) {
                    return sourceSquare;
                }
            }
        }
        
        return null;
    }

    /**
     * Checks if a message is a natural language chess command.
     * 
     * @param message The message to check
     * @return true if the message is a natural language command, false otherwise
     */
    public static boolean isNaturalLanguageCommand(String message) {
        String upperMessage = message.toUpperCase().trim();
        return upperMessage.contains(" TO ") && 
               PIECE_NAMES.keySet().stream().anyMatch(piece -> upperMessage.contains(piece));
    }
} 