package asg.games.yokel.client.managers;

import com.badlogic.gdx.utils.Disposable;

import java.util.function.Consumer;

import asg.games.yipee.game.PlayerAction;
import asg.games.yipee.objects.YipeePlayer;

public interface GameClientManager extends Disposable {

    boolean connect();

    boolean isConnected();

    boolean disconnect();

    void sendConnectionRequest(String clientId, String sessionId, YipeePlayer player);

    void sendJoinRoomRequest(String roomName);

    void sendStartGameRequest();

    void sendSeatSitDownRequest(String tableId, int seatId);

    void sendPlayerInput(PlayerAction action);

    void registerListener(Class<?> packetClass, Consumer<Object> handler); // Optional: dynamic listener registry
}
