package com.jeremy.chess.util;

import com.jeremy.chess.model.ChessMove;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

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

    public static ChessMove parseNaturalLanguage(String command, boolean isWhite) {
        command = command.toUpperCase().trim();
        
        // Pattern for commands like "Knight to c3" or "Pawn to e4"
        Pattern pattern = Pattern.compile("(KNIGHT|BISHOP|ROOK|QUEEN|KING|PAWN)\\s+TO\\s+([A-H][1-8])");
        Matcher matcher = pattern.matcher(command);
        
        if (matcher.find()) {
            String piece = matcher.group(1);
            String targetSquare = matcher.group(2).toLowerCase();
            
            // Find the source square by looking for the specified piece
            String sourceSquare = findSourceSquare(piece, targetSquare, isWhite);
            if (sourceSquare != null) {
                return new ChessMove(sourceSquare, targetSquare, null);
            }
        }
        
        return null;
    }

    private static String findSourceSquare(String pieceName, String targetSquare, boolean isWhite) {
        // This is a placeholder. In a real implementation, you would:
        // 1. Get the current board state
        // 2. Find all pieces of the specified type and color
        // 3. Check which one can legally move to the target square
        // 4. Return the source square of that piece
        
        // For now, return null to indicate no valid move found
        return null;
    }

    public static boolean isNaturalLanguageCommand(String message) {
        String upperMessage = message.toUpperCase().trim();
        return upperMessage.contains(" TO ") && 
               PIECE_NAMES.keySet().stream().anyMatch(piece -> upperMessage.contains(piece));
    }
} 