package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.TimeUtils;
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
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;

import java.util.Iterator;

import asg.games.yipee.libgdx.objects.YipeeBrokenBlockGDX;
import asg.games.yipee.libgdx.objects.YipeeGameBoardStateGDX;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.libgdx.objects.YipeeSeatGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import asg.games.yipee.libgdx.tools.LibGDXUtil;
import asg.games.yipee.libgdx.tools.NetUtil;
import asg.games.yipee.net.game.GameManager;
import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.action.PlaySoundAction;
import asg.games.yokel.client.controller.dialog.NextGameController;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.game.ClientGameManager;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.SoundFXService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.ui.actors.GameBlockGrid;
import asg.games.yokel.client.ui.actors.GameBrokenBlockSpriteContainer;
import asg.games.yokel.client.ui.actors.GameClock;
import asg.games.yokel.client.ui.actors.GamePlayerBoard;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.client.utils.YokelUtilities;

@View(id = GlobalConstants.UI_CLIENT_VIEW, value = GlobalConstants.UI_CLIENT_VIEW_PATH)
public class ClientViewController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    // Getting a utility logger:
    private static final int BREAK_TIMER_MAX = 106;
    private static final float YAHOO_DURATION = 1.56f;
    private static final float BLOCK_BREAK_DURATION = 0.65f;
    private static final float BREAK_CHECK_INTERVAL = 4f;

    @Inject
    private UserInterfaceService uiService;
    @Inject
    private SoundFXService soundFXService;
    @Inject
    private SessionService sessionService;
    @Inject
    private LoggerService loggerService;
    @Inject
    private InterfaceService interfaceService;
    @Inject
    private MusicService musicService;
    @Inject
    private LoadingController assetLoader;

    @LmlActor("gameClock")
    private GameClock gameClock;
    @LmlActor("1:area")
    private GamePlayerBoard uiArea1;
    @LmlActor("2:area")
    private GamePlayerBoard uiArea2;
    @LmlActor("3:area")
    private GamePlayerBoard uiArea3;
    @LmlActor("4:area")
    private GamePlayerBoard uiArea4;
    @LmlActor("5:area")
    private GamePlayerBoard uiArea5;
    @LmlActor("6:area")
    private GamePlayerBoard uiArea6;
    @LmlActor("7:area")
    private GamePlayerBoard uiArea7;
    @LmlActor("8:area")
    private GamePlayerBoard uiArea8;

    private float brokenCheck = BREAK_CHECK_INTERVAL;
    private boolean isGameOver;
    private boolean isYahooActive;
    private boolean isUsingServer;
    private boolean isMenacingPlaying;
    private boolean nextGameDialog;
    private boolean attemptGameStart;
    private boolean isGameReady = false;
    private long nextGame = 0;
    private GamePlayerBoard[] uiAreas;
    private final YipeeSeatGDX[] order = new YipeeSeatGDX[8];
    private final boolean[] isYahooPlaying = new boolean[8];
    private final boolean[] isAlive = new boolean[8];
    private GameManager simulatedGame;
    private boolean yahooPlayed;
    private boolean isBrokenPlaying;
    private float yahTimer, brokenCellTimer;
    private Log4LibGDXLogger logger;
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue1 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue2 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue3 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue4 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue5 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue6 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue7 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue8 = new OrderedSet<>();

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        try {
            logger.enter("initialize");
            isUsingServer = false;

            //UIManager.UIManagerUserConfiguration config = new UIManager.UIManagerUserConfiguration();

            if (!isUsingServer) {
                //UI Configuration manager needs to handle these.
                YipeePlayerGDX player1 = new YipeePlayerGDX("enboateng");
                YipeePlayerGDX player2 = new YipeePlayerGDX("lholtham", 1400, 1);
                YipeePlayerGDX player3 = new YipeePlayerGDX("rmeyers", 1700, 1);

                ObjectMap<String, Object> arguments = GdxMaps.newObjectMap();
                arguments.put(YipeeTableGDX.ARG_TYPE, YipeeTableGDX.ENUM_VALUE_PRIVATE);
                arguments.put(YipeeTableGDX.ARG_RATED, true);

                //setTable
                YipeeTableGDX table = new YipeeTableGDX(1, arguments);
                //config.setTableNumber(1);
                int currentSeatNumber = 3;

                //this needs to be set outside of the UI manager
                sessionService.setCurrentPlayer(player1);
                sessionService.setCurrentLoungeName("Social");
                sessionService.setCurrentRoomName("Eiffel Tower");
                sessionService.setCurrentSeat(-1);
                sessionService.setCurrentTable(table);
                //config.updateConfig(sessionService);
                //config.setSeat(5, player2);
                //config.setCurrentPlayer(sessionService.getCurrentPlayer());
                //config.setCurrentSeat(sessionService.getCurrentSeat());

                setSeat(6, player3, table);
                setSeat(1, player2, table);
                simulatedGame = new ClientGameManager();
            } else {
                //TODO: Fetch table state from server
                isUsingServer = false;
            }

            uiAreas = new GamePlayerBoard[]{uiArea1, uiArea2, uiArea3, uiArea4, uiArea5, uiArea6, uiArea7, uiArea8};
            setUpDefaultSeats(sessionService.getCurrentTable());
            setUpJoinButtons(uiAreas);
            //uiManager = new UIManager(uiAreas, isUsingServer, config);
            logger.exit("initialize");
        } catch (ReflectionException e) {
            String errorMsg = "Error in initialize()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    public void setSeat(int seatNumber, YipeePlayerGDX player, YipeeTableGDX table) throws ReflectionException {
        try {
            logger.enter("setSeat");
            if (table != null) {
                if (seatNumber < 0 || seatNumber > YipeeTableGDX.MAX_SEATS) return;
                //YipeeSeatGDX seat = table.getSeat(seatNumber);
                YipeeSeatGDX seat = null;
                if (seat != null) {
                    if (player == null) {
                        seat.standUp();
                    } else {
                        seat.sitDown(player);
                        seat.setSeatReady(true);
                    }
                }
            }
            logger.exit("setSeat");
        } catch (Exception e) {
            logger.error(e, "setting player {} @ seat[{}]", player, seatNumber);
            throw new ReflectionException();
        }
    }

    @Override
    public void destroy(ViewController viewController) {
        try {
            logger.enter("destroy");
            logger.info("destroying viewID: {}", GlobalConstants.UI_CLIENT_VIEW);
            //Disposables.disposeOf(simulatedGame);

            for (YipeeSeatGDX seat : order) {
                if (seat != null) {
                    seat.dispose();
                }
            }
            logger.exit("destroy");
        } catch (Exception e) {
            String errorMsg = "Error in destroy()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    @Override
    public void render(Stage stage, float delta) {
        try{
            logger.enter("render");
            //Fetch GameManager from Server
            //fetch game state from server
            GameManager game = fetchGameManagerFromServer(delta);
            System.out.println(game);

            //Check if game is ready to start
            //checkIsGameReady(game);

            //Check if Count down is starting
            handleGameStart(game);

            //If Game Over, show it
            handleGameOver(stage, game);

            //Handle Player input
            handlePlayerInput();

            //Update GameState
            updateGameState(game, delta, stage);

            //Add Broken Blocks to animation queues
            addBrokenBlocksToAnimationQueue(game);

            //handle animating broken blocks
            if (--brokenCheck == 0) {
                handleBrokenBlocks(game, stage);
            }

            //Update UI for players before the game
            if (!isGameRunning(game)) {
                updateGameBoardPreStart();
            }

            //Update game timers
            updateTimers();

            //Render
            stage.act(delta);
            stage.draw();
            logger.exit("render");
        } catch (Exception e) {
            String errorMsg = "error render()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    private void addBrokenBlocksToAnimationQueue(GameManager game) throws Exception {
        for (int boardSeat = 0; boardSeat < 8; boardSeat++) {
            for (YipeeBrokenBlockGDX cellBroken : UIUtil.getYipeeBoardState(game, boardSeat).getBrokenCells()) {
                GameBlock gameBlock = UIUtil.getInstance().getGameBlock(cellBroken.getBlock(), uiAreas[boardSeat].isPreview());
                addBrokenBlockActorToQueue(brokenBlocksQueue1, gameBlock, uiAreas[boardSeat].getGrid(), cellBroken.getRow(), cellBroken.getCol());
            }
        }
    }

    private void addBrokenBlockActorToQueue(OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue, GameBlock gameBlock, GameBlockGrid grid, int row, int col) {
        if (gameBlock != null && brokenBlocksQueue != null) {
            GameBrokenBlockSpriteContainer brokenSprite = UIUtil.getBrokenBlockSprites(gameBlock, gameBlock.getBlock(), grid, row, col);
            brokenBlocksQueue.add(brokenSprite);
        }
    }

    private Array<Vector2> getPolygonVertices(int n, float radius, int h, int k) {
        Array<Vector2> verts = GdxArrays.newArray();
        double angle_between_vertices = 2 * Math.PI / n;

        for (int i = n; i >= 0; i--) {
            double theta = i * angle_between_vertices;
            double x = h + radius * Math.cos(theta);
            double y = k + radius * Math.sin(theta);
            verts.add(new Vector2((float) x, (float) y));
        }

        return verts;
    }

    private void handleBrokenBlocks(GameManager game, Stage stage) throws Exception {
        if (brokenBlocksQueue1.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 0);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue1);
        }
        if (brokenBlocksQueue2.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 1);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue2);
        }
        if (brokenBlocksQueue3.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 2);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue3);
        }
        if (brokenBlocksQueue4.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 3);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue4);
        }
        if (brokenBlocksQueue5.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 4);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue5);
        }
        if (brokenBlocksQueue6.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 5);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue6);
        }
        if (brokenBlocksQueue7.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 6);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue7);
        }
        if (brokenBlocksQueue8.size > 0) {
            YipeeGameBoardStateGDX state = UIUtil.getYipeeBoardState(game, 7);
            UIUtil.addBrokenBlockActorToStage(stage, state.getYahooDuration() > 0, brokenBlocksQueue8);
        }
        brokenCheck = BREAK_CHECK_INTERVAL;
    }

    private boolean isPreview() {
        return false;
    }

    private void updateTimers() throws ReflectionException {
        try {
            logger.enter("updateTimers");
            if (yahTimer > -1) {
                --yahTimer;
            }

            if (brokenCellTimer > -1) {
                --brokenCellTimer;
            }
            logger.error("###yahTimer={}", yahTimer);
            logger.error("###brokenCellTimer={}", brokenCellTimer);
            logger.exit("updateTimers");
        } catch (Exception e) {
            String errorMsg = "Error in updateTimers()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void setUpDefaultSeats(YipeeTableGDX table) throws ReflectionException {
        try {
            logger.enter("setUpDefaultSeats");
            if (table != null) {
                for (int i = 0; i < YipeeTableGDX.MAX_SEATS; i++) {
                    order[i] = table.getSeat(i);
                }
            }
            logger.exit("setUpDefaultSeats");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void updateGameBoardPreStart() throws ReflectionException {
        try {
            logger.enter("updateGameBoardPreStart");

            YipeeTableGDX table = sessionService.getCurrentTable();
            int currentPlayerSeat = sessionService.getCurrentSeat();
            int currentPartnerSeat = getPlayerPartnerSeatNum(currentPlayerSeat);

            if (table != null) {
                ObjectSet<YipeeSeatGDX> seats = table.getSeats();
                if (currentPlayerSeat > -1) {
                    int playerOneSeat = currentPlayerSeat % 2;
                    int playerPartnerSeat = currentPartnerSeat % 2;
                    order[playerOneSeat] = table.getSeat(currentPlayerSeat);
                    order[playerPartnerSeat] = table.getSeat(currentPartnerSeat);

                    //Set up rest of active players+
                    Array<Integer> remaining = GdxArrays.newArray();
                    for (int i = 2; i < YipeeTableGDX.MAX_SEATS; i++) {
                        remaining.add(i);
                    }

                    Iterator<Integer> iterator = YokelUtilities.getArrayIterator(remaining);

                    for (int i = 0; i < YipeeTableGDX.MAX_SEATS; i++) {
                        if (i != currentPlayerSeat && i != currentPartnerSeat) {
                            if (iterator.hasNext()) {
                                YipeeSeatGDX seat = LibGDXUtil.getIndexOfSet(seats, i);
                                if (seat != null) {
                                    order[iterator.next()] = seat;
                                }
                            }
                        }
                    }
                } else {
                    setUpDefaultSeats(table);
                }

                for (int i = 0; i < order.length; i++) {
                    YipeeSeatGDX seat = order[i];
                    logger.debug("seat = {}", seat);
                    if (seat != null) {
                        YipeePlayerGDX player = seat.getSeatedPlayer();
                        logger.debug("player = {}", player);

                        String playerJsonObj = null;
                        logger.debug("getting seat number from seat = {}:", seat);
                        logger.debug("seat Name= {}", seat.getName());
                        int seatNumber = seat.getSeatNumber();

                        if (player != null) {
                            playerJsonObj = NetUtil.toJsonClient(player);
                            isAlive[seatNumber] = true;
                        } else {
                            isAlive[seatNumber] = false;
                        }

                        GamePlayerBoard area = uiAreas[i];

                        if (area != null) {
                            area.updateYokelData(playerJsonObj);
                            area.setPlayerView(sessionService.isCurrentPlayer(player));
                            area.setActive(sessionService.isCurrentPlayer(player));
                        }
                    }
                }
            }
            logger.exit("updateGameBoardPreStart");
        } catch (Exception e) {
            String errorMsg = "Error in updateGameBoardPreStart()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }

    }

    private int getPlayerPartnerSeatNum(int seatNumber) throws ReflectionException {
        try {
            logger.enter("getPlayerPartnerSeatNum");
            int seatNumReturn = 0;
            if (seatNumber % 2 == 0) {
                seatNumReturn = seatNumber + 1;
            } else {
                seatNumReturn = seatNumber - 1;
            }
            logger.exit("getPlayerPartnerSeatNum", seatNumReturn);
            return seatNumReturn;
        } catch (Exception e) {
            String errorMsg = "Error in getPlayerPartnerSeatNum()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    //Needs to update the simulated game with the server state.
    private void updateGameState(GameManager game, float delta, Stage stage) throws ReflectionException {
        try {
            logger.enter("setUpDefaultSeats");
            if (logger.isDebugOn()) {
                ObjectMap<String, Object> map = GdxMaps.newObjectMap();
                map.put("game", game);
                map.put("delta", delta);
                map.put("stage", stage);
                logger.enter("updateGameState", map);
            }

            //Update current game
            if (isGameRunning(game)) {
                //TODO: implement gameState generation which will be used locally for player
                game.update(delta);

                for (int board = 0; board < order.length; board++) {
                    //Get Game seat
                    int gameSeat = order[board].getSeatNumber();
                    boolean isPlayerDead = game.isPlayerDead(gameSeat);

                    if (isPlayerDead && isAlive[gameSeat]) {
                        isAlive[gameSeat] = false;
                        killPlayer(uiAreas[board]);
                    } else {
                        updateUiBoard(uiAreas[board], UIUtil.getYipeeBoardState(game, gameSeat), gameSeat);
                    }
                }
            }
            logger.exit("updateGameState");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }

    }

    private void updateUiBoard(GamePlayerBoard uiArea, YipeeGameBoardStateGDX gameBoardState, int gameBoardSeat) throws ReflectionException {
        try {
            if (logger.isDebugOn()) {
                ObjectMap<String, Object> map = GdxMaps.newObjectMap();
                map.put("uiArea", uiArea);
                map.put("gameBoardState", gameBoardState);
                logger.enter("updateUiBoard", map);
            }
            if (uiArea != null) {
                uiArea.renderPlayerBoard(gameBoardState);
                int yahooDuration = gameBoardState.getYahooDuration();

                if (isPlayerBoard(gameBoardSeat)) {
                    if (gameBoardState.isPieceSet()) {
                        soundFXService.playPiecePlacedSound();
                    }
                    Iterable<YipeeBrokenBlockGDX> brokenCells = gameBoardState.getBrokenCells();
                    logger.error("gameboard Name={}: gameboard broken cells={}", gameBoardState.getName(), brokenCells);
                    if (!YokelUtilities.isEmpty(brokenCells)) {
                        //Play broken cell sound
                        soundFXService.playBrokenCell();
                        logger.error("Broken Cells={}", brokenCells);
                    }
                }

                isYahooActive = yahooDuration > 0;
                uiArea.setYahoo(isYahooActive);
                if (isYahooActive) {
                    if (isPlayerBoard(gameBoardSeat)) {
                        soundFXService.startYahooFanfare(gameBoardSeat);
                    } else {
                        soundFXService.playYahooSound();
                    }
                } else {
                    soundFXService.stopYahooFanfare(gameBoardSeat);
                }
            }
            logger.exit("updateGameState");
        } catch (Exception e) {
            String errorMsg = "Error in updateGameState()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private static PlaySoundAction playSoundAction(Sound sound) {
        /*try {
            //logger.enter("setUpDefaultSeats");
            //if(table != null){
            //    for(int i = 0; i < YipeeTableGDX.MAX_SEATS; i++){
           //         order[i] = table.getSeat(i);
             //   }
           // }
           // logger.exit("setUpDefaultSeats");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
        }*/
        PlaySoundAction action = Actions.action(PlaySoundAction.class);
        action.setSound(sound);
        return action;
    }

    private boolean isPlayerBoard(int gameBoardInt) throws ReflectionException {
        try {
            logger.enter("isPlayerBoard");
            boolean isPlayerBoard = sessionService.getCurrentSeat() == gameBoardInt;
            logger.exit("isPlayerBoard", isPlayerBoard);
            return isPlayerBoard;
        } catch (Exception e) {
            String errorMsg = "Error in isPlayerBoard()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void killPlayer(GamePlayerBoard uiArea) throws ReflectionException {
        try {
            logger.enter("killPlayer");
            if (uiArea != null) {
                soundFXService.playBoardDeathSound();
                uiArea.killPlayer();
            }
            logger.exit("killPlayer");
        } catch (Exception e) {
            String errorMsg = "Error in killPlayer()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private boolean isGameRunning(GameManager game) throws ReflectionException {
        try {
            logger.enter("isGameRunning");
            boolean isGameRunning = game != null && game.isRunning();
            logger.exit("isGameRunning", isGameRunning);
            return isGameRunning;
        } catch (Exception e) {
            String errorMsg = "Error in isGameRunning()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void updateState(GameManager game) throws ReflectionException {
        //TODO: Need a collection of GameManagers and update current one based off
        try {
            logger.enter("updateState");
            if (game != null) {
                //serverGameManger = game;
            }
            logger.exit("updateState");
        } catch (Exception e) {
            String errorMsg = "Error in updateState()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void checkIsGameReady(GameManager game) throws ReflectionException {
        try {
            logger.enter("checkIsGameReady");
            for (int i = 0; i < order.length; i++) {
                YipeeSeatGDX seat = order[i];
                logger.debug("seat= {}", NetUtil.toJsonClient(seat));
                GamePlayerBoard uiArea = uiAreas[i];
                uiArea.setGameReady(seat.isSeatReady());
            }
            if (game != null) {
                //isGameReady = game.isGameReady();
            }
            logger.exit("checkIsGameReady");
        } catch (Exception e) {
            String errorMsg = "Error in checkIsGameReady()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void handleGameStart(GameManager game) throws ReflectionException {
        try {
            logger.enter("handleGameStart");
            if (gameClock == null) return;
            logger.debug("isGameReady={}", isGameReady);

            if (isGameReady) {
                if (attemptGameStart) {
                    if (!nextGameDialog) {
                        logger.debug("Showing next Game Dialog");
                        nextGameDialog = true;
                        interfaceService.showDialog(NextGameController.class);
                        nextGame = TimeUtils.millis();
                        hideAllJoinButtons();
                    }

                    if (getElapsedSeconds() > NextGameController.NEXT_GAME_SECONDS) {
                        interfaceService.destroyDialog(NextGameController.class);
                        attemptGameStart = false;
                        startGame();
                    }
                }
            }
            logger.exit("handleGameStart");
        } catch (Exception e) {
            String errorMsg = "Error in handleGameStart()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void hideAllJoinButtons() throws ReflectionException {
        try {
            logger.enter("hideAllJoinButtons");
            for (GamePlayerBoard area : uiAreas) {
                if (area != null) {
                    area.hideJoinButton();
                }
            }
            logger.exit("hideAllJoinButtons");
        } catch (Exception e) {
            String errorMsg = "Error in hideAllJoinButtons()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }

    }

    private void showAllJoinButtons() throws ReflectionException {
        try {
            logger.enter("setUpDefaultSeats");
            for (GamePlayerBoard area : uiAreas) {
                if (area != null) {
                    area.showJoinButton();
                }
            }
            logger.exit("setUpDefaultSeats");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }

    }

    private int getElapsedSeconds() throws ReflectionException {
        try {
            logger.enter("getElapsedSeconds");
            int elapsed = YokelUtilities.otoi((TimeUtils.millis() - nextGame) / 1000);

            logger.exit("getElapsedSeconds", elapsed);
            return elapsed;
        } catch (Exception e) {
            String errorMsg = "Error in getElapsedSeconds()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void startGame() throws ReflectionException {
        try {
            logger.enter("startGame");
            if (!gameClock.isRunning()) {
                logger.debug("Starting clock now");
                gameClock.start();
                simulatedGame.startGameLoop();
            }
            logger.exit("startGame");
        } catch (Exception e) {
            String errorMsg = "Error in startGame()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void stopGame() throws ReflectionException {
        try {
            logger.enter("stopGame");
            if (gameClock.isRunning()) {
                logger.debug("Stopping clock now");
                gameClock.stop();
            }
            logger.exit("stopGame");
        } catch (Exception e) {
            String errorMsg = "Error in stopGame()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void handleGameOver(Stage stage, GameManager game) throws ReflectionException {
        try {
            logger.enter("handleGameOver");
            if (game != null && stage != null) {
                if (game.isRunning()) {
                    soundFXService.playGameOverSound();
                    yahooPlayed = false;
                    soundFXService.stopMenacingSound();
                    toggleGameStart();
                    stage.addActor(getGameOverActor(game));
                }
            }
            logger.exit("handleGameOver");
        } catch (Exception e) {
            String errorMsg = "Error in handleGameOver()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private Actor getGameOverActor(GameManager game) throws ReflectionException {
        try {
            logger.enter("getGameOverActor");
            if (game != null) {
                Array<YipeePlayerGDX> winners = getGameWinners(game);
                YipeePlayerGDX player1 = getPlayer(winners, 0);
                YipeePlayerGDX player2 = getPlayer(winners, 1);
                logger.debug("winners={}", winners);
                logger.debug("player1={}", player1);
                logger.debug("player2={}", player2);

                //GameOverText gameOverText = new GameOverText(sessionService.getCurrentPlayer().equals(player1) || sessionService.getCurrentPlayer().equals(player2), player1, player2, uiService.getSkin());
                //gameOverText.setPosition(uiService.getStage().getWidth() / 2, uiService.getStage().getHeight() / 2);
                return null;
            }
            logger.exit("getGameOverActor");
            return null;

            //return new GameOverText(false, sessionService.getCurrentPlayer(), sessionService.getCurrentPlayer(), uiService.getSkin());
        } catch (Exception e) {
            String errorMsg = "Error in getGameOverActor()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private Array<YipeePlayerGDX> getGameWinners(GameManager game) {
        Array<YipeePlayerGDX> winners = GdxArrays.newArray();
        if (game instanceof ClientGameManager) {
            winners = ((ClientGameManager) game).getWinners();
        }
        return winners;
    }

    private YipeePlayerGDX getPlayer(Array<YipeePlayerGDX> players, int index) throws ReflectionException {
        try {
            if (logger.isDebugOn()) {
                ObjectMap<String, Object> map = GdxMaps.newObjectMap();
                map.put("players", players);
                map.put("index", index);
                logger.enter("getPlayer", map);
            }

            YipeePlayerGDX returnedPlayer = null;
            if (!YokelUtilities.isEmpty(players) && index != players.size) {
                logger.debug("player={}", players.get(index));
                if (index < players.size) {
                    returnedPlayer = players.get(index);
                }
            }

            logger.exit("getPlayer", returnedPlayer);
            return returnedPlayer;
        } catch (Exception e) {
            String errorMsg = "Error in getPlayer()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    @LmlAction("toggleGameStart")
    private void toggleGameStart() throws ReflectionException {
        try {
            logger.enter("toggleGameStart");
            if (!attemptGameStart) {
                attemptGameStart = true;
            }
            if (nextGameDialog) {
                nextGameDialog = false;
                stopGame();
            }
            logger.debug("attemptGameStart={}", attemptGameStart);
            logger.exit("toggleGameStart");
        } catch (Exception e) {
            String errorMsg = "Error in toggleGameStart()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    @LmlAction("playerStandUpAction")
    private void playerStandUpAction() throws ReflectionException {
        try {
            logger.enter("playerStandUpAction");

            try {
                if (isUsingServer) {
                    sessionService.asyncTableStandRequest(sessionService.getCurrentTableNumber(), sessionService.getCurrentSeat());
                } else {
                    YipeeTableGDX table = sessionService.getCurrentTable();
                    table.getSeat(sessionService.getCurrentSeat()).standUp();
                }
            } catch (InterruptedException e) {
                sessionService.handleException(null, e);
            }
            logger.exit("playerStandUpAction");
        } catch (Exception e) {
            String errorMsg = "Error in playerStandUpAction()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private void handlePlayerInput() throws InterruptedException, ReflectionException {
        try {
            logger.enter("handlePlayerInput");
            if (!isGameOver) {
                sessionService.handlePlayerInput(simulatedGame);
            }
            logger.exit("handlePlayerInput");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }


    private GameManager fetchGameManagerFromServer(float delta) throws InterruptedException, ReflectionException {
        try {
            logger.enter("fetchGameManagerFromServer");
            //sessionService.asyncGameManagerFromServerRequest();

            GameManager game;

            if (isUsingServer) {
                //TODO: Check if received new GameManager, return current simulation if null.
                game = sessionService.asyncGetGameManagerFromServerRequest();
            } else {
                game = simulatedGame;
                //game.setTable(sessionService.getCurrentTable());
                if (isGameRunning(game)) {
                    //TODO: game state will not need to be updated anymore, it will be updated from server
                    game.update(delta);
                    simulatedGame = game;
                }
            }

            logger.exit("fetchGameManagerFromServer");
            return game;

        } catch (Exception e) {
            String errorMsg = "Error in fetchGameManagerFromServer()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }

    }

    private YipeeGameBoardStateGDX fetchGameStateFromServer(float delta) throws InterruptedException, ReflectionException {
        try {
            logger.enter("fetchGameStateFromServer");
            //sessionService.asyncGameManagerFromServerRequest();

            GameManager game = null;
            YipeeGameBoardStateGDX state = null;

            if (isUsingServer) {
                //TODO: Check if received new GameManager, return current simulation if null.
                //game = sessionService.asyncGetGameManagerFromServerRequest();
            } else {
                game = simulatedGame;
            }

            //Fetch Game state from Manager
            if (game != null) {
                //state = game.getBoardState(1);
            }

            logger.exit("fetchGameStateFromServer", state);
            return state;
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }

    }

    private void setUpJoinButtons(GamePlayerBoard[] uiAreas) throws ReflectionException {
        try {
            logger.enter("setUpJoinButtons");
            if (uiAreas != null) {
                for (int i = 0; i < uiAreas.length; i++) {
                    GamePlayerBoard area = uiAreas[i];
                    if (area != null) {
                        area.addButtonListener(getButtonListener(i));
                    }
                }
            }
            logger.exit("setUpJoinButtons");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }

    }

    private void handleStartButtonClick(final int clickNumber) throws InterruptedException, ReflectionException {
        try {
            logger.enter("handleStartButtonClick");
            int seatNumber = order[clickNumber].getSeatNumber();

            //Send request to server to sit down
            if (isUsingServer) {
                sessionService.asyncTableSitRequest(sessionService.getCurrentTableNumber(), seatNumber);
            }
            //Simulate the sitting process for the viewer
            YipeePlayerGDX player = sessionService.getCurrentPlayer();
            int currentSeat = sessionService.getCurrentSeat();
            YipeeTableGDX table = sessionService.getCurrentTable();

            logger.info("Sitting down at player: {} @Table[{}] @ Seat={}", player, table, seatNumber);
            if (table != null) {
                ObjectSet<YipeeSeatGDX> seats = table.getSeats();
                logger.debug("seats={}", seats);
                logger.debug("getCurrentSeat={}", currentSeat);
                logger.debug("getCurrentPlayer={}", player);

                if (seats != null) {
                    for (int s = 0; s < seats.size; s++) {
                        logger.debug("seat={}", table.getSeat(s));

                        if (s == seatNumber) {
                            YipeeSeatGDX seat = table.getSeat(s);
                            if (seat != null) {
                                sessionService.setCurrentSeat(s);
                                seat.sitDown(player);
                            }
                        }
                        if (s == currentSeat) {
                            YipeeSeatGDX seat = table.getSeat(s);
                            if (seat != null) {
                                logger.info("Standing player: {} @Table[{}] @ Seat={}", player, table, seatNumber);
                                seat.standUp();
                            }
                        }
                    }
                }

                sessionService.setCurrentTable(table);
            }
            logger.exit("handleStartButtonClick");
        } catch (Exception e) {
            String errorMsg = "Error in handleStartButtonClick()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }

    private ClickListener getButtonListener(int seatNumber) throws ReflectionException {
        try {
            logger.enter("getListener");

            return new ClickListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    try {
                        logger.debug("Handling Click Event on seat: {}", seatNumber);
                        handleStartButtonClick(seatNumber);
                    } catch (InterruptedException | ReflectionException e) {
                        sessionService.handleException(logger, e);
                    }
                    logger.exit("getListener", true);
                    return true;
                }
            };
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException(e);
        }
    }
}