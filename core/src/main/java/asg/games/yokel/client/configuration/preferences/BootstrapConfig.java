package asg.games.yokel.client.configuration.preferences;

public class BootstrapConfig {
    private static boolean debugMode = false;
    private static String jwtToken = null;

    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setJwtToken(String jwt) {
        jwtToken = jwt;
    }

    public static String getJwtToken() {
        return jwtToken;
    }
}
