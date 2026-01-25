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

import asg.games.yipee.libgdx.net.GdxNetYipeePlayerDTO;
import asg.games.yipee.libgdx.net.GdxSeatStateUpdateResponse;
import asg.games.yipee.libgdx.net.GdxTableDetailsResponse;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.libgdx.objects.YipeeSeatGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import asg.games.yipee.net.packets.GameAuthTokenResponse;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.utils.JWTUtil;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.YokelUtilities;

@Component
public class ServerGameServices {

    @Inject
    private SessionService sessionService;
    @Inject
    private LoggerService loggerService;
    Log4LibGDXLogger logger;

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

    /**
     * Boot: session JWT -> launch token -> whoami -> table details.
     */
    public void boot(Ok<GdxTableDetailsResponse> onOk, Err onErr) {
        logger.enter("boot");

        try {
            ensureSessionJwt(); // sync (no network)
            logger.exit("boot");
        } catch (Throwable t) {
            onErr.run(t);
            logger.error("boot", t);
            return;
        }

        requestLaunchToken(
                launch -> getGameAuthToken(
                        auth -> getTableDetails(
                                tableDetails -> onOkOnRenderThread(onOk, tableDetails),
                                onErr
                        ),
                        onErr
                ),
                onErr
        );
    }

    public void bootWithLaunchToken(
            String launchToken,
            Ok<GdxTableDetailsResponse> onOk,
            Err onErr
    ) {
        logger.enter("bootWithLaunchToken");
        System.out.println("Enter bootWithLaunchToken()");
        if (launchToken == null || launchToken.isEmpty()) {
            onErr.run(new IllegalArgumentException("Missing launchToken"));
            logger.error("bootWithLaunchToken", new IllegalArgumentException("Missing launchToken"));
            return;
        }

        // Store it once; everything else derives from this
        sessionService.setLaunchToken(launchToken);
        logJwtPayload("Before: AUTH_TOKEN", sessionService.getAuthToken());      // dev JWT
        logJwtPayload("Before: LAUNCH_TOKEN", sessionService.getLaunchToken());  // game_session JWT

        getGameAuthToken(
                auth -> getTableDetails(
                        table -> onOkOnRenderThread(onOk, table),
                        onErr
                ),
                onErr
        );
        logJwtPayload("Before: AUTH_TOKEN", sessionService.getAuthToken());      // dev JWT
        logJwtPayload("Before: LAUNCH_TOKEN", sessionService.getLaunchToken());  // game_session JWT
        System.out.println("Exit bootWithLaunchToken()");
        logger.exit("bootWithLaunchToken");
    }

    private static void logJwtPayload(String label, String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            System.out.println("JWT:" + label + " payload=" + payload);
        } catch (Exception e) {
            System.err.println("JWT:" + label + " not decodable");
        }
    }

    // ----------------------------
    // Step 0: create session JWT
    // ----------------------------

    public void ensureSessionJwt() {
        ensureSessionJwt(false);
    }

    public void ensureSessionJwt(boolean force) {
        logger.enter("ensureSessionJwt");
        System.out.println("Enter ensureSessionJwt()=" + force);

        if (!force) {
            String existing = sessionService.getAuthToken();
            if (existing != null && !existing.isEmpty()) return;
        }

        String playerId = sessionService.getPlayerId();
        if (playerId == null || playerId.isEmpty()) {
            throw new IllegalStateException("Missing playerId in SessionService");
        }

        String username = defaultIfBlank(sessionService.getCurrentUserName(), "debug");

        Integer ratingObj = sessionService.getRating();   // or String if yours is String
        String rating = String.valueOf(ratingObj);

        Integer iconObj = sessionService.getIcon();
        String icon = String.valueOf(iconObj);

        System.out.println("Creating new Token ensureSessionJwt()");
        System.out.println("playerId=" + playerId);
        System.out.println("username=" + username);
        System.out.println("rating=" + rating);
        System.out.println("icon=" + icon);

        String token = JWTUtil.createMockJwt(playerId, username, rating, icon);
        System.out.println("token=" + token);
        sessionService.setAuthToken(token);
        System.out.println("sessionService.token=" + sessionService.getAuthToken());
        System.out.println("same?=" + (token.equals(sessionService.getAuthToken())));

        Gdx.app.log("JWT", "Generated dev JWT for playerId=" + playerId
                + " username=" + username + " rating=" + rating + " icon=" + icon);

        System.out.println("Exit ensureSessionJwt()");
        logger.exit("ensureSessionJwt");
    }


    // ----------------------------
    // Step 1: request launch token
    // ----------------------------

    public void requestLaunchToken(Ok<String> onOk, Err onErr) {
        logger.enter("requestLaunchToken");
        final String apiBase = "http://localhost:8080";
        final String url = apiBase + "/api/game/launch";

        final String sessionJwt = sessionService.getAuthToken();
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
                .content("{}")
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
                System.out.println("body: " + body);
                System.out.println("resp: " + resp);
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

                GameAuthTokenResponse resp = YokelUtilities.getObjectFromJsonString(GameAuthTokenResponse.class, body);
                if (resp == null) {
                    logger.error("getGameAuthToken", new GdxRuntimeException("GameAuthTokenResponse malformed: " + body));
                    onErrOnRenderThread(onErr, new GdxRuntimeException("GameAuthTokenResponse malformed: " + body));
                    return;
                }

                sessionService.setGameAuth(resp);
                sessionService.setCurrentTableId(resp.getTableId());
                sessionService.setCurrentGameId(resp.getGameId());
                sessionService.setCurrentPlayer(new YipeePlayerGDX(resp.name, resp.rating, resp.icon));
                sessionService.setPlayerId(resp.playerId);
                sessionService.setCurrentUserName(resp.name);
                sessionService.setIcon(resp.icon);
                sessionService.setRating(resp.rating);
                ensureSessionJwt(true);

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
        String launchToken = sessionService.getLaunchToken();
        String tableId = sessionService.getCurrentTableId();

        if (launchToken == null || launchToken.isEmpty()) {
            logger.error("getTableDetails", new IllegalStateException("Missing launchToken"));
            onErrOnRenderThread(onErr, new IllegalStateException("Missing launchToken"));
            return;
        }
        if (tableId == null || tableId.isEmpty()) {
            logger.error("getTableDetails", new IllegalStateException("Missing tableId"));
            onErrOnRenderThread(onErr, new IllegalStateException("Missing tableId"));
            return;
        }

        final String apiBase = "http://localhost:8080";
        final String url = apiBase + "/api/game/table";

        Net.HttpRequest req = new HttpRequestBuilder()
                .newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .header("Authorization", "Bearer " + launchToken)
                .header("Accept", "application/json")
                .timeout(10_000)
                .build();

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String body = httpResponse.getResultAsString();

                if (status != 200) {
                    onErrOnRenderThread(onErr, new GdxRuntimeException("Table error " + status + ": " + body));
                    return;
                }

                System.out.println("Received Body from Server: " + body);
                System.out.println("Received Body from Server: " + body.getClass());
                body = body.replace("rated", "isRated");
                //body = body.replace("ready", "isReady");
                body = body.replace("soundOn", "isSoundOn");

                GdxTableDetailsResponse tableDetails = YokelUtilities.getObjectFromJsonString(GdxTableDetailsResponse.class, body);
                if (tableDetails == null) {
                    onErrOnRenderThread(onErr, new GdxRuntimeException("TableDetailsResponse malformed: " + body));
                    return;
                }
                System.out.println("tableDetails: " + tableDetails);
                System.out.println("tableDetails: " + tableDetails.getClass());

                refreshSession(tableDetails);

                onOkOnRenderThread(onOk, tableDetails);
            }

            @Override
            public void failed(Throwable t) {
                onErrOnRenderThread(onErr, t);
            }

            @Override
            public void cancelled() {
                onErrOnRenderThread(onErr, new GdxRuntimeException("Table cancelled"));
            }
        });
    }

    private void refreshSession(GdxTableDetailsResponse tableDetails) {
        if (tableDetails != null) {
            sessionService.setTableDetails(tableDetails);
            sessionService.setServerId(tableDetails.serverId);
            sessionService.setGameId(tableDetails.gameId);
            sessionService.setSessionId(tableDetails.sessionId);
            sessionService.setServerTimestamp(tableDetails.serverTimestamp);
            sessionService.setTickRate(tableDetails.tickRate);
            sessionService.setTableId(tableDetails.tableId);
            sessionService.setCurrentRoomName(tableDetails.roomName);
            YipeeTableGDX table = buildTable(tableDetails);
            sessionService.setCurrentTable(table);
            sessionService.setCurrentTableId(table.getId());
            //sessionService.setCurrentPlayer(YipeePlayerGDX(resp.name, resp.rating, resp.icon));
            //sessionService.setPlayerId(resp.playerId);
            //sessionService.setCurrentUserName(resp.name);
            //sessionService.setIcon(resp.icon);
            //sessionService.setRating(resp.rating);
        }
    }

    private YipeeTableGDX buildTable(GdxTableDetailsResponse tableDetails) {
        YipeeTableGDX table = new YipeeTableGDX();
        if (tableDetails != null) {
            table.setId(tableDetails.tableId);
            table.setName("#" + tableDetails.tableNumber);
            table.setSoundOn(tableDetails.isSoundOn);
            table.setRated(tableDetails.isRated);
            table.setAccessType(tableDetails.tableAccessType);
            table.setSeats(buildSeats(tableDetails.seats));
            table.setWatchers(buildWatches(tableDetails.watchers));
        }
        return table;
    }

    private YipeePlayerGDX toGdxPlayer(GdxNetYipeePlayerDTO dto) {
        YipeePlayerGDX player = new YipeePlayerGDX();
        player.setId(dto.id);
        player.setName(dto.name);
        player.setCreated(dto.created);
        player.setModified(dto.modified);
        player.setIcon(dto.icon);
        player.setRating(dto.rating);
        return player;
    }

    private YipeeSeatGDX toGdxSeat(GdxSeatStateUpdateResponse dto) {
        YipeeSeatGDX seat = new YipeeSeatGDX();
        seat.setName("seatNumber_" + dto.seatIndex);
        seat.setParentTableId(dto.tableId);
        if (dto.occupied) {
            YipeePlayerGDX player = toGdxPlayer(dto.player);
            seat.setSeatedPlayer(player);
            if (dto.ready) {
                seat.setSeatReady(true);
            }
        }
        return seat;
    }

    private Iterable<YipeePlayerGDX> buildWatches(Array<GdxNetYipeePlayerDTO> watchers) {
        ObjectSet<YipeePlayerGDX> localWatchers = GdxSets.newSet();

        if (watchers != null) {
            watchers.forEach(dto -> localWatchers.add(toGdxPlayer(dto)));
        }

        return localWatchers;
    }

    private Iterable<YipeeSeatGDX> buildSeats(Array<GdxSeatStateUpdateResponse> seats) {
        ObjectSet<YipeeSeatGDX> localSeats = GdxSets.newSet();

        if (seats != null) {
            seats.forEach(dto -> localSeats.add(toGdxSeat(dto)));
        }

        return localSeats;
    }

    // ----------------------------
    // helpers
    // ----------------------------

    private static <T> void onOkOnRenderThread(Ok<T> ok, T value) {
        if (ok == null) return;
        Gdx.app.postRunnable(() -> ok.run(value));
    }

    private static void onErrOnRenderThread(Err err, Throwable t) {
        if (err == null) return;
        Gdx.app.postRunnable(() -> err.run(t));
    }

    private static String defaultIfBlank(String s, String def) {
        return (s == null || s.trim().isEmpty()) ? def : s.trim();
    }
}