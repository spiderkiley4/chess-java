package com.chess.service;

import com.jeremy.chess.model.ChessMove;
import org.springframework.stereotype.Service;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveException;

@Service
public class ChessService {
    private Board board = new Board(); // Chess game state

    public String getBoardState() {
        return board.getFen();
    }

    public String makeMove(ChessMove move) {
        try {
            board.doMove(new ChessMove(move.getFrom(), move.getTo()));
            return board.getFen();
        } catch (MoveException e) {
            return "Invalid move!";
        }
    }
}
