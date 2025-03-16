import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.subscriptions = new Map();
        this.connected = false;
        this.sessionId = null;
    }

    connect(onConnect = () => {}) {
        if (this.stompClient) {
            console.log('WebSocket already connected');
            onConnect();
            return;
        }

        const socket = new SockJS('http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);
        
        // Debug
        this.stompClient.debug = function(str) {
            console.log('STOMP: ' + str);
        };

        this.stompClient.connect({}, (frame) => {
            console.log('WebSocket Connected Successfully');
            this.connected = true;
            // Extract session ID from the socket URL
            const urlParts = socket._transport.url.split('/');
            this.sessionId = urlParts[urlParts.length - 2]; // Get the second to last part which is the actual session ID
            console.log('Connected with session ID:', this.sessionId);
            onConnect();
        }, error => {
            console.error('WebSocket Connection Error:', error);
            this.connected = false;
            setTimeout(() => this.connect(onConnect), 5000); // Retry connection after 5 seconds
        });
    }

    getSessionId() {
        return this.sessionId;
    }

    subscribe(lobbyId, onMessage) {
        if (!this.stompClient || !this.connected) {
            console.error('WebSocket not connected, attempting to reconnect...');
            this.connect(() => this.subscribe(lobbyId, onMessage));
            return;
        }

        console.log(`Subscribing to lobby: ${lobbyId}`);
        const subscription = this.stompClient.subscribe('/topic/game', message => {
            console.log('Received message:', message.body);
            try {
                const gameMessage = JSON.parse(message.body);
                if (gameMessage.lobbyId === lobbyId) {
                    onMessage(gameMessage);
                }
            } catch (error) {
                console.error('Error processing message:', error);
            }
        });

        this.subscriptions.set(lobbyId, subscription);
    }

    unsubscribe(lobbyId) {
        const subscription = this.subscriptions.get(lobbyId);
        if (subscription) {
            subscription.unsubscribe();
            this.subscriptions.delete(lobbyId);
            console.log(`Unsubscribed from lobby: ${lobbyId}`);
        }
    }

    sendMove(lobbyId, move) {
        if (!this.stompClient || !this.connected) {
            console.error('WebSocket not connected, cannot send move');
            return false;
        }

        try {
            console.log('Sending move:', { lobbyId, move });
            this.stompClient.send("/app/move", {}, JSON.stringify({
                lobbyId: lobbyId,
                type: "MOVE",
                content: move
            }));
            return true;
        } catch (error) {
            console.error('Error sending move:', error);
            return false;
        }
    }

    joinGame(lobbyId) {
        if (!this.stompClient || !this.connected) {
            console.error('WebSocket not connected, cannot join game');
            return false;
        }

        try {
            console.log('Joining game:', lobbyId);
            this.stompClient.send("/app/join", {}, JSON.stringify({
                lobbyId: lobbyId,
                type: "JOIN",
                content: null
            }));
            return true;
        } catch (error) {
            console.error('Error joining game:', error);
            return false;
        }
    }

    disconnect() {
        if (this.stompClient) {
            console.log('Disconnecting WebSocket');
            this.subscriptions.forEach(subscription => subscription.unsubscribe());
            this.subscriptions.clear();
            this.stompClient.disconnect();
            this.stompClient = null;
            this.connected = false;
        }
    }

    // Chat message subscriptions
    subscribeToChatMessages(lobbyId, callback) {
        const subscription = this.stompClient.subscribe(
            `/topic/chat/${lobbyId}`,
            (message) => {
                const chatMessage = JSON.parse(message.body);
                callback(chatMessage);
            }
        );
        this.subscriptions.set(`chat_${lobbyId}`, subscription);
    }

    unsubscribeFromChat(lobbyId) {
        const subscription = this.subscriptions.get(`chat_${lobbyId}`);
        if (subscription) {
            subscription.unsubscribe();
            this.subscriptions.delete(`chat_${lobbyId}`);
        }
    }

    sendChatMessage(lobbyId, message) {
        if (!this.stompClient || !this.connected) {
            console.error("WebSocket is not connected");
            return false;
        }

        this.stompClient.send("/app/chat", {}, JSON.stringify({
            lobbyId: lobbyId,
            type: "CHAT",
            content: message
        }));
        return true;
    }

    claimColor(lobbyId, color) {
        if (!this.stompClient || !this.connected) {
            console.error('WebSocket not connected, cannot claim color');
            return false;
        }

        try {
            console.log('Claiming color:', { lobbyId, color });
            this.stompClient.send("/app/claim", {}, JSON.stringify({
                lobbyId: lobbyId,
                type: "CLAIM",
                content: color
            }));
            return true;
        } catch (error) {
            console.error('Error claiming color:', error);
            return false;
        }
    }

    subscribeToLobbies(callback) {
        if (!this.stompClient || !this.connected) {
            console.error('WebSocket not connected, attempting to reconnect...');
            this.connect(() => this.subscribeToLobbies(callback));
            return;
        }

        console.log('Subscribing to lobbies');
        const subscription = this.stompClient.subscribe('/topic/lobbies', message => {
            console.log('Received lobbies update:', message.body);
            try {
                const lobbies = JSON.parse(message.body);
                console.log('Parsed lobbies:', lobbies);
                if (!Array.isArray(lobbies)) {
                    console.error('Received lobbies is not an array:', lobbies);
                    return;
                }
                callback(lobbies);
            } catch (error) {
                console.error('Error processing lobbies update:', error);
            }
        });

        this.subscriptions.set('lobbies', subscription);
    }

    unsubscribeFromLobbies() {
        const subscription = this.subscriptions.get('lobbies');
        if (subscription) {
            subscription.unsubscribe();
            this.subscriptions.delete('lobbies');
        }
    }
}

export default new WebSocketService(); 