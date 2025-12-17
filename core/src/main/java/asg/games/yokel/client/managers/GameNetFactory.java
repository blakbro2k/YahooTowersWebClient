package asg.games.yokel.client.managers;

// core/src/main/java/asg/games/yokel/client/managers/GameNetFactory.java
public class GameNetFactory {
    private static GameNetworkManager instance;
    private static GameNetworkManager webInstance;

    public static void registerClientManager(GameNetworkManager manager) {
        instance = manager;
    }

    public static GameNetworkManager getClientManager() {
        if (instance == null) {
            throw new IllegalStateException("GameNetworkManager not registered!");
        }
        return instance;
    }

    public static void registerWebClientManager(GameNetworkManager newInstance) {
        webInstance = newInstance;
    }

    public static GameNetworkManager getDesktopWebClientManager() {
        if (webInstance == null) {
            throw new IllegalStateException("DesktopWebClientManager not registered!");
        }
        return webInstance;
    }
}