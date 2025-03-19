package com.jeremy.chess.controller;

import com.jeremy.chess.model.ChessMove;
import com.jeremy.chess.model.GameMessage;
import com.jeremy.chess.model.ChatMessage;
import com.jeremy.chess.model.Lobby;
import com.jeremy.chess.service.ChessService;
import com.jeremy.chess.util.MoveParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

@Controller
public class ChessWebSocketController {

    @Autowired
    private ChessService chessService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/move")
    @SendTo("/topic/game")
    public GameMessage handleMove(GameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getSessionId();
        ChessMove move = convertToChessMove(message.getContent());
        Map<String, String> newState = chessService.makeMove(message.getLobbyId(), move, playerId);
        
        if (newState != null) {
            // Send lobby update after move
            sendLobbyUpdate();
            
            // Get the lobby to check game end state and check status
            Lobby lobby = chessService.getLobby(message.getLobbyId());
            
            // Return game state with end state information if available
            GameMessage response = new GameMessage(
                message.getLobbyId(),
                "MOVE",
                newState,
                lobby.isWhiteTurn(),
                lobby.isGameOver(),
                lobby.getWinningTeam(),
                lobby.getGameEndReason()
            );
            
            // Set check status for the current player
            response.setInCheck(lobby.isInCheck(lobby.isWhiteTurn()));
            
            return response;
        }
        return null;
    }

    @MessageMapping("/join")
    @SendTo("/topic/game")
    public GameMessage handleJoin(GameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getSessionId();
        chessService.joinLobby(message.getLobbyId(), playerId);
        Map<String, String> boardState = chessService.getBoardState(message.getLobbyId());
        
        // Send lobby update after join
        sendLobbyUpdate();
        
        // Send player information and turn state
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("boardState", boardState);
        gameState.put("players", Map.of(
            "whitePlayerId", chessService.getWhitePlayerId(message.getLobbyId()) != null ? 
                chessService.getWhitePlayerId(message.getLobbyId()) : "",
            "blackPlayerId", chessService.getBlackPlayerId(message.getLobbyId()) != null ? 
                chessService.getBlackPlayerId(message.getLobbyId()) : ""
        ));
        
        return new GameMessage(message.getLobbyId(), "STATE", gameState, chessService.isWhiteTurn(message.getLobbyId()));
    }

    @MessageMapping("/chat")
    @SendTo("/topic/game")
    public GameMessage handleChat(GameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String lobbyId = message.getLobbyId();
        String playerId = headerAccessor.getSessionId();
        Lobby lobby = chessService.getLobby(lobbyId);
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found: " + lobbyId);
        }

        // Check if the message is a natural language chess move
        String content = message.getContent().toString();
        if (MoveParser.isNaturalLanguageCommand(content)) {
            // Parse the move
            ChessMove move = MoveParser.parseNaturalLanguage(content, lobby.isWhiteTurn(), lobby);
            if (move != null) {
                // Make the move
                Map<String, String> newState = chessService.makeMove(lobbyId, move, playerId);
                if (newState != null) {
                    // Send lobby update after move
                    sendLobbyUpdate();
                    
                    // Return game state with end state information if available
                    GameMessage response = new GameMessage(
                        lobbyId,
                        "MOVE",
                        newState,
                        lobby.isWhiteTurn(),
                        lobby.isGameOver(),
                        lobby.getWinningTeam(),
                        lobby.getGameEndReason()
                    );
                    
                    // Set check status for the current player
                    response.setInCheck(lobby.isInCheck(lobby.isWhiteTurn()));
                    
                    // Also send a chat message about the move
                    GameMessage chatResponse = new GameMessage(
                        lobbyId,
                        "CHAT",
                        createChatMessage(lobbyId, playerId, "Moved " + content),
                        lobby.isWhiteTurn()
                    );
                    
                    return chatResponse;
                }
            }
        }

        // If not a valid move, just forward the chat message
        return new GameMessage(
            lobbyId,
            "CHAT",
            message.getContent(),
            lobby.isWhiteTurn()
        );
    }

    @MessageMapping("/chat/{lobbyId}")
    @SendTo("/topic/chat/{lobbyId}")
    public ChatMessage handleChatMessage(@DestinationVariable String lobbyId, ChatMessage message) {
        return message;
    }

    @MessageMapping("/claim")
    @SendTo("/topic/game")
    public GameMessage handleClaim(GameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getSessionId();
        String color = (String) message.getContent();
        boolean success = chessService.claimColor(message.getLobbyId(), playerId, color);
        
        if (success) {
            Map<String, String> players = Map.of(
                "whitePlayerId", chessService.getWhitePlayerId(message.getLobbyId()) != null ? 
                    chessService.getWhitePlayerId(message.getLobbyId()) : "",
                "blackPlayerId", chessService.getBlackPlayerId(message.getLobbyId()) != null ? 
                    chessService.getBlackPlayerId(message.getLobbyId()) : ""
            );
            return new GameMessage(message.getLobbyId(), "PLAYERS", players, chessService.isWhiteTurn(message.getLobbyId()));
        }
        return null;
    }

    @MessageMapping("/lobbies")
    @SendTo("/topic/lobbies")
    public Collection<Lobby> sendLobbyUpdate() {
        return chessService.getLobbies();
    }

    @MessageMapping("/disconnect")
    @SendTo("/topic/game")
    public GameMessage handleDisconnect(GameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getSessionId();
        String lobbyId = message.getLobbyId();
        Lobby lobby = chessService.getLobby(lobbyId);
        
        if (lobby != null) {
            String playerColor = null;
            if (playerId.equals(lobby.getWhitePlayerId())) {
                playerColor = "white";
                chessService.claimColor(lobbyId, null, "white"); // Release white color
            } else if (playerId.equals(lobby.getBlackPlayerId())) {
                playerColor = "black";
                chessService.claimColor(lobbyId, null, "black"); // Release black color
            }

            if (playerColor != null) {
                // Send a chat message about the disconnection
                GameMessage chatMessage = createChatMessage(
                    lobbyId,
                    "System",
                    playerColor + " player has disconnected"
                );
                
                // Send updated player information
                Map<String, String> players = Map.of(
                    "whitePlayerId", chessService.getWhitePlayerId(lobbyId) != null ? 
                        chessService.getWhitePlayerId(lobbyId) : "",
                    "blackPlayerId", chessService.getBlackPlayerId(lobbyId) != null ? 
                        chessService.getBlackPlayerId(lobbyId) : ""
                );
                
                // Send lobby update after disconnect
                sendLobbyUpdate();
                
                return new GameMessage(lobbyId, "PLAYERS", players, chessService.isWhiteTurn(lobbyId));
            }
        }
        return null;
    }

    private ChessMove convertToChessMove(Object content) {
        try {
            if (content instanceof Map) {
                return objectMapper.convertValue(content, ChessMove.class);
            } else if (content instanceof ChessMove) {
                return (ChessMove) content;
            }
            throw new IllegalArgumentException("Invalid move format");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert move: " + e.getMessage());
        }
    }

    private GameMessage createChatMessage(String lobbyId, String playerId, String content) {
        ChatMessage chatMessage = new ChatMessage("Player " + playerId, content);
        return new GameMessage(lobbyId, "CHAT", chatMessage, chessService.isWhiteTurn(lobbyId));
    }
} 