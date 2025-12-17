package asg.games.yokel.client.lwjgl3;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

import asg.games.yipee.common.dto.NetYipeePlayer;
import asg.games.yipee.net.packets.ClientHandshakeRequest;
import asg.games.yokel.client.managers.GameNetworkManager;

public class Lwjgl3WebSocketNetworkManager implements GameNetworkManager {
    private final String url; // full ws://host:port/path
    private WebSocket socket;
    private final Queue<String> messageQueue = new Queue<>();
    private final Json json = new Json();
    private volatile boolean connected;

    public Lwjgl3WebSocketNetworkManager(String url) {
        this.url = url;
    }

    @Override
    public boolean connect() {
        if (socket != null && socket.isOpen()) return true;

        socket = WebSockets.newSocket(url);
        socket.setSendGracefully(true);

        socket.addListener(new WebSocketListener() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                connected = true;
                return true;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                return false;
            }

            public boolean onClose(WebSocket webSocket, WebSocketCloseCode code, String reason) {
                connected = false;
                return true;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                connected = false;
                error.printStackTrace();
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String message) {
                messageQueue.addLast(message); // raw JSON
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                // ignore binary frames for now
                return false;
            }
        });

        socket.connect();     // async open
        return true;          // "started connecting"
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && socket.isOpen();
    }

    @Override
    public boolean disconnect() {
        if (socket != null) socket.close();
        socket = null;
        connected = false;
        return true;
    }

    @Override
    public void dispose() {
        disconnect();
        messageQueue.clear();
    }

    @Override
    public void send(Object packet) {
        if (!isConnected()) return;
        socket.send(json.toJson(packet)); // send envelope or packet JSON
    }

    @Override
    public boolean hasMessage() {
        return messageQueue.notEmpty();
    }

    @Override
    public Object pollMessage() {
        return messageQueue.notEmpty() ? messageQueue.removeFirst() : null;
    }

    @Override
    public void registerUser(String authToken, NetYipeePlayer player, String clientId, String sessionKey) {
        ClientHandshakeRequest req = new ClientHandshakeRequest();
        req.setAuthToken(authToken);
        req.setPlayerId(player.getId());
        req.setClientId(clientId);
        req.setTimestamp(System.currentTimeMillis());
        send(req);
    }

    @Override
    public void registerPackets() {
    }
}
