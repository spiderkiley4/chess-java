package com.jeremy.chess.util;

/**
 * Utility class for chess-related operations.
 * Provides methods for converting between chess notation and board indices.
 * 
 * @author Jeremy Kiley
 * @author ChatGPT
 */
public class ChessUtils {

    /**
     * Converts chess notation (e.g., "e4") to board index (0-63).
     * 
     * @param notation The chess notation (e.g., "e4")
     * @return The corresponding board index (0-63)
     */
    public static int notationToIndex(String notation) {
        char file = notation.charAt(0); // e.g., 'f'
        char rank = notation.charAt(1); // e.g., '3'

        int fileIndex = file - 'a'; // 'a' -> 0, 'b' -> 1, ..., 'h' -> 7
        int rankIndex = 8 - Character.getNumericValue(rank); // '8' -> 0, '7' -> 1, ..., '1' -> 7

        return rankIndex * 8 + fileIndex;
    }

    /**
     * Converts board index (0-63) to chess notation (e.g., "e4").
     * 
     * @param index The board index (0-63)
     * @return The corresponding chess notation (e.g., "e4")
     */
    public static String indexToNotation(int index) {
        int file = index % 8;
        int rank = 8 - (index / 8);
        return String.valueOf((char)('a' + file)) + rank;
    }
}