package com.jeremy.chess.controller;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.Lobby;
import com.jeremy.chess.service.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173") // Allow frontend requests
@RestController
@RequestMapping("/game")
public class ChessController {

    @Autowired
    private ChessService chessService;

    @PostMapping("/lobby")
    public Lobby createLobby() {
        return chessService.createLobby();
    }

    @GetMapping("/state/{lobbyId}")
    public String getBoardState(@PathVariable String lobbyId) {
        return chessService.getBoardState(lobbyId);
    }

    @PostMapping("/move/{lobbyId}")
    public String makeMove(@PathVariable String lobbyId, @RequestBody ChessMove move) {
        return chessService.makeMove(lobbyId, move);
    }
}
