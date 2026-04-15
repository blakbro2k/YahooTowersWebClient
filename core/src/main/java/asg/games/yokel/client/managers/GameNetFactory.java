package asg.games.yokel.client.managers;

// core/src/main/java/asg/games/yokel/client/managers/GameNetFactory.java
public class GameNetFactory {
    private static GameNetworkManager instance;
    private static GameNetworkManager webInstance;

    public static void registerClientManager(GameNetworkManager manager) {
        instance = manager;
    }

    public static GameNetworkManager getManager() {
        if (instance == null) {
            throw new IllegalStateException("GameNetworkManager not registered!");
        }
        return instance;
    }
}