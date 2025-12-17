package asg.games.yokel.client.managers;

import asg.games.yipee.common.dto.NetYipeePlayer;

public interface GameNetworkManager {

    /**
     * Initiates connection to the server.
     * On HTML/WebSocket, this is async.
     *
     * @return
     */
    boolean connect();

    /**
     * @return true if the client considers itself connected.
     */
    boolean isConnected();

    /**
     * Closes the connection.
     *
     */
    boolean disconnect();

    /**
     * Frees all resources.
     */
    void dispose();

    /**
     * Sends a single packet object to the server.
     * The implementation handles serialization (JSON or Kryo).
     *
     * @param packet POJO to send
     */
    void send(Object packet);

    /**
     * Checks if there is any message received from the server.
     *
     * @return true if there is at least one message in the queue.
     */
    boolean hasMessage();

    /**
     * Retrieves and removes the next message received from the server, or null if none.
     *
     * @return next received packet or null
     */
    Object pollMessage();

    void registerUser(String authToken, NetYipeePlayer player, String clientId, String sessionKey);

    void registerPackets();
}