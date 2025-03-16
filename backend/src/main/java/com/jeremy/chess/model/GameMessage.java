package com.jeremy.chess.model;

public class GameMessage {
    private String lobbyId;
    private String type;
    private Object content;
    private boolean whiteTurn;

    public GameMessage() {
    }

    public GameMessage(String lobbyId, String type, Object content, boolean whiteTurn) {
        this.lobbyId = lobbyId;
        this.type = type;
        this.content = content;
        this.whiteTurn = whiteTurn;
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
} 