package com.jeremy.chess.model;

public class ChatMessage {
    private String playerName;
    private String content;
    private String type;

    public ChatMessage() {
    }

    public ChatMessage(String playerName, String content) {
        this.playerName = playerName;
        this.content = content;
        this.type = "CHAT";
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
} 