package com.jeremy.chess.util;

import java.util.ArrayList;

public class MoveValidator {

    public static boolean isValidMove(String piece, String from, String to, ArrayList<String> boardState) {
        switch (piece.charAt(1)) {
            case 'P': return isValidPawnMove(piece, from, to, boardState);
            case 'R': return isValidRookMove(piece, from, to, boardState);
            case 'N': return isValidKnightMove(piece, from, to, boardState);
            case 'B': return isValidBishopMove(piece, from, to, boardState);
            case 'Q': return isValidQueenMove(piece, from, to, boardState);
            case 'K': return isValidKingMove(piece, from, to, boardState);
            default: return false;
        }
    }

    private static boolean isValidPawnMove(String piece, String from, String to, ArrayList<String> boardState) {
        int fromIndex = ChessUtils.notationToIndex(from);
        int toIndex = ChessUtils.notationToIndex(to);
        int direction = piece.charAt(0) == 'w' ? -1 : 1;

        // Normal move
        if (toIndex == fromIndex + 8 * direction && boardState.get(toIndex).isEmpty()) {
            return true;
        }

        // Double move from starting position
        if ((fromIndex / 8 == 1 && direction == 1 || fromIndex / 8 == 6 && direction == -1) &&
            toIndex == fromIndex + 16 * direction && boardState.get(toIndex).isEmpty() && boardState.get(fromIndex + 8 * direction).isEmpty()) {
            return true;
        }

        // Capture move
        if (toIndex == fromIndex + 7 * direction || toIndex == fromIndex + 9 * direction) {
            // Regular capture
            if (!boardState.get(toIndex).isEmpty() && boardState.get(toIndex).charAt(0) != piece.charAt(0)) {
                return true;
            }
            
            // En passant capture
            if (boardState.get(toIndex).isEmpty()) {
                int capturedPawnIndex = toIndex - 8 * direction; // The square the captured pawn is on
                if (boardState.get(capturedPawnIndex).equals(piece.charAt(0) == 'w' ? "bP" : "wP")) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isValidRookMove(String piece, String from, String to, ArrayList<String> boardState) {
        int fromIndex = ChessUtils.notationToIndex(from);
        int toIndex = ChessUtils.notationToIndex(to);

        if (fromIndex / 8 == toIndex / 8) { // Same row
            int step = fromIndex < toIndex ? 1 : -1;
            for (int i = fromIndex + step; i != toIndex; i += step) {
                if (!boardState.get(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        if (fromIndex % 8 == toIndex % 8) { // Same column
            int step = fromIndex < toIndex ? 8 : -8;
            for (int i = fromIndex + step; i != toIndex; i += step) {
                if (!boardState.get(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private static boolean isValidKnightMove(String piece, String from, String to, ArrayList<String> boardState) {
        int fromIndex = ChessUtils.notationToIndex(from);
        int toIndex = ChessUtils.notationToIndex(to);
        int rowDiff = Math.abs(fromIndex / 8 - toIndex / 8);
        int colDiff = Math.abs(fromIndex % 8 - toIndex % 8);

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    private static boolean isValidBishopMove(String piece, String from, String to, ArrayList<String> boardState) {
        int fromIndex = ChessUtils.notationToIndex(from);
        int toIndex = ChessUtils.notationToIndex(to);
        int rowDiff = Math.abs(fromIndex / 8 - toIndex / 8);
        int colDiff = Math.abs(fromIndex % 8 - toIndex % 8);

        if (rowDiff == colDiff) {
            int step = (toIndex - fromIndex) / rowDiff;
            for (int i = fromIndex + step; i != toIndex; i += step) {
                if (!boardState.get(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private static boolean isValidQueenMove(String piece, String from, String to, ArrayList<String> boardState) {
        return isValidRookMove(piece, from, to, boardState) || isValidBishopMove(piece, from, to, boardState);
    }

    private static boolean isValidKingMove(String piece, String from, String to, ArrayList<String> boardState) {
        int fromIndex = ChessUtils.notationToIndex(from);
        int toIndex = ChessUtils.notationToIndex(to);
        int rowDiff = Math.abs(fromIndex / 8 - toIndex / 8);
        int colDiff = Math.abs(fromIndex % 8 - toIndex % 8);

        return rowDiff <= 1 && colDiff <= 1;
    }
}