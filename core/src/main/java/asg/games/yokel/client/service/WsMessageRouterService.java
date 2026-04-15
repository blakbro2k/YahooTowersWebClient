package asg.games.yokel.client.service;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.kiwi.log.LoggerService;

import java.util.HashMap;
import java.util.Map;

import asg.games.yipee.common.net.wire.GameAuthTokenResponse;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.managers.GameNetworkManager;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.YokelUtilities;

@Component
public class WsMessageRouterService {

    @Inject
    private SessionService sessionService;
    @Inject
    private LoggerService loggerService;
    private Log4LibGDXLogger logger;
    private Runnable authOnce;

    public void onAuthOnce(Runnable r) {
        this.authOnce = r;
    }

    // Optional: handlers for other packet types
    public interface Handler {
        void handle(String rawJson);
    }

    private final Map<String, Handler> handlers = new HashMap<>();

    public void on(String packetType, Handler handler) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        handlers.put(packetType, handler);
    }

    /**
     * Call this once per frame (or on a timer)
     */
    public void pump(GameNetworkManager networkManager) {
        while (networkManager.hasMessage()) {
            Object rawObj = networkManager.pollMessage();
            String raw = "";
            if (!(rawObj instanceof String)) {
                continue;
            } else {
                raw = (String) rawObj;
            }

            route(raw);
        }
    }

    public void route(String raw) {
        String packetType = extractPacketType(raw);
        Gdx.app.log("WsMessageRouterService", "Enter route()");

        if ("GameAuthTokenResponse".equals(packetType)) {
            Gdx.app.log("WsMessageRouterService", "Enter GameAuthTokenResponse");
            Gdx.app.log("WsMessageRouterService", "raw=" + raw);
            GameAuthTokenResponse auth = YokelUtilities.getObjectFromJsonString(GameAuthTokenResponse.class, raw);
            sessionService.applyGameAuth(auth);
            sessionService.handleGameAuthTokenResponse();

            if (authOnce != null) {
                Runnable r = authOnce;
                authOnce = null;
                Gdx.app.postRunnable(r); // safe, you’re likely touching UI/state next
            }
            return;
        }

        Gdx.app.log("WsMessageRouterService", "handling packet type=" + packetType);
        System.out.println();
        Handler handler = handlers.get(packetType);
        if (handler != null) {
            handler.handle(raw);
        } else {
            // default: ignore or log
            Gdx.app.log("WsMessageRouterService", "Unhandled packetType=" + packetType + " raw=" + raw);
        }
    }


    /**
     * Cheap extraction without full parse. Since your server includes "packetType": "...",
     * we can string-scan it.
     */
    private static String extractPacketType(String raw) {
        if (raw == null) return null;
        String key = "\"packetType\":\"";
        int i = raw.indexOf(key);
        if (i < 0) return null;
        int start = i + key.length();
        int end = raw.indexOf('"', start);
        if (end < 0) return null;
        return raw.substring(start, end);
    }
}