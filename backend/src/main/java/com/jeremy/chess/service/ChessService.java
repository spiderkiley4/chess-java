package com.jeremy.chess.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Square;
import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.Lobby;
import org.springframework.stereotype.Service;

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

    public String getBoardState(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        return lobby != null ? lobby.getBoardState() : "Lobby not found!";
    }

    public String makeMove(String lobbyId, ChessMove chessMove) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            return "Lobby not found!";
        }

        try {
            Board board = new Board();
            board.loadFromFen(lobby.getBoardState());

            Move move = new Move(
                    Square.valueOf(chessMove.getFrom().toUpperCase()),
                    Square.valueOf(chessMove.getTo().toUpperCase())
            );

            board.doMove(move);
            String newBoardState = board.getFen();
            lobby.setBoardState(newBoardState);
            return newBoardState;
        } catch (Exception e) {
            return "Invalid move: " + e.getMessage();
        }
    }
}
