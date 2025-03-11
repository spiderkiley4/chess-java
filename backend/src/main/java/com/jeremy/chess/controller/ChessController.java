package com.jeremy.chess.controller;

import com.jeremy.chess.model.Move;
import com.jeremy.chess.service.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class ChessController {

    @Autowired
    private ChessService chessService;

    @PostMapping("/move")
    public String makeMove(@RequestBody Move move) {
        return chessService.makeMove(move);
    }

    @MessageMapping("/move")
    @SendTo("/topic/game")
    public Move broadcastMove(Move move) {
        return move;
    }
}