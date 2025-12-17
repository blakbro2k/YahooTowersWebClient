package asg.games.yokel.client.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import asg.games.yipee.common.dto.NetYipeePlayer;
import asg.games.yipee.net.packets.ClientHandshakeRequest;
import asg.games.yipee.net.tools.PacketRegistrar;
import asg.games.yokel.client.managers.GameNetworkManager;

public class KryoNetworkManager implements GameNetworkManager {

    private final Queue<Object> messageQueue = new ConcurrentLinkedQueue<>();
    private final Client client = new Client();
    private final int connectTimeoutMs;
    private final String host;
    private final int tcpPort;
    private final int udpPort;

    private volatile boolean started = false;

    public KryoNetworkManager(int connectTimeout, String host, int tcpPort, int udpPort) {
        this.connectTimeoutMs = connectTimeout;
        this.host = host;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                // Optionally enqueue a higher level event object
                // messageQueue.add(new ConnectedEvent());
                System.out.println("Enter connected()=" + connection.isConnected());
            }

            @Override
            public void disconnected(Connection connection) {
                // Optionally enqueue a higher level event object
                // messageQueue.add(new DisconnectedEvent());
                System.out.println("Enter disconnected()=" + connection.isConnected());
            }

            @Override
            public void received(Connection connection, Object object) {
                // Push to queue for the game loop to process on main thread
                messageQueue.offer(object);
            }
        });
    }

    @Override
    public void registerPackets() {
        System.out.println("Enter registerPackets()");
        FileHandle packetsFile = Gdx.files.internal("assets/config/libgdxPackets.xml");
        if (!packetsFile.exists()) {
            throw new GdxRuntimeException(packetsFile + " not found on classpath");
        }

        String path = packetsFile.path();
        try {
            PacketRegistrar.reloadConfiguration(path);
            PacketRegistrar.registerPackets(client.getKryo());
            PacketRegistrar.printRegisteredPackets();
        } catch (ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean connect() {
        System.out.println("Enter KryoNetworkManager#connect()");
        if (!started) {
            registerPackets();
            client.start();
            started = true;
        }
        try {
            // If you don't use UDP, you can pass -1 or 0 for udpPort and call connect(timeout, host, tcpPort)
            if (udpPort > 0) {
                client.connect(connectTimeoutMs, host, tcpPort, udpPort);
            } else {
                client.connect(connectTimeoutMs, host, tcpPort);
            }

            System.out.println("Exit KryoNetworkManager#connect(): true");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exit KryoNetworkManager#connect(): false");
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public boolean disconnect() {
        started = false;
        try {
            if (started) {
                client.close(); // closes and joins the client thread
                started = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            started = false;
        }
        return started;
    }

    @Override
    public void send(Object packet) {
        // Default to TCP (reliable). If you need UDP for a specific packet type, add a heuristic or an overload.
        if (!client.isConnected()) return;
        client.sendTCP(packet);
        // client.sendUDP(packet); // if you have a UDP-safe packet
    }

    @Override
    public boolean hasMessage() {
        return !messageQueue.isEmpty();
    }

    @Override
    public Object pollMessage() {
        return messageQueue.poll();
    }

    @Override
    public void registerUser(String authToken, NetYipeePlayer player, String clientId, String sessionKey) {
        ClientHandshakeRequest requestPacket = new ClientHandshakeRequest();
        requestPacket.setAuthToken(authToken);
        requestPacket.setPlayerId(player.getId());
        requestPacket.setClientId(clientId);
        requestPacket.setAuthToken(authToken);
        requestPacket.setClientTick(-1);
        requestPacket.setTimestamp(TimeUtils.millis());
        send(requestPacket);
    }

    @Override
    public void dispose() {
        messageQueue.clear();
        if (started) {
            // Try to let KryoNet close cleanly
            try {
                client.stop();
                // tiny grace period to allow shutdown hooks (optional)
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception ignored) {
            }
            started = false;
        }
    }
}