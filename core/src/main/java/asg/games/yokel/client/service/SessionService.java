package asg.games.yokel.client.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.websocket.data.WebSocketException;

import asg.games.yokel.client.controller.dialog.ErrorController;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.managers.GameClientManager;
import asg.games.yokel.client.managers.KryoClientManager;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.managers.GameManager;
import asg.games.yokel.objects.YokelKeyMap;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.objects.YokelTable;
import asg.games.yokel.utils.PayloadUtil;
import asg.games.yokel.utils.YokelUtilities;


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
    Log4LibGDXLogger logger;

    private final String CONNECT_MSG = "Connecting...";
    private GameClientManager client;
    private String currentLoungeName;
    private String currentRoomName;
    private YokelTable currentTable;
    private int currentSeat;
    private String userName;
    private YokelPlayer player;
    private final ObjectMap<String, ViewController> views = GdxMaps.newObjectMap();
    private final YokelKeyMap keyMap = new YokelKeyMap();
    private String currentErrorMessage;

    @Initiate
    public void initialize() throws WebSocketException, InterruptedException {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        logger.setDebug();
        logger.enter("initialize");
        client = new KryoClientManager(5000, "localhost", 8081, 55000);

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

    public void closeClient() {
        client.dispose();
    }

    public boolean connectToServer() throws InterruptedException {
        logger.enter("connectToServer");
        return client.connect();
    }

    public boolean disconnectToServer() throws InterruptedException {
        logger.enter("disconnectToServer");
        return client.disconnect();
    }

    public boolean isConnected() throws InterruptedException {
        logger.enter("isConnected");
        return client.isConnected();
    }

    /*
    public Array<YokelLounge> getAllLounges() throws InterruptedException {
        client.requestLounges();
        client.waitForOneRequest();
        return PayloadUtil.getAllLoungesRequest(client.getNextRequest().getPayload());
    }*/

    public Array<YokelPlayer> getAllPlayers() throws InterruptedException {
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

    public Array<YokelPlayer> asyncGetPlayerAllRequest() {
        //return PayloadUtil.getAllRegisteredPlayersRequest(client.getNextRequest(ServerRequest.REQUEST_ALL_REGISTERED_PLAYERS));
        return PayloadUtil.getAllRegisteredPlayersRequest(new String[]{""});
    }

    public void asyncTableAllRequest() throws InterruptedException {
        //client.requestTables(currentLoungeName, currentRoomName);
    }

    public void asyncCreateGameRequest(YokelTable.ACCESS_TYPE accessType, boolean isRated) throws InterruptedException {
        //client.requestCreateGame(currentLoungeName, currentRoomName, accessType, isRated);
    }

    public void asyncTableSitRequest(String tableNumber, int seatNumber) throws InterruptedException {
        //client.requestTableSit(player, currentLoungeName, currentRoomName, tableNumber, seatNumber);
    }

    public void asyncTableStandRequest(String tableNumber, int seatNumber) throws InterruptedException {
        //client.requestTableStand(currentLoungeName, currentRoomName, tableNumber, seatNumber);
    }

    public Array<YokelTable> asyncGetTableAllRequest() {
        //TODO: Save tables states
        //return PayloadUtil.getAllTablesRequest(client.getNextRequest(ServerRequest.REQUEST_TABLE_INFO));new String[]{""}
        return PayloadUtil.getAllTablesRequest(new String[]{""});
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

    public GameManager asyncGetGameManagerFromServerRequest() {
        //return PayloadUtil.getGameManagerRequest(client.getNextRequest(ServerRequest.REQUEST_TABLE_GAME_MANAGER));new String[]{""}
        return PayloadUtil.getGameManagerRequest(new String[]{""});
    }

    public Array<String> toPlayerNames(Array<YokelPlayer> players) {
        Array<String> playerNames = GdxArrays.newArray();
        if(players != null){
            for(YokelPlayer player : YokelUtilities.safeIterable(players)){
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

    public void setCurrentSeat(int currentSeat){
        this.currentSeat = currentSeat;
    }

    public int getCurrentSeat(){
        return currentSeat;
    }

    public void setCurrentTable(YokelTable currentTable){
        this.currentTable = currentTable;
    }

    public YokelTable getCurrentTable(){
        return currentTable;
    }

    public String getCurrentTableNumber(){
        currentTable = getCurrentTable();
        if(currentTable != null){
            return currentTable.getName();
        } else {
            return "";
        }
    }

    public void setCurrentRoomName(String currentRoomName){
        this.currentRoomName = currentRoomName;
    }

    public String getCurrentRoomName(){
        return currentRoomName;
    }

    public void setCurrentLoungeName(String currentLoungeName){
        this.currentLoungeName = currentLoungeName;
    }

    public String getCurrentLoungeName(){
        return currentLoungeName;
    }

    public void setCurrentError(Throwable cause, String message) {
        currentErrorMessage = message;
    }

    public String getCurrentError() {
        return currentErrorMessage;
    }

    public void setCurrentPlayer(YokelPlayer yokelPlayer) {
        this.player = yokelPlayer;
    }

    public boolean isCurrentPlayer(YokelPlayer player) {
        return player != null && player.equals(getCurrentPlayer());
    }

    public YokelPlayer getCurrentPlayer() {
        return player;
    }

    public void handlePlayerSimulatedInput(GameManager game){
        logger.enter("handleLocalPlayerInput");
        int currentSeat = getCurrentSeat();
        logger.debug("currentSeat={}", currentSeat);

        if(game == null || currentSeat < 0) return;

        //TODO: Remove, moves test player's key to the right
        game.handleMoveRight(7);
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            game.testMedusa(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            game.testMidas(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            game.showGameBoard(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            game.testGameBoard(currentSeat);
        }

        if (Gdx.input.isKeyJustPressed(keyMap.getRightKey())) {
            game.handleMoveRight(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getLeftKey())) {
            game.handleMoveLeft(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getCycleDownKey())) {
            soundFXService.playCycleClickSound();
            game.handleCycleDown(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getCycleUpKey())) {
            soundFXService.playCycleClickSound();
            game.handleCycleUp(currentSeat);
        }
        if (Gdx.input.isKeyPressed(keyMap.getDownKey())) {
            if(!downKeyPressed){
                downKeyPressed = true;
            }
            soundFXService.playBlockDownSound();
            game.handleStartMoveDown(currentSeat);
        }
        if (!Gdx.input.isKeyPressed(keyMap.getDownKey())) {
            downKeyPressed = false;
            game.handleStopMoveDown(currentSeat);
        }
        if (Gdx.input.isKeyJustPressed(keyMap.getRandomAttackKey())) {
            game.handleRandomAttack(currentSeat);
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
    }
}