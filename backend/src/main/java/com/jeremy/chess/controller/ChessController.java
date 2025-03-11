package com.jeremy.chess.controller;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class ChessController {

    @Autowired
    private com.chess.service.ChessService chessService;

    @GetMapping("/state")
    public String getBoardState() {
        return chessService.getBoardState();
    }

    @PostMapping("/move")
    public String makeMove(@RequestBody ChessMove move) {
        return chessService.makeMove(move);
    }
}
