package asg.games.yokel.client.game;

public class Version {
    private static final String release = "1";
    private static final String major = "13";
    private static final String minor = "0";
    private static final String patch = "0";
    private static final String releaseSeparator = ".";
    private static final String patchSeparator = "p";

    public static String printVersion() {
        return release + releaseSeparator + major + releaseSeparator + minor + patchSeparator + patch;
    }
}
