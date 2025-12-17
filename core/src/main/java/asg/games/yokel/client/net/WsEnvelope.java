package asg.games.yokel.client.net;

public class WsEnvelope {
    public String t;   // type, ex: "ClientHandshakeRequest"
    public int v = 1;  // version
    public String payload; // JSON string or nested object

    public WsEnvelope(String gameStartRequest, String json) {
        t = gameStartRequest;
        payload = json;
    }
}