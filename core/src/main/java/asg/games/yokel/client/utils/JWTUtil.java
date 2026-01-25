package asg.games.yokel.client.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JWTUtil {
    private JWTUtil() {
    }

    // ----------------------
    // JWT helpers
    // ----------------------

    public static String createMockJwt(String playerId, String username, String rating, String icon) {
        String header = base64UrlJson("{\"alg\":\"none\"}");
        String payload = base64UrlJson(YokelUtilities.getJsonString(JwtPayload.class, new JwtPayload(playerId, username, rating, icon)));
        return header + "." + payload + ".dev";
    }

    public static String base64UrlJson(String jsonStr) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(jsonStr.getBytes(StandardCharsets.UTF_8));
    }

    public static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    public static String trimToEmpty(String s) {
        return trimToNull(s) == null ? "" : s;
    }

    static class JwtPayload {
        public String sub;
        public String username;
        public String rating;
        public String icon;

        public JwtPayload(String sub, String username, String rating, String icon) {
            this.sub = sub;
            this.username = username;
            this.rating = rating;
            this.icon = icon;
        }
    }
}
