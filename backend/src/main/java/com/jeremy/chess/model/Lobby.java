package com.jeremy.chess.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lobby {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    private String id;
    private String name;
    private String whitePlayerId;
    private String blackPlayerId;
    private boolean isWhiteTurn = true;
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
        this.name = "Unnamed Lobby";
        this.boardState = new ArrayList<>(initialBoard);
        logger.info("Lobby created with ID: {}", id);
        logger.info("Initial board state: {}", boardState);
    }

    public Lobby(String name) {
        this();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getBoardState() {
        return boardState;
    }

    public void setBoardState(ArrayList<String> boardState) {
        this.boardState = boardState;
        this.isWhiteTurn = !this.isWhiteTurn; // Toggle turn after move
        logger.info("Board state updated: {}", boardState);
    }

    public String getWhitePlayerId() {
        return whitePlayerId;
    }

    public String getBlackPlayerId() {
        return blackPlayerId;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public void addPlayer(String playerId) {
        if (whitePlayerId == null) {
            whitePlayerId = playerId;
            logger.info("White player joined: {}", playerId);
        } else if (blackPlayerId == null && !playerId.equals(whitePlayerId)) {
            blackPlayerId = playerId;
            logger.info("Black player joined: {}", playerId);
        }
    }

    public boolean canPlayerMove(String playerId) {
        if (playerId == null) return false;
        return (isWhiteTurn && playerId.equals(whitePlayerId)) ||
               (!isWhiteTurn && playerId.equals(blackPlayerId));
    }

    public boolean isFull() {
        return whitePlayerId != null && blackPlayerId != null;
    }

    public void setWhitePlayerId(String playerId) {
        this.whitePlayerId = playerId;
        logger.info("White player set to: {}", playerId);
    }

    public void setBlackPlayerId(String playerId) {
        this.blackPlayerId = playerId;
        logger.info("Black player set to: {}", playerId);
    }
}