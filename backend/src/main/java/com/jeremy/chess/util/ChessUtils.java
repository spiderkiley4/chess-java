package com.jeremy.chess.util;

public class ChessUtils {

    public static int notationToIndex(String notation) {
        char file = notation.charAt(0); // e.g., 'f'
        char rank = notation.charAt(1); // e.g., '3'

        int fileIndex = file - 'a'; // 'a' -> 0, 'b' -> 1, ..., 'h' -> 7
        int rankIndex = 8 - Character.getNumericValue(rank); // '8' -> 0, '7' -> 1, ..., '1' -> 7

        return rankIndex * 8 + fileIndex;
    }

    public static String indexToNotation(int index) {
        int file = index % 8;
        int rank = 8 - (index / 8);
        return String.valueOf((char)('a' + file)) + rank;
    }
}