package asg.games.yokel.client.utils;

public class JWTUtil {
    private JWTUtil() {
    }

    // ----------------------
    // JWT helpers

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
