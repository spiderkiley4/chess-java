package com.jeremy.chess.controller;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.Lobby;
import com.jeremy.chess.service.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.Collection;

@CrossOrigin(origins = "http://localhost:5173") // Allow frontend requests
@RestController
@RequestMapping("/game")
public class ChessController {

    @Autowired
    private ChessService chessService;

    @Autowired
    private ChessWebSocketController webSocketController;

    @PostMapping("/lobby")
    public Lobby createLobby(@RequestParam(required = false) String name) {
        Lobby lobby = chessService.createLobby(name != null ? name : "Unnamed Lobby");
        webSocketController.sendLobbyUpdate();
        return lobby;
    }

    @GetMapping("/state/{lobbyId}")
    public Map<String, String> getBoardState(@PathVariable String lobbyId) {
        return chessService.getBoardState(lobbyId);
    }

    @PostMapping("/move/{lobbyId}")
    public Map<String, String> makeMove(@PathVariable String lobbyId, @RequestBody ChessMove move, HttpSession session) {
        String playerId = session.getId();
        return chessService.makeMove(lobbyId, move, playerId);
    }

    @GetMapping("/lobbies")
    public Collection<Lobby> getLobbies() {
        return chessService.getLobbies();
    }

    @PostMapping("/disconnect/{lobbyId}")
    public void disconnect(@PathVariable String lobbyId, HttpSession session) {
        String playerId = session.getId();
        chessService.disconnect(lobbyId, playerId);
    }

    @PostMapping("/claim/{lobbyId}/{color}")
    public boolean claimColor(@PathVariable String lobbyId, @PathVariable String color, HttpSession session) {
        String playerId = session.getId();
        return chessService.claimColor(lobbyId, playerId, color);
    }

    @GetMapping("/players/{lobbyId}")
    public Map<String, String> getPlayers(@PathVariable String lobbyId) {
        return Map.of(
            "whitePlayerId", chessService.getWhitePlayerId(lobbyId) != null ? chessService.getWhitePlayerId(lobbyId) : "",
            "blackPlayerId", chessService.getBlackPlayerId(lobbyId) != null ? chessService.getBlackPlayerId(lobbyId) : ""
        );
    }
}
