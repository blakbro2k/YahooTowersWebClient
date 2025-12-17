package asg.games.yokel.client.gwt;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

import asg.games.yipee.common.dto.NetYipeePlayer;
import asg.games.yipee.net.packets.ClientHandshakeRequest;
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
        if (connected) return true;
        try {
            String url = WebSockets.toWebSocketUrl(host, port);
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
                    return true;
                }

                @Override
                public boolean onMessage(WebSocket webSocket, String message) {
                    messageQueue.addLast(message);
                    return true;
                }

                @Override
                public boolean onMessage(WebSocket webSocket, byte[] packet) {
                    // Not used — JSON protocol is text-only
                    return false;
                }
            });

            socket.connect();
            return true; // "connection initiated"
        } catch (Exception e) {
            connected = false;
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

    @Override
    public void registerUser(String authToken, NetYipeePlayer player, String clientId, String sessionKey) {
        ClientHandshakeRequest requestPacket = new ClientHandshakeRequest();
        requestPacket.setAuthToken(authToken);
        requestPacket.setPlayerId(player.getId());
        requestPacket.setClientId(clientId);
        //requestPacket.setSessionKey(sessionKey);
        requestPacket.setClientTick(-1);
        requestPacket.setTimestamp(TimeUtils.millis());
        send(requestPacket);
    }

    @Override
    public void registerPackets() {

    }
}