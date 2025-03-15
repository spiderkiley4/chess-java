package com.jeremy.chess.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lobby {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    private String id;
    private ArrayList<String> initialBoard = new ArrayList<>(Arrays.asList(
        "bR", "bN", "bB", "bQ", "bK", "bB", "bN", "bR",
             "bP", "bP", "bP", "bP", "bP", "bP", "bP", "bP",
             "", "", "", "", "", "", "", "",
             "", "", "", "", "", "", "", "",
             "", "", "", "", "", "", "", "",
             "", "", "", "", "", "", "", "",
             "wP", "wP", "wP", "wP", "wP", "wP", "wP", "wP",
             "wR", "wN", "wB", "wQ", "wK", "wB", "wN", "wR"
    ));
    private ArrayList<String> boardState;

    public Lobby() {
        this.id = UUID.randomUUID().toString();
        this.boardState = new ArrayList<>(initialBoard);
        logger.info("Lobby created with ID: {}", id);
        logger.info("Initial board state: {}", boardState);
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getBoardState() {
        return boardState;
    }

    public void setBoardState(ArrayList<String> boardState) {
        this.boardState = boardState;
        logger.info("Board state updated: {}", boardState);
    }
}