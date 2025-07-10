package asg.games.yokel.client.lwjgl3;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import asg.games.yokel.client.managers.GameNetworkManager;

public class KryoNetworkManager implements GameNetworkManager {

    private final Queue<Object> messageQueue = new ConcurrentLinkedQueue<>();

    public KryoNetworkManager(int connectTimeout, String host, int tcpPort, int udpPort) {
        //throw new UnsupportedOperationException("KryoNetworkManager is not supported on this platform.");
    }

    @Override
    public boolean connect() {
        //throw new UnsupportedOperationException("KryoNetworkManager is not supported on this platform.");
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean disconnect() {
        return true;
    }

    @Override
    public void send(Object packet) {
        // Do nothing
    }

    @Override
    public boolean hasMessage() {
        return false;
    }

    @Override
    public Object pollMessage() {
        return null;
    }

    @Override
    public void dispose() {
        messageQueue.clear();
    }
}