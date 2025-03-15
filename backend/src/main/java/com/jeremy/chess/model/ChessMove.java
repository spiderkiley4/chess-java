package com.jeremy.chess.model;

import com.jeremy.chess.util.ChessUtils;

public class ChessMove {
    private String from;
    private String to;
    private String promotion; // Add this field

    // Getters and setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public int getSourceIndex() {
        return ChessUtils.notationToIndex(from);
    }

    public int getTargetIndex() {
        return ChessUtils.notationToIndex(to);
    }
}