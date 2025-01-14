package asg.games.yokel.client.managers;

import com.badlogic.gdx.utils.Disposable;

import asg.games.yokel.objects.YokelPlayer;

public interface GameClient extends Disposable {

    /**
     * Connects to the server.  Should use internal configuration
     **/
    boolean connectToServer();

    /**
     * Register a player to the server
     **/
    void requestPlayerRegister(YokelPlayer currentPlayer);
}
