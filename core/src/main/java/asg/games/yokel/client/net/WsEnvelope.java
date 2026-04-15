package asg.games.yokel.client.net;

public class WsEnvelope {
    public String type;   // type, ex: "ClientHandshakeRequest"
    public int v = 1;  // version
    public Object payload; // JSON string or nested object

    public WsEnvelope(String wsClassString, Object payload) {
        type = wsClassString;
        this.payload = payload;
    }
}