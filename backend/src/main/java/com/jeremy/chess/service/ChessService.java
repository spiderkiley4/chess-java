package com.jeremy.chess.service;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.Lobby;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChessService {
    private final Map<String, Lobby> lobbies = new HashMap<>();

    public Lobby createLobby() {
        Lobby lobby = new Lobby();
        lobbies.put(lobby.getId(), lobby);
        return lobby;
    }

    public ArrayList<String> getBoardState(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        return lobby != null ? lobby.getBoardState() : null;
    }

    public ArrayList<String> makeMove(String lobbyId, ChessMove chessMove) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            return null;
        }

        try {
            
            ArrayList<String> boardState = lobby.getBoardState();
            
            lobby.setBoardState(boardState);
            return boardState;
        } catch (Exception e) {
            return null;
        }
    }

    public Collection<Lobby> getLobbies() {
        return lobbies.values();
    }
}
