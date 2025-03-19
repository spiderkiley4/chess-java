package com.jeremy.chess.model;

public class GameMessage {
    private String lobbyId;
    private String type;
    private Object content;
    private boolean whiteTurn;
    private boolean gameOver;
    private String winningTeam;
    private String gameEndReason;
    private boolean inCheck;

    public GameMessage() {
    }

    public GameMessage(String lobbyId, String type, Object content, boolean whiteTurn) {
        this.lobbyId = lobbyId;
        this.type = type;
        this.content = content;
        this.whiteTurn = whiteTurn;
        this.gameOver = false;
        this.winningTeam = null;
        this.gameEndReason = null;
        this.inCheck = false;
    }

    public GameMessage(String lobbyId, String type, Object content, boolean whiteTurn, boolean gameOver, String winningTeam, String gameEndReason) {
        this.lobbyId = lobbyId;
        this.type = type;
        this.content = content;
        this.whiteTurn = whiteTurn;
        this.gameOver = gameOver;
        this.winningTeam = winningTeam;
        this.gameEndReason = gameEndReason;
        this.inCheck = false;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(String winningTeam) {
        this.winningTeam = winningTeam;
    }

    public String getGameEndReason() {
        return gameEndReason;
    }

    public void setGameEndReason(String gameEndReason) {
        this.gameEndReason = gameEndReason;
    }

    public boolean isInCheck() {
        return inCheck;
    }

    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }
} 