package asg.games.yokel.client.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.preferences.PreferencesService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import asg.games.yipee.common.enums.ACCESS_TYPE;
import asg.games.yipee.common.game.PlayerAction;
import asg.games.yipee.common.net.wire.GameAuthTokenResponse;
import asg.games.yipee.libgdx.net.GdxTableDetailsResponse;
import asg.games.yipee.libgdx.objects.YipeeKeyMapGDX;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.libgdx.objects.YipeeSeatGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import asg.games.yokel.client.configuration.Configuration;
import asg.games.yokel.client.configuration.preferences.BootstrapConfig;
import asg.games.yokel.client.controller.dialog.ErrorController;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.game.ClientGameManager;
import asg.games.yokel.client.game.GameSeatActionPair;
import asg.games.yokel.client.managers.GameNetFactory;
import asg.games.yokel.client.managers.GameNetworkManager;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.PayloadUtil;
import asg.games.yokel.client.utils.YokelUtilities;
import lombok.Getter;
import lombok.Setter;


/**
 * Manages an authorized user's current session
 * Includes the client and communicates with the server
 *
 * @author Blakbro2k
 */
@Component
public class SessionService {
    long lastBlockDown = 0;
    private boolean downKeyPressed = false;
    private boolean isDebug = false;

    @Inject
    private InterfaceService interfaceService;
    @Inject
    private UserInterfaceService userInterfaceService;
    @Inject
    private SoundFXService soundFXService;
    @Inject
    private LoggerService loggerService;
    @Inject
    private PreferencesService preferencesService;
    @Inject
    private ServerGameService serverGameService;
    Log4LibGDXLogger logger;

    private final String CONNECT_MSG = "Connecting...";
    @Getter
    private GameNetworkManager networkManager;
    @Setter
    private String currentLoungeName;
    @Setter
    private String currentRoomName;
    @Setter
    @Getter
    private YipeeTableGDX currentTable;

    @Setter
    private int currentSeat;
    private String userName;
    private YipeePlayerGDX player;
    private final ObjectMap<String, ViewController> views = GdxMaps.newObjectMap();
    private final YipeeKeyMapGDX keyMap = new YipeeKeyMapGDX();
    private String currentErrorMessage;
    private boolean isWinner;
    private boolean isPartnered;
    private boolean connected;
    private boolean initialized;

    @Setter
    @Getter
/**
 * Browser-safe client identifier (GWT-compatible, not UUID).
 */
    private String clientId = null;

    @Setter
    @Getter
    private String apiToken = null;  // server-signed JWT or dev token

    @Setter
    @Getter
    private String launchToken = null;

    @Setter
    @Getter
    private String gameToken = null;

    @Setter
    @Getter
    private GameAuthTokenResponse gameAuth = null;

    @Setter
    @Getter
    private String currentTableId = null;

    @Setter
    @Getter
    private GdxTableDetailsResponse tableDetails = null;

    @Setter
    @Getter
    private String currentGameId = null;

    @Setter
    @Getter
    private String playerId = null;

    @Setter
    @Getter
    private int rating = -1;

    @Setter
    @Getter
    private int icon = -1;

    @Setter
    @Getter
    public String serverId;

    @Setter
    @Getter
    public String sessionId;

    @Setter
    @Getter
    public long serverTimestamp;

    @Setter
    @Getter
    public int tickRate;

    @Setter
    @Getter
    public String roomName;

    private static final String PREF_CLIENT_ID = "clientId";

    @Initiate
    public void initialize() throws InterruptedException {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        logger.setDebug();
        logger.enter("initialize");
        networkManager = GameNetFactory.getManager();
        applyBootstrapConfig();

        //connectToServer();
        //TODO: Create PHPSESSION token6
        //TODO: Create CSRF Token
        //TODO: Get host and port from configuration or preferences
        logger.exit("initialize");
    }

    @Destroy
    public void destroy() {
        logger.enter("destroy");
        closeClient();
        Disposables.disposeOf(currentTable);
        views.clear();
        logger.exit("destroy");
    }

    public String getGameRequestToken() {
        // Prefer game token once we have it
        if (!YokelUtilities.isEmpty(gameToken)) return gameToken;

        // Fallback during bootstrap / before whoami/auth completes
        if (!YokelUtilities.isEmpty(launchToken)) return launchToken;

        // Last resort: API token (lobby endpoints)
        return apiToken;
    }

    private void applyBootstrapConfig() {
        if (BootstrapConfig.isDebugMode()) {
            setDebug(true);
        }

        if (BootstrapConfig.getJwtToken() != null) {
            setApiToken(BootstrapConfig.getJwtToken());
        }

        if (BootstrapConfig.getApiToken() != null) {
            setApiToken(BootstrapConfig.getApiToken());
        }

        if (BootstrapConfig.getLaunchToken() != null) {
            setLaunchToken(BootstrapConfig.getLaunchToken());
        }

        if (BootstrapConfig.getClientId() != null) {
            setClientId(BootstrapConfig.getClientId());
        }

        if (BootstrapConfig.getSessionId() != null) {
            setSessionId(BootstrapConfig.getSessionId());
        }
    }

    public void applyGameAuth(GameAuthTokenResponse resp) {
        logger.enter("applyGameAuth");
        if (resp != null) {
            logger.debug("response={}", resp);
            setGameAuth(resp);

            // Important: this becomes your “request auth token” going forward
            setGameToken(resp.getGameToken()); // or resp.jwt depending on your field

            setPlayerId(resp.playerId);
            setClientId(resp.clientId);

            setSessionId(resp.getSessionId());  // you also store sessionId

            setCurrentGameId(resp.getGameId());
            setCurrentTableId(resp.getTableId());
            setCurrentSeat(resp.getSeatIndex());
            setCurrentLoungeName(resp.getLoungeName());
            setCurrentRoomName(resp.getRoomName());

            setServerId(resp.getServerId());
            setServerTimestamp(resp.getServerTimestamp());
            setTickRate(resp.getTickRate());

            setRating(resp.rating);
            setIcon(resp.icon);

            // update player object
            YipeePlayerGDX p = getCurrentPlayer();
            setCurrentUserName(resp.name);
            if (p == null) p = new YipeePlayerGDX();
            p.setId(resp.playerId);
            p.setName(resp.name);
            p.setRating(resp.rating);
            p.setIcon(resp.icon);
            setCurrentPlayer(p);
        }
        logger.exit("applyGameAuth");
    }
    private String ensureClientId() {
        logger.enter("ensureClientId");
        Preferences prefs = preferencesService.getPreferences(Configuration.PREFERENCES);
        if (prefs != null) {
            clientId = prefs.getString(PREF_CLIENT_ID);
        }

        if (clientId == null || clientId.isEmpty()) {
            clientId = generateClientId();
            if (prefs != null) {
                prefs.putString(PREF_CLIENT_ID, clientId);
                prefs.flush();
            }
        }

        logger.exit("ensureClientId={}", clientId);
        return clientId;
    }
    public void closeClient() {
        networkManager.dispose();
    }

    /**
     * Generates a lightweight client identifier that is safe for GWT.
     *
     * <p>This replaces {@code java.util.UUID}, which is not available in the GWT
     * compilation environment.
     *
     * <p>This ID is sufficient for client/session identification and is not intended
     * for cryptographic use.
     *
     * @return a reasonably unique identifier string
     */
    private String generateClientId() {
        long now = System.currentTimeMillis();
        int rand = (int) (Math.random() * 1_000_000);
        return "client-" + now + "-" + rand;
    }

    public int getCurrentSeat() {
        //logger.enter("getCurrentSeat");
        int returningSeat = -1;
        YipeePlayerGDX currentPlayer = getCurrentPlayer();
        YipeeTableGDX table = getCurrentTable();

        if (currentPlayer != null && table != null) {
            for (YipeeSeatGDX seat : table.getSeats()) {
                if (seat != null && seat.isOccupied() && seat.getSeatedPlayer().getName().equals(currentPlayer.getName())) {
                    returningSeat = seat.getSeatNumber();
                    break;
                }
            }
        }
        //logger.exit("getCurrentSeat={}", returningSeat);
        return returningSeat;
    }

    public boolean connectToServer() throws InterruptedException {
        logger.enter("connectToServer");
        if (!networkManager.isConnected()) {
            logger.debug("Not initialized");
            networkManager.registerPackets();
            clientId = ensureClientId();
            networkManager.setRequestToken(apiToken);
            connected = networkManager.connect();
            initialized = true;
        }
        logger.exit("connectToServer", connected);
        return connected;
    }

    public void registerUser() {
        if (!connected) {
            networkManager.registerUser(apiToken, player, clientId, sessionId);
        }
    }

    public boolean disconnectToServer() throws InterruptedException {
        logger.enter("disconnectToServer");
        return networkManager.disconnect();
    }

    public boolean isConnected() throws InterruptedException {
        logger.enter("isConnected");
        return networkManager.isConnected();
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    public boolean isPartnered() {
        return isPartnered;
    }

    public void setPartnered(boolean isPartnered) {
        this.isPartnered = isPartnered;
    }
    /*
    public Array<YokelLounge> getAllLounges() throws InterruptedException {
        client.requestLounges();
        client.waitForOneRequest();
        return PayloadUtil.getAllLoungesRequest(client.getNextRequest().getPayload());
    }*/

    public Array<YipeePlayerGDX> getAllPlayers() throws InterruptedException {
        logger.enter("getAllPlayers");
        //client.requestPlayers();
        //client.waitForOneRequest();
        //String[] payload = client.getNextRequest().getPayload();
        String[] payload = new String[]{""};
        logger.exit("getAllPlayers", payload);
        return PayloadUtil.getAllRegisteredPlayersRequest(payload);
    }

    public void requestTableSitRequest(String tableNumber, int seatNumber) throws InterruptedException {
        //client.requestTableSit(player, currentLoungeName, currentRoomName, tableNumber, seatNumber);
        //client.waitForOneRequest();
    }

    public void asyncPlayerAllRequest() throws InterruptedException {
        //client.requestPlayers();
    }

    public Array<YipeePlayerGDX> asyncGetPlayerAllRequest() {
        //return PayloadUtil.getAllRegisteredPlayersRequest(client.getNextRequest(ServerRequest.REQUEST_ALL_REGISTERED_PLAYERS));
        return PayloadUtil.getAllRegisteredPlayersRequest(new String[]{""});
    }

    public void asyncTableAllRequest() throws InterruptedException {
        //client.requestTables(currentLoungeName, currentRoomName);
    }

    public void asyncCreateGameRequest(ACCESS_TYPE accessType, boolean isRated) throws InterruptedException {
        //client.requestCreateGame(currentLoungeName, currentRoomName, accessType, isRated);
    }

    public void asyncTableSitRequest(String tableNumber, int seatNumber) throws InterruptedException {
        //client.requestTableSit(player, currentLoungeName, currentRoomName, tableNumber, seatNumber);
    }

    public void asyncTableStandRequest(String tableNumber, int seatNumber) throws InterruptedException {
        //client.requestTableStand(currentLoungeName, currentRoomName, tableNumber, seatNumber);
    }

    public Array<YipeeTableGDX> asyncGetTableAllRequest() {
        //TODO: Save tables states
        //return PayloadUtil.getAllTablesRequest(client.getNextRequest(ServerRequest.REQUEST_TABLE_INFO));new String[]{""}
        //return PayloadUtil.getAllTablesRequest(new String[]{""});
        return null;
    }

    private void asyncMoveRightRequest() throws InterruptedException {
        //client.requestMoveRight(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    private void asyncMoveLeftRequest() throws InterruptedException {
        //client.requestMoveLeft(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    private void asyncCycleDownRequest() throws InterruptedException {
        //client.requestCycleDown(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    private void asyncCycleUpRequest() throws InterruptedException {
        //client.requestCycleUp(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    private void asyncMoveStartDownRequest() throws InterruptedException {
        //client.requestMoveStartDown(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    private void asyncMoveStopDownRequest() throws InterruptedException {
        //client.requestMoveStopDown(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    private void asyncTargetAttackRequest(int currentSeat, int targetSeat) throws InterruptedException {
        //client.requestTargetAttack(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat, targetSeat);
    }

    private void asyncRandomAttackRequest(int currentSeat) throws InterruptedException {
        //client.requestRandomAttack(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    public void asyncGameManagerFromServerRequest() throws InterruptedException {
        //client.requestGameManager(currentLoungeName, currentRoomName, getCurrentTableNumber(), currentSeat);
    }

    public ClientGameManager asyncGetGameManagerFromServerRequest() {
        //return PayloadUtil.getGameManagerRequest(client.getNextRequest(ServerRequest.REQUEST_TABLE_GAME_MANAGER));new String[]{""}
        // return PayloadUtil.getGameManagerRequest(new String[]{""});
        return null;
    }

    public Array<String> toPlayerNames(Array<YipeePlayerGDX> players) {
        Array<String> playerNames = GdxArrays.newArray();
        if(players != null){
            for (YipeePlayerGDX player : YokelUtilities.safeIterable(players)) {
                if(player != null){
                    playerNames.add(player.getName());
                }
            }
        }
        return playerNames;
    }

    public ViewController getView(String viewId){
        if(views.containsKey(viewId)){
            return views.get(viewId);
        } else {
            for(ViewController ctrl : interfaceService.getControllers()){
                if(ctrl != null && YokelUtilities.equalsIgnoreCase(viewId, ctrl.getViewId())){
                    views.put(viewId, ctrl);
                    return ctrl;
                }
            }
            //If view does not exist, return current view
            return interfaceService.getCurrentController();
        }
    }

    public void setCurrentUserName(String userName){
        this.userName = userName;
    }

    public String getCurrentUserName(){
        return userName;
    }

    public String getCurrentTableNumber(){
        currentTable = getCurrentTable();
        if(currentTable != null){
            return currentTable.getName();
        } else {
            return "";
        }
    }

    public String getCurrentRoomName(){
        return currentRoomName;
    }

    public void setCurrentError(Throwable cause, String message) {
        currentErrorMessage = message;
    }

    public String getCurrentError() {
        return currentErrorMessage;
    }

    public void setCurrentPlayer(YipeePlayerGDX yokelPlayer) {
        this.player = yokelPlayer;
        this.userName = yokelPlayer.getName();
    }

    public boolean isCurrentPlayer(YipeePlayerGDX player) {
        return player != null && player.equals(getCurrentPlayer());
    }

    public YipeePlayerGDX getCurrentPlayer() {
        return player;
    }

    public void handlePlayerInput(ClientGameManager game) {
        logger.enter("handleLocalPlayerInput");
        int currentSeat = getCurrentSeat();
        logger.debug("currentSeat={}", currentSeat);

        if(game == null || currentSeat < 0) return;

        //TODO: Remove, moves test player's key to the right
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.O_MEDUSA, currentSeat, null), 10));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.O_MIDAS, currentSeat, null), 10));
        }

        if (Gdx.input.isKeyJustPressed(keyMap.getRightKey())) {
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.P_MOVE_RIGHT, currentSeat, null), 10));
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getLeftKey())) {
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.P_MOVE_LEFT, currentSeat, null), 10));
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getCycleDownKey())) {
            soundFXService.playCycleClickSound();
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.P_CYCLE_DOWN, currentSeat, null), 10));
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getCycleUpKey())) {
            soundFXService.playCycleClickSound();
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.P_CYCLE_UP, currentSeat, null), 10));
        }
        if (Gdx.input.isKeyPressed(keyMap.getDownKey())) {
            if(!downKeyPressed){
                downKeyPressed = true;
            }
            soundFXService.playBlockDownSound();
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.P_MOVE_DOWN_START, currentSeat, null), 10));
        }
        if (!Gdx.input.isKeyPressed(keyMap.getDownKey())) {
            downKeyPressed = false;
            game.addAction(new GameSeatActionPair(currentSeat, new PlayerAction(currentSeat, PlayerAction.ActionType.P_MOVE_DOWN_END, currentSeat, null), 10));
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getRandomAttackKey())) {
            //game.applyLocalPlayerAction(new PlayerAction(currentSeat, PlayerAction.ActionType.P_MOVE_LEFT, currentSeat, 10, null));
        }
        /*
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget1())) {
            game.handleTargetAttack(currentSeat,1);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget2())) {
            game.handleTargetAttack(currentSeat,2);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget3())) {
            game.handleTargetAttack(currentSeat,3);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget4())) {
            game.handleTargetAttack(currentSeat,4);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget5())) {
            game.handleTargetAttack(currentSeat,5);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget6())) {
            game.handleTargetAttack(currentSeat,6);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget7())) {
            game.handleTargetAttack(currentSeat,7);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget8())) {
            game.handleTargetAttack(currentSeat,8);
        }*/
        logger.exit("handleLocalPlayerInput");
    }

    public void handlePlayerInputToServer() throws InterruptedException {
        logger.enter("handlePlayerInput");

        int currentSeat = getCurrentSeat();
        logger.debug("currentSeat={}", currentSeat);

        if (Gdx.input.isKeyJustPressed(keyMap.getRightKey())) {
            asyncMoveRightRequest();
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getLeftKey())) {
            asyncMoveLeftRequest();
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getCycleDownKey())) {
            asyncCycleDownRequest();
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getCycleUpKey())) {
            asyncCycleUpRequest();
        }
        if (Gdx.input.isKeyPressed(keyMap.getDownKey())) {
            asyncMoveStartDownRequest();
        }
        if (!Gdx.input.isKeyPressed(keyMap.getDownKey())) {
            asyncMoveStopDownRequest();
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getRandomAttackKey())) {
            asyncRandomAttackRequest(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget1())) {
            asyncTargetAttackRequest(currentSeat,1);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget2())) {
            asyncTargetAttackRequest(currentSeat,2);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget3())) {
            asyncTargetAttackRequest(currentSeat,3);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget4())) {
            asyncTargetAttackRequest(currentSeat,4);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget5())) {
            asyncTargetAttackRequest(currentSeat,5);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget6())) {
            asyncTargetAttackRequest(currentSeat,6);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget7())) {
            asyncTargetAttackRequest(currentSeat,7);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getTarget8())) {
            asyncTargetAttackRequest(currentSeat,8);
        }
        logger.exit("handlePlayerInput");
    }

    public void handleException(Logger logger, Throwable throwable) {
        if(logger != null) {
            logger.error(throwable, throwable.getMessage());
            throwable.printStackTrace();
            showError(logger, throwable);
        } else {
            showError(throwable);
        }
    }


    public void showError(Logger logger, Throwable throwable) {
        if(throwable == null) return;
        String errorMsg = throwable.getMessage();
        if(logger != null) logger.error(throwable, errorMsg);
        setCurrentError(throwable.getCause(), errorMsg);
        interfaceService.showDialog(ErrorController.class);
    }

    public void showError(Throwable throwable) {
        showError(logger, throwable);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
        logger.error("debug={}", debug);
    }

    public void handleGameAuthTokenResponse() {
        logger.enter("handleGameAuthTokenResponse");
        logger.debug("resp={}", getGameAuth());

        if (getGameAuth() == null) {
            if (serverGameService.getPendingBootErr() != null) {
                ServerGameService.onErrOnRenderThread(
                        serverGameService.getPendingBootErr(),
                        new GdxRuntimeException("GameAuthTokenResponse was null")
                );
            }
            return;
        }

        serverGameService.getTableDetails(
                td -> {
                    if (serverGameService.getPendingBootOk() != null) {
                        ServerGameService.onOkOnRenderThread(serverGameService.getPendingBootOk(), td);
                    }
                    serverGameService.setPendingBootErr(null);
                    serverGameService.setPendingBootOk(null);
                },
                err -> {
                    logger.error(err, "getTableDetails failed");

                    if (serverGameService.getPendingBootErr() != null) {
                        ServerGameService.onErrOnRenderThread(
                                serverGameService.getPendingBootErr(),
                                err
                        );
                    }

                    serverGameService.setPendingBootErr(null);
                    serverGameService.setPendingBootOk(null);
                }
        );

        logger.exit("handleGameAuthTokenResponse");
    }
}