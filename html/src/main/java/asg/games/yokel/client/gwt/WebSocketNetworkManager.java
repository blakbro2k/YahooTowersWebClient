package asg.games.yokel.client.gwt;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import asg.games.yipee.common.net.wire.ClientHandshakeRequest;
import asg.games.yipee.common.net.wire.GameAuthTokenResponse;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yokel.client.managers.GameNetworkManager;

public class WebSocketNetworkManager implements GameNetworkManager {

    private final String host;
    private final int port;

    private WebSocket socket;
    private boolean connected = false;
    private String wsPath = "/ws/game";
    private final Queue<Object> messageQueue = new Queue<>();
    private final Json json = new Json();

    // NEW: listeners
    private Consumer<GameAuthTokenResponse> onAuthenticated;
    private Consumer<Throwable> onError;

    // NEW: optional stored token (not used by handshake connect; used by request packets if you want)
    private String requestToken;

    public WebSocketNetworkManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean connect() {
        return connectInternal(null);
    }

    @Override
    public boolean connectWithLaunchToken(String launchToken) {
        return connectInternal(launchToken);
    }

    private boolean connectInternal(String launchToken) {
        if (connected) return true;

        try {
            String baseUrl = WebSockets.toWebSocketUrl(host, port);

            String normalizedPath = wsPath;

            if (!normalizedPath.startsWith("/")) {
                normalizedPath = "/" + normalizedPath;
            }

            //String url = baseUrl + normalizedPath;
            String url = baseUrl + "ws/game";

            if (launchToken != null && !launchToken.trim().isEmpty()) {
                String enc = URLEncoder.encode(launchToken, StandardCharsets.UTF_8.name());
                // IMPORTANT: match your interceptor's query param name
                url = url + "?launchToken=" + enc;
            }

            socket = WebSockets.newSocket(url);
            socket.setSendGracefully(true);

            socket.addListener(new WebSocketListener() {
                @Override
                public boolean onOpen(WebSocket webSocket) {
                    connected = true;
                    return true;
                }

                @Override
                public boolean onClose(WebSocket webSocket, WebSocketCloseCode code, String reason) {
                    connected = false;
                    return true;
                }

                @Override
                public boolean onError(WebSocket webSocket, Throwable error) {
                    connected = false;
                    if (onError != null) onError.accept(error);
                    return true;
                }

                @Override
                public boolean onMessage(WebSocket webSocket, String message) {
                    // NEW: intercept GameAuthTokenResponse (server sends direct response object)
                    try {
                        // Cheap check without fully parsing into a class first:
                        // If you prefer, you can parse into a JsonValue tree; Json can do readValue(Object.class, ...)
                        if (message != null && message.contains("\"packetType\"")
                                && message.contains("GameAuthTokenResponse")) {

                            GameAuthTokenResponse auth = json.fromJson(GameAuthTokenResponse.class, message);
                            if (auth != null) {
                                if (onAuthenticated != null) onAuthenticated.accept(auth);
                                return true; // swallow from queue (recommended)
                            }
                        }
                    } catch (Exception e) {
                        // fall through: treat as normal message
                    }

                    messageQueue.addLast(message);
                    return true;
                }

                @Override
                public boolean onMessage(WebSocket webSocket, byte[] packet) {
                    return false; // text-only
                }
            });

            socket.connect();
            return true; // initiated
        } catch (Exception e) {
            connected = false;
            if (onError != null) onError.accept(e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && socket.isOpen();
    }

    @Override
    public boolean disconnect() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
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
        if (socket != null && isConnected()) {
            try {
                String payload = json.toJson(packet);
                socket.send(payload);
            } catch (Exception e) {
                if (onError != null) onError.accept(e);
                e.printStackTrace();
            }
        } else {
            System.err.println("WebSocket is not connected. Cannot send packet.");
        }
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
    public void registerUser(String authToken, YipeePlayerGDX player, String clientId, String sessionKey) {
        // You may not need this anymore if LaunchToken+WS gives you auth.
        ClientHandshakeRequest requestPacket = new ClientHandshakeRequest();
        requestPacket.setAuthToken(authToken);
        requestPacket.setPlayerId(player.getId());
        requestPacket.setClientId(clientId);
        send(requestPacket);
    }

    @Override
    public void registerPackets() {
        // no-op for JSON WS
    }

    @Override
    public void setRequestToken(String tokenString) {
        this.requestToken = tokenString;
    }

    @Override
    public void setOnAuthenticated(Consumer<GameAuthTokenResponse> listener) {
        this.onAuthenticated = listener;
    }

    @Override
    public void setOnError(Consumer<Throwable> listener) {
        this.onError = listener;
    }
}