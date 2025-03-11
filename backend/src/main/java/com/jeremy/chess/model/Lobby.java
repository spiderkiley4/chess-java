package com.jeremy.chess.model;

import java.util.UUID;

public class Lobby {
    private String id;
    private String boardState;

    public Lobby() {
        this.id = UUID.randomUUID().toString();
        this.boardState = "start";
    }

    public String getId() {
        return id;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }
}