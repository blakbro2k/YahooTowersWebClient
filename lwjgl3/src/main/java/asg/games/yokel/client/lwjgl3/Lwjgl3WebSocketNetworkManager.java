package asg.games.yokel.client.lwjgl3;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Consumer;

import asg.games.yipee.common.net.wire.ClientHandshakeRequest;
import asg.games.yipee.common.net.wire.GameAuthTokenResponse;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yokel.client.managers.GameNetworkManager;

public class Lwjgl3WebSocketNetworkManager implements GameNetworkManager {

    private final String host;
    private final int port;

    private WebSocket socket;
    private final Queue<String> messageQueue = new Queue<>();
    private final Json json = new Json();
    private volatile boolean connected;

    // new callbacks
    //private AuthListener onAuthenticated;
    private Consumer<GameAuthTokenResponse> onAuthenticated;
    private Consumer<Throwable> onError;

    // configure these to match your server
    private final String wsPath = "/ws/game";                 // <- adjust if your endpoint is /ws/game
    private final String launchParamName = "launchToken"; // <- MUST match interceptor query param

    public Lwjgl3WebSocketNetworkManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void setRequestToken(String tokenString) {
    }

    @Override
    public void setOnAuthenticated(Consumer<GameAuthTokenResponse> listener) {
        this.onAuthenticated = listener;
    }

    @Override
    public void setOnError(Consumer<Throwable> listener) {
        this.onError = listener;
    }

    @Override
    public boolean connect() {
        // connect without launch token (fallback/dev)
        return connectInternal(null);
    }

    @Override
    public boolean connectWithLaunchToken(String launchToken) {
        return connectInternal(launchToken);
    }

    private boolean connectInternal(String launchToken) {
        System.out.println("enter connectInternal()");
        if (socket != null && socket.isOpen()) return true;

        String url = WebSockets.toWebSocketUrl(host, port);

        // Append path if your server uses one
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        url = url + wsPath;

        // If launch token provided, use it for handshake
        if (launchToken != null && !launchToken.trim().isEmpty()) {
            url = url + "?" + launchParamName + "=" + encode(launchToken);
        }

        System.out.println("url=" + url);

        socket = WebSockets.newSocket(url);
        socket.setSendGracefully(true);

        socket.addListener(new WebSocketListener() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                System.out.println("WebSocketListener onOpen() fired.");
                //onAuthenticated.();
                connected = true;
                return true;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                System.out.println("WebSocketListener onClose() fired. code=" + closeCode + " reason=" + reason);
                connected = false;
                return true;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                System.out.println("WebSocketListener onError() fired.");
                connected = false;
                if (onError != null) onError.accept(error);
                error.printStackTrace();
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String message) {
                System.out.println("WebSocketListener onMessage() fired.");
                System.out.println("WebSocketListener onMessage() message." + message);
                messageQueue.addLast(message);
                System.out.println("messageQueue=" + messageQueue);
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                return false;
            }
        });

        socket.connect(); // async
        System.out.println("exit connectInternal()=true");
        return true;
    }

    private static boolean looksLikeGameAuth(String message) {
        // cheap and safe: don’t fully parse unless it smells right
        return message != null
                && message.contains("\"packetType\"")
                && message.contains("GameAuthTokenResponse");
    }

    private static String encode(String token) {
        try {
            return URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
        socket.send(json.toJson(packet));
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
        // Optional legacy. With LaunchToken flow, you probably won’t need this.
        ClientHandshakeRequest req = new ClientHandshakeRequest();
        req.setAuthToken(authToken);
        req.setPlayerId(player.getId());
        req.setClientId(clientId);
        req.setTimestamp(System.currentTimeMillis());
        send(req);
    }

    @Override
    public void registerPackets() {
        // no-op for JSON WS
    }
}