package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
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

import asg.games.yipee.common.net.wire.AbstractClientRequest;
import asg.games.yipee.common.net.wire.GameStartRequest;
import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.configuration.preferences.BootstrapConfig;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.managers.GameNetFactory;
import asg.games.yokel.client.managers.GameNetworkManager;
import asg.games.yokel.client.net.WsEnvelope;
import asg.games.yokel.client.service.ServerGameService;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.service.WsMessageRouterService;
import asg.games.yokel.client.ui.actors.GamePlayerBoard;
import asg.games.yokel.client.utils.JWTUtil;
import asg.games.yokel.client.utils.LogUtil;

@View(id = GlobalConstants.UI_DEBUG_CLIENT_VIEW, value = GlobalConstants.UI_TEST_CLIENT_VIEW_PATH)
public class ClientViewTestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    @Inject
    private UserInterfaceService uiService;
    @Inject
    private SessionService sessionService;
    @Inject
    private ServerGameService serverGameService;
    @Inject
    private InterfaceService interfaceService;
    @Inject
    private MusicService musicService;
    @Inject
    private LoadingController assetController;
    @Inject
    private LoggerService loggerService;
    @Inject
    private WsMessageRouterService wsMessageRouterService;
    private Log4LibGDXLogger logger;

    private GameNetworkManager networkManager;

    @LmlActor("apiTokenField")
    private VisTextField apiTokenField;
    @LmlActor("launchTokenField")
    private VisTextField launchTokenField;
    @LmlActor("sessionIdField")
    private VisTextField sessionIdField;
    @LmlActor("playerIdField")
    private VisTextField playerIdField;
    @LmlActor("roomSelectBox")
    private VisSelectBox<String> roomSelectBox;
    @LmlActor("tableSelectBox")
    private VisSelectBox<String> tableSelectBox;

    @LmlActor("whoAmIText")
    private VisTextField whoAmIText;

    @LmlActor("playerRatingText")
    private VisTextField playerRatingText;

    @LmlActor("playerIconText")
    private VisTextField playerIconText;

    @LmlActor("tableRoom")
    private VisLabel tableRoom;

    @LmlActor("tableNumber")
    private VisLabel tableNumber;

    @LmlActor("playerSeatNumber")
    private VisLabel playerSeatNumber;

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
            //sendJWTValue();
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
            String errorMsg = "error in destroy()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    @Override
    public void render(Stage stage, float delta) {
        try {
            getWsMessages();
            //Render
            renderTable();
            stage.act(delta);
            stage.draw();
        } catch (Exception e) {
            String errorMsg = "Error in render()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
            //throw new ReflectionException(e);
        }
    }

    private void getWsMessages() {
        wsMessageRouterService.pump(networkManager);
    }

    private void initiate() throws ReflectionException {
        networkManager = GameNetFactory.getManager();
        if (launchTokenField != null) {
            String launchToken = BootstrapConfig.getLaunchToken();
            logger.error("Bootstrap:launch={}", launchToken);
            launchTokenField.setText(launchToken);
        }

        if (apiTokenField != null) {
            String apiToken = BootstrapConfig.getApiToken();
            logger.error("Bootstrap:api={}", apiToken);
            apiTokenField.setText(apiToken);
        }

        wsMessageRouterService.on("GameStartedResponse", raw -> {
            logger.debug("GameStartedResponse raw={}", raw);
            // parse strongly typed if you want
        });

        wsMessageRouterService.on("GameStateTickResponse", raw -> {
            logger.debug("GameStateTickResponse raw={}", raw);
            // parse strongly typed if you want
        });
    }

    public String getLaunchTokenFieldValue() {
        if (launchTokenField != null) {
            return JWTUtil.trimToEmpty(launchTokenField.getText());
        } else {
            return "";
        }
    }

    public String getApiTokenFieldValue() {
        if (apiTokenField != null) {
            return JWTUtil.trimToEmpty(apiTokenField.getText());
        } else {
            return "";
        }
    }

    public void sessionIdChanged() {
        sessionService.setSessionId(JWTUtil.trimToNull(sessionIdField.getText()));
    }

    public void playerIdChanged() {
        sessionService.setPlayerId(JWTUtil.trimToNull(playerIdField.getText()));
    }

    @LmlAction("getRooms")
    public Array<String> getRooms() {
        return GdxArrays.newArray("Room1", "Room2", "Room3");
    }

    @LmlAction("loadTableDetails")
    public void loadTableDetails() {
        logger.enter("loadTableDetails");

        String apiTokenString = getApiTokenFieldValue();
        if (apiTokenString == null) {
            throw new GdxRuntimeException("api token cannot be null;");
        }
        sessionService.setApiToken(apiTokenString);

        String launchTokenString = getLaunchTokenFieldValue();
        if (launchTokenString == null) {
            throw new GdxRuntimeException("launch token cannot be null");
        }
        sessionService.setLaunchToken(launchTokenString);

        logger.error("Enter bootWithLaunchToken()");
        serverGameService.bootWithLaunchToken(
                launchTokenString,
                tableDetailsResponse -> {
                    // build game context / switch screen
                    //interfaceService.show(GameScreen.ID);
                },
                err -> sessionService.handleException(logger, err)
        );
        logger.error("Exit bootWithLaunchToken()");

        logger.exit("loadTableDetails");
    }

    private void renderTable() {
        whoAmIText.setText(sessionService.getCurrentUserName());
        playerRatingText.setText(sessionService.getRating() + "");
        playerIconText.setText(sessionService.getIcon() + "");
        tableRoom.setText(sessionService.getCurrentRoomName());
        tableNumber.setText(sessionService.getCurrentTableNumber());
        playerSeatNumber.setText(sessionService.getCurrentSeat());

        if (sessionService.getCurrentSeat() % 2 == 0) {
            uiArea1.sitPlayerDown(sessionService.getCurrentPlayer());
        } else {
            uiArea2.sitPlayerDown(sessionService.getCurrentPlayer());
        }
    }

    @LmlAction("startGame")
    public void startGame() throws ReflectionException {
        logger.enter("startGame");
        try {
            // Ensure you are connected first
            if (!networkManager.isConnected()) {
                logger.debug("Not connected"); // or networkManager.connect()
                logger.debug("connected={}", sessionService.connectToServer()); // or networkManager.connect()
            }

            WsEnvelope env = getWsEnvelope(GameStartRequest.class);
            networkManager.send(env);
            logger.exit("startGame");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private WsEnvelope getWsEnvelope(Class<? extends AbstractClientRequest> clazz) {
        logger.enter("getWsEnvelope");

        WsEnvelope env = null;
        if (clazz == GameStartRequest.class) {
            GameStartRequest req = new GameStartRequest();
            req.setClientId(sessionService.getClientId());
            req.setSessionId(sessionService.getSessionId());
            req.setPlayerId(sessionService.getPlayerId());
            req.setAuthToken(sessionService.getApiToken()); // NOT launch token
            env = new WsEnvelope(GameStartRequest.class.getSimpleName(), req);
        }
        logger.exit("getWsEnvelope", env);
        return env;
    }
}