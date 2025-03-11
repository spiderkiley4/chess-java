package com.jeremy.chess.service;

import com.jeremy.chess.model.Move;
import org.springframework.stereotype.Service;

@Service
public class ChessService {
    public String makeMove(Move move) {
        // TODO: Implement chess move validation logic
        return "Move accepted: " + move.getFrom() + " to " + move.getTo();
    }
}