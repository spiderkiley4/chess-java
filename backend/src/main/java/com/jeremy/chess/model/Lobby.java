package com.jeremy.chess.model;

import java.util.ArrayList;
import java.util.UUID;

public class Lobby {
    private String id;
    private ArrayList<String> boardState;

    public Lobby() {
        this.id = UUID.randomUUID().toString();
        this.boardState = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getBoardState() {
        return boardState;
    }

    public void setBoardState(ArrayList<String> boardState) {
        this.boardState = boardState;
    }
}