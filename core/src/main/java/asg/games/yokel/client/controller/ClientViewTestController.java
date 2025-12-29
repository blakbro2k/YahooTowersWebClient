package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.net.packets.GameAuthTokenResponse;
import asg.games.yipee.net.packets.GameStartRequest;
import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.managers.GameNetFactory;
import asg.games.yokel.client.managers.GameNetworkManager;
import asg.games.yokel.client.net.WsEnvelope;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GamePlayerBoard;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.YokelUtilities;

@View(id = GlobalConstants.UI_DEBUG_CLIENT_VIEW, value = GlobalConstants.UI_TEST_CLIENT_VIEW_PATH)
public class ClientViewTestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    @Inject
    private UserInterfaceService uiService;
    @Inject
    private SessionService sessionService;
    @Inject
    private InterfaceService interfaceService;
    @Inject
    private MusicService musicService;
    @Inject
    private LoadingController assetController;
    @Inject
    private LoggerService loggerService;

    private Log4LibGDXLogger logger;

    private final Json json = new Json();
    private GameNetworkManager networkManager;

    @LmlActor("accessTokenField")
    private VisTextField accessTokenField;
    @LmlActor("sessionIdField")
    private VisTextField sessionIdField;
    @LmlActor("playerIdField")
    private VisTextField playerIdField;
    @LmlActor("roomSelectBox")
    private VisSelectBox<String> roomSelectBox;
    @LmlActor("tableSelectBox")
    private VisSelectBox<String> tableSelectBox;
    @LmlActor("whoAmI")
    private VisLabel whoAmI;
    private boolean showGameOver;

    @LmlActor("1:area")
    private GamePlayerBoard uiArea1;
    @LmlActor("2:area")
    private GamePlayerBoard uiArea2;

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        try {
            initiate();
        } catch (Exception e) {
            String errorMsg = "error initialize()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    @Override
    public void destroy(ViewController viewController) {
        try {

        } catch (Exception e) {
            String errorMsg = "error destroy()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    @Override
    public void render(Stage stage, float delta) {
        try {
            //Render
            stage.act(delta);
            stage.draw();
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
            //throw new ReflectionException(e);
        }
    }

    private void initiate() throws ReflectionException {
        networkManager = GameNetFactory.getClientManager();
    }

    public String getAccessTokenFieldValue() {
        if (accessTokenField != null) {
            return trimToEmpty(accessTokenField.getText());
        } else {
            return "";
        }
    }

    public void sessionIdChanged() {
        sessionService.setSessionKey(trimToNull(sessionIdField.getText()));
    }

    public void playerIdChanged() {
        sessionService.setPlayerId(trimToNull(playerIdField.getText()));
    }

    @LmlAction("getRooms")
    public Array<String> getRooms() {
        return GdxArrays.newArray("Room1", "Room2", "Room3");
    }

    @LmlAction("getTableDetails")
    public Array<String> getTableDetails() {
        return GdxArrays.newArray("Tables1", "Tables2", "Tables3");
    }

    /**
     * Same algorithm as yipee-lobby.html
     */
    @LmlAction("sendJWTValue")
    public void sendJWTValue() {
        YipeePlayerGDX player = sessionService.getCurrentPlayer();
        String playerId = sessionService.getPlayerId();
        String username = sessionService.getCurrentUserName(); // or pull from another field
        String rating = sessionService.getRating();          // if you track it
        String icon = sessionService.getIcon();            // if you track it

        // If you don't have username/rating/icon in this debug screen yet:
        if (username == null) username = "debug";
        if (rating == null) rating = "0";
        if (icon == null) icon = "0";

        String jwt = createMockJwt(playerId, username, rating, icon);
        sessionService.setAuthToken(jwt);

        // optionally show it somewhere or log it
        Gdx.app.log("JWT", "Generated dev JWT for playerId=" + playerId);
    }

    @LmlAction("getGameAuthToken")
    public void getGameAuthToken() {
        System.out.println("Enter getGameAuthToken()");
        final String launchToken = getAccessTokenFieldValue();
        System.out.println("launchToken=" + launchToken);
        if (launchToken.trim().isEmpty()) {
            //gameWhoamiOut.setText("Paste a launchToken first.");
            return;
        }
        final String apiBase = "http://localhost:8080"; // NO trailing slash
        final String url = apiBase + "/api/game/whoami";

        Net.HttpRequest req = new HttpRequestBuilder()
                .newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .header("Authorization", "Bearer " + launchToken)
                .header("Accept", "application/json")
                .timeout(10_000) // 10s
                .build();

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final int status = httpResponse.getStatus().getStatusCode();
                final String body = httpResponse.getResultAsString();

                // UI updates should happen on the render thread
                Gdx.app.postRunnable(() -> {
                    if (status == 200) {
                        GameAuthTokenResponse resp = YokelUtilities.getObjectFromJsonString(GameAuthTokenResponse.class, body);
                        if (resp == null) {
                            throw new GdxRuntimeException("GameAuthTokenResponse was malformed or invalid!");
                        }

                        Gdx.app.log("AUTH", "playerId=" + resp.playerId + " tableId=" + resp.tableId);
                        whoAmI.setText(resp.getName());
                        System.out.println("Exit getGameAuthToken()=200" + ": " + body);
                    } else {
                        // gameWhoamiOut.setText("Error " + status + ": " + body);
                        System.out.println("Exit getGameAuthToken()=" + "Error " + status + ": " + body);
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                sessionService.handleException(logger, t);
            }

            @Override
            public void cancelled() {
                //Gdx.app.postRunnable(() -> gameWhoamiOut.setText("Request cancelled."));
            }
        });
        System.out.println("Exit getGameAuthToken()");
    }

    @LmlAction("startGame")
    public void startGame() throws ReflectionException {
        try {
            // Ensure you are connected first
            if (!networkManager.isConnected()) {
                sessionService.connectToServer(); // or networkManager.connect()
            }

            GameStartRequest req = new GameStartRequest();
            req.setClientId(sessionService.getClientId());
            req.setSessionId(sessionService.getSessionKey());
            req.setPlayerId(sessionService.getPlayerId());
            req.setAuthToken(sessionService.getAuthToken());

            // If your server requires tableId/seatNumber, set them too:
            // req.tableId = sessionService.getCurrentTableId();
            // req.seatNumber = sessionService.getCurrentSeat();

            WsEnvelope env = new WsEnvelope("GameStartRequest", json.toJson(req));
            networkManager.send(env);
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    // ----------------------
    // JWT helpers
    // ----------------------

    private String createMockJwt(String playerId, String username, String rating, String icon) {
        String header = base64UrlJson("{\"alg\":\"none\"}");
        String payload = base64UrlJson(json.toJson(new JwtPayload(playerId, username, rating, icon)));
        return header + "." + payload + ".dev";
    }

    private String base64UrlJson(String jsonStr) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(jsonStr.getBytes(StandardCharsets.UTF_8));
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private String trimToEmpty(String s) {
        return trimToNull(s) == null ? "" : s;
    }

    private static class JwtPayload {
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