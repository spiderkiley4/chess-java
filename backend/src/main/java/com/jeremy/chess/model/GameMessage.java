package com.jeremy.chess.model;

/**
 * Represents a message containing game state information to be sent to clients.
 * 
 * @author Jeremy Kiley
 * @author ChatGPT
 */
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

    /**
     * Checks if it is currently the white player's turn.
     * 
     * @return true if it is white's turn, false otherwise
     */
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    /**
     * Sets whether it is the white player's turn.
     * 
     * @param whiteTurn true if it is white's turn, false otherwise
     */
    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    /**
     * Checks if the game is over.
     * 
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Sets whether the game is over.
     * 
     * @param gameOver true if the game is over, false otherwise
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
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
     * Sets the winning team.
     * 
     * @param winningTeam "White", "Black", "Draw", or null if the game is still in progress
     */
    public void setWinningTeam(String winningTeam) {
        this.winningTeam = winningTeam;
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
     * Sets the reason why the game ended.
     * 
     * @param gameEndReason The game end reason
     */
    public void setGameEndReason(String gameEndReason) {
        this.gameEndReason = gameEndReason;
    }

    /**
     * Checks if the current player is in check.
     * 
     * @return true if the current player is in check, false otherwise
     */
    public boolean isInCheck() {
        return inCheck;
    }

    /**
     * Sets whether the current player is in check.
     * 
     * @param inCheck true if the current player is in check, false otherwise
     */
    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }
} 