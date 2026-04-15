package asg.games.yokel.client.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;

import asg.games.yipee.common.net.wire.GameAuthTokenResponse;
import asg.games.yipee.libgdx.net.GdxNetYipeePlayerDTO;
import asg.games.yipee.libgdx.net.GdxSeatStateUpdateResponse;
import asg.games.yipee.libgdx.net.GdxTableDetailsResponse;
import asg.games.yipee.libgdx.net.GdxTableDetailsSummary;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.libgdx.objects.YipeeSeatGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.managers.GameNetworkManager;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.PayloadUtil;
import asg.games.yokel.client.utils.YokelUtilities;
import lombok.Getter;
import lombok.Setter;

@Component
public class ServerGameService {
    @Inject
    private WsMessageRouterService wsMessageRouterService;

    @Inject
    private SessionService sessionService;
    @Inject
    private LoggerService loggerService;
    Log4LibGDXLogger logger;


    @Setter
    @Getter
    private Ok<GdxTableDetailsResponse> pendingBootOk;

    @Setter
    @Getter
    private Err pendingBootErr;

    /**
     * Small functional callback type.
     */
    public interface Ok<T> {
        void run(T value);
    }

    public interface Err {
        void run(Throwable t);
    }

    @Initiate
    public void initialize() throws InterruptedException {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        logger.setDebug();
        logger.enter("initialize");

        logger.exit("initialize");
    }

    // ----------------------------
    // High-level boot (reusable)
    // ----------------------------

    public void bootWithLaunchToken(String launchToken, Ok<GdxTableDetailsResponse> onOk, Err onErr) {
        logger.enter("bootWithLaunchToken");

        if (launchToken == null) {
            logger.error("bootWithLaunchToken", new IllegalArgumentException("Missing launchToken"));
            onErrOnRenderThread(onErr, new IllegalStateException("Missing launchToken"));
            return;
        }

        this.pendingBootOk = onOk;
        this.pendingBootErr = onErr;

        GameNetworkManager nm = sessionService.getNetworkManager();
        logger.error("connectWithLaunchToken()");
        nm.connectWithLaunchToken(launchToken);

        logger.exit("bootWithLaunchToken");
    }

    // ----------------------------
    // Step 0: create session JWT
    // ----------------------------


    // ----------------------------
    // Step 1: request launch token
    // ----------------------------

    public void requestLaunchToken(Ok<String> onOk, Err onErr) {
        logger.enter("requestLaunchToken");
        final String apiBase = "http://localhost:8080";
        final String url = apiBase + "/api/game/getlaunchToken";

        final String sessionJwt = sessionService.getApiToken();
        if (sessionJwt == null || sessionJwt.isEmpty()) {
            logger.error("requestLaunchToken", new IllegalArgumentException("Missing session JWT"));
            onErrOnRenderThread(onErr, new IllegalStateException("Missing session JWT"));
            return;
        }

        Net.HttpRequest req = new HttpRequestBuilder()
                .newRequest()
                .method(Net.HttpMethods.POST)
                .url(url)
                .header("Authorization", "Bearer " + sessionJwt)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .content("{\"tableId\":\"" + sessionService.getCurrentTableId() + "\"}")
                .timeout(10_000)
                .build();

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String body = httpResponse.getResultAsString();

                if (status != 200) {
                    logger.error("requestLaunchToken", new GdxRuntimeException("Launch error " + status + ": " + body));
                    onErrOnRenderThread(onErr, new GdxRuntimeException("Launch error " + status + ": " + body));
                    return;
                }

                LaunchTokenResponse resp = YokelUtilities.getObjectFromJsonString(LaunchTokenResponse.class, body);

                if (resp == null || resp.launchToken == null || resp.launchToken.isEmpty()) {
                    logger.error("requestLaunchToken", new IllegalArgumentException("LaunchTokenResponse malformed: " + body));
                    onErrOnRenderThread(onErr, new GdxRuntimeException("LaunchTokenResponse malformed: " + body));
                    return;
                }

                sessionService.setLaunchToken(resp.launchToken);
                onOkOnRenderThread(onOk, resp.launchToken);
            }

            @Override
            public void failed(Throwable t) {
                onErrOnRenderThread(onErr, t);
            }

            @Override
            public void cancelled() {
                onErrOnRenderThread(onErr, new GdxRuntimeException("Launch cancelled"));
            }
        });
        logger.exit("requestLaunchToken");
    }

    public static class LaunchTokenResponse {
        public String launchToken;
        public long expiresAt;
        public String wsUrl;
    }

    // ----------------------------
    // Step 2: whoami (launch token -> auth details)
    // ----------------------------

    public void getGameAuthToken(Ok<GameAuthTokenResponse> onOk, Err onErr) {
        logger.enter("getGameAuthToken");
        final String launchToken = sessionService.getLaunchToken();

        if (launchToken == null || launchToken.isEmpty()) {
            logger.error("getGameAuthToken", new IllegalArgumentException("Missing launchToken"));
            onErrOnRenderThread(onErr, new IllegalStateException("Missing launchToken"));
            return;
        }

        final String apiBase = "http://localhost:8080";
        final String url = apiBase + "/api/game/whoami";

        Net.HttpRequest req = new HttpRequestBuilder()
                .newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .header("Authorization", "Bearer " + launchToken)
                .header("Accept", "application/json")
                .timeout(10_000)
                .build();

        logger.debug("launchToken={}", launchToken);
        logger.debug("req={}", req);
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String body = httpResponse.getResultAsString();

                if (status != 200) {
                    logger.error("getGameAuthToken", new GdxRuntimeException("Whoami error " + status + ": " + body));
                    onErrOnRenderThread(onErr, new GdxRuntimeException("Whoami error " + status + ": " + body));
                    return;
                }

                System.out.println("body: " + body);
                GameAuthTokenResponse resp = YokelUtilities.getObjectFromJsonString(GameAuthTokenResponse.class, body);
                System.out.println("resp: " + resp);
                sessionService.setGameAuth(resp);

                if (resp == null) {
                    logger.error("getGameAuthToken", new GdxRuntimeException("GameAuthTokenResponse malformed: " + body));
                    onErrOnRenderThread(onErr, new GdxRuntimeException("GameAuthTokenResponse malformed: " + body));
                    return;
                }

                onOkOnRenderThread(onOk, resp);
            }

            @Override
            public void failed(Throwable t) {
                onErrOnRenderThread(onErr, t);
            }

            @Override
            public void cancelled() {
                onErrOnRenderThread(onErr, new GdxRuntimeException("Whoami cancelled"));
            }
        });
    }

    // ----------------------------
    // Step 3: table details (game scoped)
    // ----------------------------

    public void getTableDetails(Ok<GdxTableDetailsResponse> onOk, Err onErr) {
        logger.enter("getTableDetails");

        String tableId = sessionService.getCurrentTableId();
        String apiToken = sessionService.getApiToken();

        if (tableId == null || tableId.isEmpty()) {
            IllegalStateException ex = new IllegalStateException("Missing tableId");
            logger.error("getTableDetails", ex);
            onErrOnRenderThread(onErr, ex);
            return;
        }

        if (apiToken == null || apiToken.isEmpty()) {
            IllegalStateException ex = new IllegalStateException("Missing apiToken");
            logger.error("getTableDetails", ex);
            onErrOnRenderThread(onErr, ex);
            return;
        }

        final String apiBase = "http://localhost:8080";
        final String url = apiBase + "/api/tables/" + tableId;

        logger.debug("tableId={}", tableId);
        logger.debug("url={}", url);

        Net.HttpRequest req = new HttpRequestBuilder()
                .newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/json")
                .timeout(10_000)
                .build();

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String body = httpResponse.getResultAsString();

                logger.debug("getTableDetails status={}", status);
                logger.debug("getTableDetails body={}", body);

                if (status != 200) {
                    onErrOnRenderThread(onErr,
                            new GdxRuntimeException("Table error " + status + ": " + body));
                    return;
                }

                GdxTableDetailsResponse tableDetails = YokelUtilities.getObjectFromJsonString(GdxTableDetailsResponse.class, body);

                if (tableDetails == null) {
                    onErrOnRenderThread(onErr,
                            new GdxRuntimeException("TableDetailsResponse malformed: " + body));
                    return;
                }
                logger.debug("tableDetails={}", tableDetails);

                refreshSession(tableDetails);
                onOkOnRenderThread(onOk, tableDetails);
            }

            @Override
            public void failed(Throwable t) {
                logger.error(t, "getTableDetails failed");
                onErrOnRenderThread(onErr, t);
            }

            @Override
            public void cancelled() {
                logger.debug("getTableDetails message cancelled");
                onErrOnRenderThread(onErr, new GdxRuntimeException("Table cancelled"));
            }
        });
    }

    private void refreshSession(GdxTableDetailsResponse tableDetails) {
        logger.enter("refreshSession");
        if (tableDetails != null) {
            GdxTableDetailsSummary tableDetailsSummary = tableDetails.getTableDetailsSummary();

            if (tableDetailsSummary != null) {
                sessionService.setTableDetails(tableDetails);/*
               sessionService.setServerId(tableDetails.serverId);
               sessionService.setCurrentGameId(tableDetails.gameId);
               sessionService.setSessionId(tableDetails.sessionId);
               sessionService.setServerTimestamp(tableDetails.serverTimestamp);
               sessionService.setTickRate(tableDetails.tickRate);*/
                sessionService.setCurrentRoomName(tableDetails.getRoomName());
                YipeeTableGDX table = PayloadUtil.getTableFromTableDetailsResponse(tableDetails);
                sessionService.setCurrentTable(table);
                sessionService.setCurrentTableId(table.getId());
                sessionService.setCurrentTableId(table.getId());
                sessionService.setCurrentSeat(getSeatFromTable(sessionService.getCurrentPlayer(), table));
            }


        }
        logger.exit("refreshSession");
    }

    private int getSeatFromTable(YipeePlayerGDX currentPlayer, YipeeTableGDX table) {
        int seat = -1;
        if (currentPlayer != null && table != null) {

        }
        return seat;
    }

    private Iterable<YipeePlayerGDX> buildWatchers(Array<GdxNetYipeePlayerDTO> watchers) {
        ObjectSet<YipeePlayerGDX> localWatchers = GdxSets.newSet();

        if (watchers != null) {
            watchers.forEach(dto -> localWatchers.add(PayloadUtil.toGdxPlayer(dto)));
        }

        return localWatchers;
    }

    private Iterable<YipeeSeatGDX> buildSeats(Array<GdxSeatStateUpdateResponse> seats) {
        ObjectSet<YipeeSeatGDX> localSeats = GdxSets.newSet();

        if (seats != null) {
            seats.forEach(dto -> localSeats.add(PayloadUtil.toGdxSeat(dto)));
        }

        return localSeats;
    }

    // ----------------------------
    // helpers
    // ----------------------------

    public static <T> void onOkOnRenderThread(Ok<T> ok, T value) {
        if (ok == null) return;
        Gdx.app.postRunnable(() -> ok.run(value));
    }

    public static void onErrOnRenderThread(Err err, Throwable t) {
        if (err == null) return;
        Gdx.app.postRunnable(() -> err.run(t));
    }

    public static String defaultIfBlank(String s, String def) {
        return (s == null || s.trim().isEmpty()) ? def : s.trim();
    }
}