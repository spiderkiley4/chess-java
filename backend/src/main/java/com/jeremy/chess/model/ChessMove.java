package com.jeremy.chess.model;

import com.jeremy.chess.util.ChessUtils;

/**
 * Represents a chess move with source square, target square, and optional promotion piece.
 * 
 * @author Jeremy Kiley
 * @author ChatGPT
 */
public class ChessMove {
    private String from;
    private String to;
    private String promotion;

    /**
     * Default constructor.
     */
    public ChessMove() {
    }

    /**
     * Constructor with source square, target square, and optional promotion piece.
     * 
     * @param from The source square in chess notation (e.g., "e2")
     * @param to The target square in chess notation (e.g., "e4")
     * @param promotion The piece to promote to (e.g., "Q" for queen)
     */
    public ChessMove(String from, String to, String promotion) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
    }

    /**
     * Gets the source square of the move.
     * 
     * @return The source square in chess notation
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the source square of the move.
     * 
     * @param from The source square in chess notation
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets the target square of the move.
     * 
     * @return The target square in chess notation
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the target square of the move.
     * 
     * @param to The target square in chess notation
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets the promotion piece.
     * 
     * @return The piece to promote to (e.g., "Q" for queen)
     */
    public String getPromotion() {
        return promotion;
    }

    /**
     * Sets the promotion piece.
     * 
     * @param promotion The piece to promote to (e.g., "Q" for queen)
     */
    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    /**
     * Gets the source square index (0-63).
     * 
     * @return The source square index
     */
    public int getSourceIndex() {
        return ChessUtils.notationToIndex(from);
    }

    /**
     * Gets the target square index (0-63).
     * 
     * @return The target square index
     */
    public int getTargetIndex() {
        return ChessUtils.notationToIndex(to);
    }
}