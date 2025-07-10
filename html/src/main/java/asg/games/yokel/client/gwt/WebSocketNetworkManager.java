package asg.games.yokel.client.gwt;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

import asg.games.yokel.client.managers.GameNetworkManager;

public class WebSocketNetworkManager implements GameNetworkManager {

    private final String host;
    private final int port;
    private WebSocket socket;
    private boolean connected = false;

    private final Queue<Object> messageQueue = new Queue<>();
    private final Json json = new Json();

    public WebSocketNetworkManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean connect() {
        if (connected) return false;
        try {
            String url = WebSockets.toWebSocketUrl(host, port);
            socket = WebSockets.newSocket(url);

            socket.setSendGracefully(true);
            socket.addListener(new WebSocketListener() {
                @Override
                public boolean onOpen(WebSocket webSocket) {
                    connected = true;
                    System.out.println("WebSocket connected!");
                    return true;
                }

                @Override
                public boolean onClose(WebSocket webSocket, WebSocketCloseCode code, String reason) {
                    return false;
                }


                @Override
                public boolean onError(WebSocket webSocket, Throwable error) {
                    connected = false;
                    error.printStackTrace();
                    return true;
                }

                @Override
                public boolean onMessage(WebSocket webSocket, String message) {
                    // Deserialize JSON string to generic Object.
                    try {
                        Object obj = json.fromJson(Object.class, message);
                        messageQueue.addLast(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

                @Override
                public boolean onMessage(WebSocket webSocket, byte[] packet) {
                    return false;
                }
            });

            socket.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        return false;
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
}