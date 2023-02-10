package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;

import java.util.Iterator;
import java.util.Vector;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBoard;
import asg.games.yokel.client.ui.actors.GameClock;
import asg.games.yokel.managers.GameManager;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.objects.YokelSeat;
import asg.games.yokel.objects.YokelTable;
import asg.games.yokel.utils.YokelUtilities;

@View(id = GlobalConstants.UI_CLIENT_VIEW, value = GlobalConstants.UI_CLIENT_VIEW_PATH)
public class ClientViewController {//extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    // Getting a utility logger:

    @Inject private UserInterfaceService uiService;
    //@Inject private SessionService sessionService;
    @Inject private LoggerService loggerService;
    @Inject private InterfaceService interfaceService;
    @Inject private MusicService musicService;
    @Inject private LoadingController assetLoader;

    @LmlActor("gameClock") private GameClock gameClock;
    @LmlActor("1:area") private GameBoard uiArea1;
    @LmlActor("2:area") private GameBoard uiArea2;
    @LmlActor("3:area") private GameBoard uiArea3;
    @LmlActor("4:area") private GameBoard uiArea4;
    @LmlActor("5:area") private GameBoard uiArea5;
    @LmlActor("6:area") private GameBoard uiArea6;
    @LmlActor("7:area") private GameBoard uiArea7;
    @LmlActor("8:area") private GameBoard uiArea8;

    private boolean isGameOver, isYahooActive, isUsingServer,
            isMenacingPlaying, nextGameDialog, attemptGameStart, isGameReady = false;
    private long nextGame = 0;
    private GameBoard[] uiAreas;
    private YokelSeat[] order = new YokelSeat[8];
    private boolean[] isYahooPlaying = new boolean[8];
    private boolean[] isAlive = new boolean[8];
    private GameManager simulatedGame;
    private boolean yahooPlayed;
    private boolean isBrokenPlaying;
    private float yahTimer, brokenCellTimer;

   // @Override
   // public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        //Log4LibGDXLoggerService.INSTANCE.setActiveLogger(this.getClass(), false);
        /*
        try {
            sessionService.asyncGameManagerFromServerRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
            sessionService.showError(e);
        }
        //logger.enter("initialize");
        isUsingServer = false;

        //UIManager.UIManagerUserConfiguration config = new UIManager.UIManagerUserConfiguration();

        if(!isUsingServer){
            //UI Configuration manager needs to handle these.
            YokelPlayer player1 = new YokelPlayer("enboateng");
            YokelPlayer player2 = new YokelPlayer("lholtham", 1400, 1);
            YokelPlayer player3 = new YokelPlayer("rmeyers", 1700, 1);

            //setTable
            YokelTable table = new YokelTable("Table#1", 1);
            //config.setTableNumber(1);
            //int currentSeatNumber = 3;

            //this needs to be set outside of the UI manager
            //sessionService.setCurrentPlayer(player1);
            //sessionService.setCurrentLoungeName("Social");
            //sessionService.setCurrentRoomName("Eiffel Tower");
            //sessionService.setCurrentSeat(-1);
            //sessionService.setCurrentTable(table);
            //config.updateConfig(sessionService);
            //config.setSeat(5, player2);
            //config.setCurrentPlayer(sessionService.getCurrentPlayer());
            //config.setCurrentSeat(sessionService.getCurrentSeat());

            setSeat(7, player3, table);
            setSeat(1, player2, table);
            simulatedGame = new GameManager(table);
        } else {
            //TODO: Fetch table state from server
            isUsingServer = true;
        }

        uiAreas = new GameBoard[]{uiArea1, uiArea2, uiArea3, uiArea4, uiArea5, uiArea6, uiArea7, uiArea8};
        //setUpDefaultSeats(sessionService.getCurrentTable());
        setUpJoinButtons(uiAreas);
        //uiManager = new UIManager(uiAreas, isUsingServer, config);
        //logger.exit("initialize");
    }

    public void setSeat(int seatNumber, YokelPlayer player, YokelTable table){
        //logger.error("setting player {} @ seat[{}]", player, seatNumber);
        if(table != null){
            if(seatNumber < 0 || seatNumber > YokelTable.MAX_SEATS) return;
            YokelSeat seat = table.getSeat(seatNumber);
            if(seat != null) {
                if(player == null) {
                    seat.standUp();
                } else {
                    seat.sitDown(player);
                    seat.setSeatReady(true);
                }
            }
        }
    }

    @Override
    public void destroy(ViewController viewController) {
        //logger.debug("destroying viewID: {}", GlobalConstants.UI_CLIENT_VIEW);
        Disposables.disposeOf(simulatedGame);

        for(YokelSeat seat : order){
            if(seat != null){
                seat.dispose();
            }
        }
        //logger.debug("files={}", YokelUtilities.getFiles("U:\\YokelTowersMVC\\assets\\music"));
    }

    @Override
    public void render(Stage stage, float delta) {
        //logger.enter("render");
        try{
            //Fetch GameManager from Server
            //fetch game state from server
            GameManager game = fetchGameManagerFromServer(delta);

            //Check if game is ready to start
            checkIsGameReady(game);

            //Check if Count down is starting
            checkGameStart(game);

            //Handle Player input
            handlePlayerInput();

            //Update GameState
            updateGameState(game, delta, stage);

            //Update UI for players before the game
            if(!isGameRunning(game)){
                updateGameBoardPreStart();
            }

            //Update game timers
            updateTimers();

            //If Game Over, show it
            showGameOver(stage, game);

            stage.act(delta);
            stage.draw();
        } catch (Exception e){
            //sessionService.handleException(logger, e);
        }
        //logger.exit("render");
    }

    private void updateTimers() {
        if(yahTimer > -1){
            --yahTimer;
        }

        if(brokenCellTimer > -1){
            --brokenCellTimer;
        }
        //logger.error("###yahTimer={}", yahTimer);
        //logger.error("###brokenCellTimer={}", brokenCellTimer);
    }

    private void setUpDefaultSeats(YokelTable table){
        if(table != null){
            for(int i = 0; i < YokelTable.MAX_SEATS; i++){
                order[i] = table.getSeat(i);
            }
        }
    }

    private void updateGameBoardPreStart() {
        YokelTable table = sessionService.getCurrentTable();
        int currentPlayerSeat = sessionService.getCurrentSeat();
        int currentPartnerSeat = getPlayerPartnerSeatNum(currentPlayerSeat);

        if(table != null) {
            Array<YokelSeat> seats = table.getSeats();
            if(currentPlayerSeat > -1){
                int playerOneSeat = currentPlayerSeat % 2;
                int playerPartnerSeat = currentPartnerSeat % 2;
                order[playerOneSeat] = table.getSeat(currentPlayerSeat);
                order[playerPartnerSeat] = table.getSeat(currentPartnerSeat);

                //Set up rest of active players+
                Array<Integer> remaining = GdxArrays.newArray();
                for(int i = 2; i < YokelTable.MAX_SEATS; i++) {
                    remaining.add(i);
                }

                Iterator<Integer> iterator = YokelUtilities.getArrayIterator(remaining);

                for(int i = 0; i < YokelTable.MAX_SEATS; i++){
                    if(i != currentPlayerSeat && i != currentPartnerSeat){
                        if(iterator.hasNext()){
                            order[iterator.next()] = seats.get(i);
                        }
                    }
                }
            } else {
                setUpDefaultSeats(table);
            }

            for(int i = 0; i < order.length; i++){
                YokelSeat seat = order[i];
                if(seat != null){
                    YokelPlayer player = seat.getSeatedPlayer();
                    String playerJsonObj = null;

                    int seatNumber = seat.getSeatNumber();

                    if(player != null){
                        playerJsonObj = player.toString();
                        isAlive[seatNumber] = true;
                    } else {
                        isAlive[seatNumber] = false;
                    }

                    GameBoard area = uiAreas[i];

                    if(area != null){
                        area.setData(playerJsonObj);
                        area.setPlayerView(sessionService.isCurrentPlayer(player));
                        area.setActive(sessionService.isCurrentPlayer(player));
                    }
                }
            }
        }
    }

    private int getPlayerPartnerSeatNum(int seatNumber) {
        if (seatNumber % 2 == 0) {
            return seatNumber + 1;
        } else {
            return seatNumber - 1;
        }
    }

    //Needs to update the simulated game with the server state.
    private void updateGameState(GameManager game, float delta, Stage stage) throws GdxRuntimeException {
        if(logger.isDebugOn()){
            ObjectMap<String, Object> map = GdxMaps.newObjectMap();
            map.put("game", game);
            map.put("delta", delta);
            logger.enter("updateGameState", map);
        }

        //Update current game
        if(isGameRunning(game)){
            //TODO: game state will not need to be updated anymore, it will be updated from server
            game.update(delta);

            for(int board = 0; board < order.length; board++){
                //Get Game seat
                int gameSeat = order[board].getSeatNumber();
                boolean isPlayerDead = game.isPlayerDead(gameSeat);

                if(isPlayerDead && isAlive[gameSeat]){
                    isAlive[gameSeat] = false;
                    killPlayer(uiAreas[board]);
                } else {
                    updateUiBoard(uiAreas[board], game.getGameBoard(gameSeat));
                }
            }
        }
        logger.exit("updateGameState");
    }

    private void updateUiBoard(GameBoard uiArea, YokelGameBoard gameBoard) {
        if(uiArea != null){
            uiArea.update(gameBoard);
            int yahooDuration = gameBoard.fetchYahooDuration();
            int gameBoardSeat = Integer.parseInt(gameBoard.getName());

            if(isPlayerBoard(gameBoardSeat)){
                if(gameBoard.hasPieceSet()){
                    uiService.getSoundFXFactory().playPiecePlacedSound();
                }
                Vector<YokelBlock> brokenCells = gameBoard.getBrokenCells();
                System.err.println(gameBoard.getName() + "]#$$getBrokenCells=" + gameBoard.getBrokenCells());
                if(!YokelUtilities.isEmpty(brokenCells)){
                    //Play broken cell sound
                    uiService.getSoundFXFactory().playBrokenCell();
                    System.err.println("!!!brokenCells=" + brokenCells);
                }
            }

            isYahooActive = yahooDuration > 0;
            uiArea.setYahooDuration(isYahooActive);
            if(isYahooActive) {
                if(isPlayerBoard(gameBoardSeat)){
                    uiService.getSoundFXFactory().startYahooFanfare(gameBoardSeat);
                } else {
                    uiService.getSoundFXFactory().playYahooSound();
                }
            } else {
                uiService.getSoundFXFactory().stopYahooFanfare(gameBoardSeat);
            }
        }
    }

    private static PlaySoundAction playSoundAction(Sound sound) {
        PlaySoundAction action = Actions.action(PlaySoundAction.class);
        action.setSound(sound);
        return action;
    }

    private boolean isPlayerBoard(int gameBoardInt) {
        return sessionService.getCurrentSeat() == gameBoardInt;
    }

    private void killPlayer(GameBoard uiArea) {
        if(uiArea != null){
            uiService.getSoundFXFactory().playBoardDeathSound();
            uiArea.killPlayer();
        }
    }

    private boolean isGameRunning(GameManager game){
        return game != null && game.isRunning();
    }

    private void updateState(GameManager game) {
        //TODO: Need a collection of GameManagers and update current one based off
        if(game != null){
            //serverGameManger = game;
        }
    }

    private void checkIsGameReady(GameManager game) {
        if(game != null){
            isGameReady = game.isGameReady();
        }
    }

    private void checkGameStart(GameManager game) {
        if(gameClock == null) return;
        logger.error("isGameReady={}", isGameReady);

        if(isGameReady){
            if(attemptGameStart){
                if(!nextGameDialog) {
                    logger.debug("Showing next Game Dialog");
                    nextGameDialog = true;
                    interfaceService.showDialog(NextGameController.class);
                    nextGame = TimeUtils.millis();
                    hideAllJoinButtons();
                }

                if(getElapsedSeconds() > NextGameController.NEXT_GAME_SECONDS){
                    interfaceService.destroyDialog(NextGameController.class);
                    attemptGameStart = false;
                    startGame();
                }
            }
        }
    }

    private void hideAllJoinButtons() {
        for(GameBoard area : uiAreas){
            if(area != null){
                area.hideJoinButton();
            }
        }
    }

    private void showAllJoinButtons() {
        for(GameBoard area : uiAreas){
            if(area != null){
                area.showJoinButton();
            }
        }
    }

    private int getElapsedSeconds(){
        return (int) ((TimeUtils.millis() - nextGame) / 1000);
    }

    private void startGame(){
        if(!gameClock.isRunning()) {
            logger.debug("Starting clock now");
            gameClock.start();
            simulatedGame.startGame();
        }
    }

    private void stopGame(){
        if(gameClock.isRunning()) {
            logger.debug("Starting clock now");
            gameClock.stop();
        }
    }

    private void showGameOver(Stage stage, GameManager game){
        logger.enter("showGameOver");

        if(game != null && stage != null){
            if(game.showGameOver()){
                uiService.getSoundFXFactory().playGameOverSound();
                yahooPlayed = false;
                uiService.getSoundFXFactory().stopMenacingSound();
                toggleGameStart();
                stage.addActor(getGameOverActor(game));
            }
        }
        logger.exit("showGameOver");
    }

    private Actor getGameOverActor(GameManager game) {
        logger.enter("getGameOverActor");
        if(game != null){
            Array<YokelPlayer> winners = game.getWinners();
            YokelPlayer player1 = getPlayer(winners, 0);
            YokelPlayer player2 = getPlayer(winners, 1);
            logger.debug("winners={}", winners);
            logger.debug("player1={}", player1);
            logger.debug("player2={}", player2);

            GameOverText gameOverText = new GameOverText(sessionService.getCurrentPlayer().equals(player1) || sessionService.getCurrentPlayer().equals(player2), player1, player2, uiService.getSkin());
            gameOverText.setPosition(uiService.getStage().getWidth() / 2, uiService.getStage().getHeight() / 2);
            return gameOverText;
        }
        logger.exit("getGameOverActor");
        return new GameOverText(false, sessionService.getCurrentPlayer(), sessionService.getCurrentPlayer(), uiService.getSkin());
    }

    private YokelPlayer getPlayer(Array<YokelPlayer> players, int index){
        if(logger.isDebugOn()){
            ObjectMap<String, Object> map = GdxMaps.newObjectMap();
            map.put("players", players);
            map.put("index", index);
            logger.enter("getPlayer", map);
        }

        if(!YokelUtilities.isEmpty(players) && index != players.size){
            logger.debug("player={}", players.get(index));
            if(index < players.size) return players.get(index);
        }

        logger.exit("getPlayer");
        return null;
    }

    @LmlAction("toggleGameStart")
    private void toggleGameStart() {
        logger.enter("toggleGameStart");
        if(!attemptGameStart){
            attemptGameStart = true;
        }
        if(nextGameDialog){
            nextGameDialog = false;
            stopGame();
        }
        logger.error("attemptGameStart={}", attemptGameStart);
        logger.exit("toggleGameStart");
    }

    @LmlAction("playerStandUpAction")
    private void playerStandUpAction(){
        try {
            if(isUsingServer) {
                sessionService.asyncTableStandRequest(sessionService.getCurrentTableNumber(), sessionService.getCurrentSeat());
            } else {
                YokelTable table = sessionService.getCurrentTable();
                table.getSeat(sessionService.getCurrentSeat()).standUp();
            }
        } catch (InterruptedException e) {
            sessionService.handleException(logger, e);
        }
    }

    private void handlePlayerInput() throws InterruptedException {
        logger.enter("handlePlayerInput");
        if(!isGameOver) {
            sessionService.handlePlayerSimulatedInput(simulatedGame);
            if(isUsingServer){
                sessionService.handlePlayerInputToServer();
            }
        }
        logger.exit("handlePlayerInput");
    }


    private GameManager fetchGameManagerFromServer(float delta) throws InterruptedException {
        logger.enter("fetchGameManagerFromServer");
        //sessionService.asyncGameManagerFromServerRequest();

        GameManager game;

        if(isUsingServer) {
            //TODO: Check if received new GameManager, return current simulation if null.
            game = sessionService.asyncGetGameManagerFromServerRequest();
        } else {
            game = simulatedGame;
            game.setTable(sessionService.getCurrentTable());
            if(isGameRunning(game)) {
                //TODO: game state will not need to be updated anymore, it will be updated from server
                game.update(delta);
                simulatedGame = game;
            }
        }

        logger.exit("fetchGameManagerFromServer");
        return game;
    }

    private void setUpJoinButtons(GameBoard[] uiAreas) {
        if(uiAreas != null){
            for(int i = 0; i < uiAreas.length; i++) {
                GameBoard area = uiAreas[i];
                if(area != null){
                    area.addButtonListener(getButtonListener(i));
                }
            }
        }
    }

    private void handleStartButtonClick(final int clickNumber) throws InterruptedException {
        logger.enter("handleStartButtonClick");
        int seatNumber = order[clickNumber].getSeatNumber();

        logger.error("Sitting down at player number={}", seatNumber);

        if(isUsingServer){
            sessionService.asyncTableSitRequest(sessionService.getCurrentTableNumber(), seatNumber);
        } else {
            YokelPlayer player = sessionService.getCurrentPlayer();
            int currentSeat = sessionService.getCurrentSeat();

            YokelTable table = sessionService.getCurrentTable();
            if(table != null) {
                Array<YokelSeat> seats = table.getSeats();
                logger.error("seats={}", seats);
                logger.error("getCurrentSeat={}", currentSeat);
                logger.error("getCurrentPlayer={}", player);

                if(seats != null){
                    for(int s = 0; s < seats.size; s++){
                        logger.error("seat={}", table.getSeat(s));

                        if(s == seatNumber){
                            YokelSeat seat = table.getSeat(s);
                            if(seat != null){
                                sessionService.setCurrentSeat(s);
                                seat.sitDown(player);
                            }
                        }
                        if(s == currentSeat){
                            YokelSeat seat = table.getSeat(s);
                            if(seat != null){
                                seat.standUp();
                            }
                        }
                    }
                }
            }
            sessionService.setCurrentTable(table);
        }
        logger.exit("handleStartButtonClick");
    }

    private ClickListener getButtonListener(int seatNumber){
        logger.enter("getListener");

        return new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    handleStartButtonClick(seatNumber);
                } catch (InterruptedException e) {
                    sessionService.handleException(logger, e);
                }
                return true;
            }
        };
    }*/
}