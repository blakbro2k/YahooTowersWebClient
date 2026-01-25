package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
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

import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.net.packets.GameStartRequest;
import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.managers.GameNetFactory;
import asg.games.yokel.client.managers.GameNetworkManager;
import asg.games.yokel.client.net.WsEnvelope;
import asg.games.yokel.client.service.ServerGameServices;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GamePlayerBoard;
import asg.games.yokel.client.utils.JWTUtil;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.YokelUtilities;

@View(id = GlobalConstants.UI_DEBUG_CLIENT_VIEW, value = GlobalConstants.UI_TEST_CLIENT_VIEW_PATH)
public class ClientViewTestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    @Inject
    private UserInterfaceService uiService;
    @Inject
    private SessionService sessionService;
    @Inject
    private ServerGameServices serverGameServices;
    @Inject
    private InterfaceService interfaceService;
    @Inject
    private MusicService musicService;
    @Inject
    private LoadingController assetController;
    @Inject
    private LoggerService loggerService;

    private Log4LibGDXLogger logger;

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
            sendJWTValue();
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
            renderTable();
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
            return JWTUtil.trimToEmpty(accessTokenField.getText());
        } else {
            return "";
        }
    }

    public void sessionIdChanged() {
        sessionService.setSessionKey(JWTUtil.trimToNull(sessionIdField.getText()));
    }

    public void playerIdChanged() {
        sessionService.setPlayerId(JWTUtil.trimToNull(playerIdField.getText()));
    }

    @LmlAction("getRooms")
    public Array<String> getRooms() {
        return GdxArrays.newArray("Room1", "Room2", "Room3");
    }

    /**
     * Same algorithm as yipee-lobby.html
     */
    @LmlAction("sendJWTValue")
    public void sendJWTValue() {
        YipeePlayerGDX player = sessionService.getCurrentPlayer();
        String playerId = sessionService.getPlayerId();
        String username = sessionService.getCurrentUserName(); // or pull from another field
        int rating = sessionService.getRating();          // if you track it
        int icon = sessionService.getIcon();            // if you track it

        // If you don't have username/rating/icon in this debug screen yet:
        if (username == null) username = "debug";

        String jwt = JWTUtil.createMockJwt(playerId, username, rating + "", icon + "");
        System.out.println("JWT: " + jwt);
        sessionService.setAuthToken(jwt);

        // optionally show it somewhere or log it
        Gdx.app.log("JWT", "Generated dev JWT for playerId=" + playerId);
    }

    @LmlAction("loadTableDetails")
    public void loadTableDetails() {
        String accessTokenString = getAccessTokenFieldValue();

        serverGameServices.bootWithLaunchToken(
                accessTokenString,
                tableDetailsResponse -> {
                    // build game context / switch screen
                    //interfaceService.show(GameScreen.ID);
                },
                err -> sessionService.handleException(logger, err)
        );
    }

    private void renderTable() {
/*
        System.out.println("whoAmIText: " + sessionService.getCurrentUserName());
        System.out.println("playerRatingText: " + sessionService.getRating());
        System.out.println("playerIconText: " + sessionService.getIcon());
        System.out.println("tableRoom: " + sessionService.getCurrentTableNumber());
        System.out.println("tableNumber: " + sessionService.getCurrentTableNumber());
        System.out.println("playerSeatNumber: " + sessionService.getCurrentSeat());
*/
        whoAmIText.setText(sessionService.getCurrentUserName());
        playerRatingText.setText(sessionService.getRating() + "");
        playerIconText.setText(sessionService.getIcon() + "");
        tableRoom.setText(sessionService.getCurrentRoomName());
        tableNumber.setText(sessionService.getCurrentTableNumber());
        playerSeatNumber.setText(sessionService.getCurrentSeat());
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

            WsEnvelope env = new WsEnvelope("GameStartRequest", YokelUtilities.getJsonString(GameStartRequest.class, req));
            networkManager.send(env);
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }
}