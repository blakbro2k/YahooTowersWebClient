package asg.games.yokel.client.configuration.preferences;

import lombok.Getter;
import lombok.Setter;

public class BootstrapConfig {
    @Setter
    @Getter
    private static boolean debugMode = true;
    @Setter
    @Getter
    private static String jwtToken = null;

    @Setter
    @Getter
    private static String apiToken = null;

    @Setter
    @Getter
    private static String launchToken = null;

    @Setter
    @Getter
    private static String clientId = null;

    @Setter
    @Getter
    private static String sessionId = null;

}
