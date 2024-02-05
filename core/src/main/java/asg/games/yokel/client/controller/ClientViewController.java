package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
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
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;

import java.util.Iterator;
import java.util.Vector;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.action.PlaySoundAction;
import asg.games.yokel.client.controller.dialog.NextGameController;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.SoundFXService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.ui.actors.GameBrokenBlockSpriteContainer;
import asg.games.yokel.client.ui.actors.GameClock;
import asg.games.yokel.client.ui.actors.GamePlayerBoard;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.managers.GameManager;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelGameBoardState;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.objects.YokelSeat;
import asg.games.yokel.objects.YokelTable;
import asg.games.yokel.utils.YokelUtilities;

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
    private final YokelSeat[] order = new YokelSeat[8];
    private final boolean[] isYahooPlaying = new boolean[8];
    private final boolean[] isAlive = new boolean[8];
    private GameManager simulatedGame;
    private boolean yahooPlayed;
    private boolean isBrokenPlaying;
    private float yahTimer, brokenCellTimer;
    private Log4LibGDXLogger logger;
    private final Array<GameBrokenBlockSpriteContainer> brokenBlocksQueue = GdxArrays.newArray();
    private final Queue<GameBlock> brokenBlocks = new Queue<>();

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        try {
            logger.enter("initialize");
            isUsingServer = false;

            //UIManager.UIManagerUserConfiguration config = new UIManager.UIManagerUserConfiguration();

            if (!isUsingServer) {
                //UI Configuration manager needs to handle these.
                YokelPlayer player1 = new YokelPlayer("enboateng");
                YokelPlayer player2 = new YokelPlayer("lholtham", 1400, 1);
                YokelPlayer player3 = new YokelPlayer("rmeyers", 1700, 1);

                //setTable
                YokelTable table = new YokelTable("sim:table:1", 1);
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
                simulatedGame = new GameManager(table);
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

    public void setSeat(int seatNumber, YokelPlayer player, YokelTable table) throws ReflectionException {
        try {
            logger.enter("setSeat");
            if (table != null) {
                if (seatNumber < 0 || seatNumber > YokelTable.MAX_SEATS) return;
                YokelSeat seat = table.getSeat(seatNumber);
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
            Disposables.disposeOf(simulatedGame);

            for (YokelSeat seat : order) {
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
            YokelGameBoardState gameState = fetchGameStateFromServer(delta);

            //Check if game is ready to start
            checkIsGameReady(game);

            //Check if Count down is starting
            handleGameStart(game);

            //If Game Over, show it
            handleGameOver(stage, game);

            //Handle Player input
            handlePlayerInput();

            //Update GameState
            updateGameState(game, delta, stage);

            //handle animating broken blocks
            handleBrokenBlocks(game, stage);

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

    private void handleBrokenBlocks(GameManager game, Stage stage) {
        if (brokenBlocksQueue.size > 0 && --brokenCheck == 0) {
            brokenCheck = BREAK_CHECK_INTERVAL;
            addBrokenBlockActorToStage(stage);
        }
    }

    void addBrokenBlockActorToStage(Stage stage) {
        if (stage != null) {
            Array.ArrayIterator<GameBrokenBlockSpriteContainer> brokenIterator = brokenBlocksQueue.iterator();

            boolean toggleYahoo = false;
            if (toggleYahoo) {
                logger.error("verts: {}" + getPolygonVertices(brokenBlocksQueue.size, stage.getWidth(), 0, 0));
                Array<Vector2> vectors = getPolygonVertices(brokenBlocksQueue.size, stage.getWidth() + 40, 300, 300);
                Queue<Vector2> yahooStarEnds = new Queue<>();

                for (Vector2 vector : vectors) {
                    yahooStarEnds.addFirst(vector);
                }

                while (brokenIterator.hasNext()) {
                    GameBrokenBlockSpriteContainer brokenGameSprite = brokenIterator.next();
                    if (brokenGameSprite != null) {
                        GameBlock parent = brokenGameSprite.getParentGameBlock();
                        Image left = brokenGameSprite.getLeftSprite();
                        Image bottom = brokenGameSprite.getBottomSprite();
                        Image right = brokenGameSprite.getRightSprite();

                        Interpolation interpolation = Interpolation.smoother;

                        if (parent != null) {
                            Image block = new Image(parent.getImage().getDrawable());
                            parent.setBlock(YokelBlockEval.addBrokenFlag(parent.getBlock()));
                            Vector2 cord = yahooStarEnds.removeFirst();

                            float endOfScreenX = cord.x;
                            float endOfScreenY = cord.y;
                            //Get the coordinates of the parent block
                            Vector2 blockV = left.localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                            //Get the screen coordinates
                            Vector2 blockV2 = left.localToScreenCoordinates(new Vector2(blockV.x, blockV.y));
                            //Shift block x slightly
                            //blockV2.x -= parent.getX();
                            //blockV2.x -= brokenOffSet;
                            //Shift (and flip) block y
                            blockV2.x -= parent.getX();
                            blockV2.y = 1 - (blockV2.y) + stage.getHeight();

                            block.setVisible(true);
                            block.setBounds(blockV2.x, blockV2.y, block.getWidth(), block.getHeight());

                            if (endOfScreenX == -1) {
                                endOfScreenX = blockV2.x;
                            }
                            if (endOfScreenY == -1) {
                                endOfScreenY = blockV2.y;
                            }
                            //logger.error("endOfScreen({},{})",endOfScreenX,endOfScreenY);
                            block.addAction(Actions.sequence(Actions.moveTo(endOfScreenX, endOfScreenY, YAHOO_DURATION, interpolation), Actions.removeActor(block)));
                            left.setVisible(false);
                            bottom.setVisible(false);
                            right.setVisible(false);
                            stage.addActor(block);
                        }
                        Pools.free(brokenGameSprite);
                        brokenIterator.remove();
                    }
                }
                logger.error("size: " + brokenBlocksQueue.size);

            } else {
                if (brokenIterator.hasNext()) {
                    GameBrokenBlockSpriteContainer brokenGameSprite = brokenIterator.next();
                    if (brokenGameSprite != null) {
                        float duration = BLOCK_BREAK_DURATION;
                        float delay = 0.05f;
                        float endOfScreen = 0f;
                        float blockOffSet = 32;

                        Interpolation interpolation = Interpolation.sineIn;

                        Image left = brokenGameSprite.getLeftSprite();
                        Image bottom = brokenGameSprite.getBottomSprite();
                        Image right = brokenGameSprite.getRightSprite();
                        GameBlock parent = brokenGameSprite.getParentGameBlock();
                        float brokenXOffSet = 3;
                        float brokenYOffSet = left.getHeight();

                        if (parent != null) {
                            parent.setBlock(YokelBlockEval.addBrokenFlag(parent.getBlock()));
                            //parent.addAction(Actions.sequence(Actions.delay(delay), Actions.visible(false)));

                            //Get the coordinates of the parent block
                            Vector2 leftV = left.localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                            //Get the screen coordinates
                            Vector2 leftV2 = left.localToScreenCoordinates(new Vector2(leftV.x, leftV.y));
                            //Shift block x slightly
                            leftV2.x -= parent.getX();
                            leftV2.x -= brokenXOffSet;
                            //Shift (and flip) block y
                            leftV2.y = 1 - (leftV2.y - brokenYOffSet) + stage.getHeight();

                            Vector2 bottomV = bottom.localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                            Vector2 bottomV2 = bottom.localToScreenCoordinates(new Vector2(bottomV.x, bottomV.y));
                            bottomV2.x -= parent.getX();
                            bottomV2.y = 1 - (bottomV2.y - brokenYOffSet) + stage.getHeight();

                            Vector2 rightV = right.localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                            Vector2 rightV2 = right.localToScreenCoordinates(new Vector2(rightV.x, rightV.y));
                            rightV2.x -= parent.getX();
                            rightV2.x += brokenXOffSet;
                            rightV2.y = 1 - (rightV2.y - brokenYOffSet) + stage.getHeight();

                            left.setBounds(leftV2.x, leftV2.y, left.getWidth(), left.getHeight());
                            left.addAction(Actions.sequence(Actions.delay(delay),
                                    Actions.moveTo(leftV2.x - blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(left)));

                            bottom.setBounds(bottomV2.x, bottomV2.y, bottom.getWidth(), bottom.getHeight());
                            bottom.addAction(Actions.sequence(Actions.delay(delay),
                                    Actions.moveTo(bottomV2.x, endOfScreen, duration, interpolation), Actions.removeActor(bottom)));

                            right.setBounds(rightV2.x, rightV2.y, right.getWidth(), right.getHeight());
                            right.addAction(Actions.sequence(Actions.delay(delay),
                                    Actions.moveTo(rightV2.x + blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(right)));

                            if (isPreview()) {
                                if (brokenCheck % 3 == 1) {
                                    stage.addActor(left);
                                } else if (brokenCheck % 3 == 2) {
                                    stage.addActor(right);
                                } else {
                                    stage.addActor(bottom);
                                }
                            } else {
                                stage.addActor(left);
                                stage.addActor(bottom);
                                stage.addActor(right);
                            }
                        }
                        brokenIterator.remove();
                        Pools.free(brokenGameSprite);
                    }
                }
            }
            //logger.info("{}: ({},{})", "stage", stage.getWidth(), stage.getHeight());
            //logger.exit("addBrokenBlockActorToStage");
        }
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
            throw new ReflectionException();
        }
    }

    private void setUpDefaultSeats(YokelTable table) throws ReflectionException {
        try {
            logger.enter("setUpDefaultSeats");
            if (table != null) {
                for (int i = 0; i < YokelTable.MAX_SEATS; i++) {
                    order[i] = table.getSeat(i);
                }
            }
            logger.exit("setUpDefaultSeats");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
        }
    }

    private void updateGameBoardPreStart() throws ReflectionException {
        try {
            logger.enter("updateGameBoardPreStart");

            YokelTable table = sessionService.getCurrentTable();
            int currentPlayerSeat = sessionService.getCurrentSeat();
            int currentPartnerSeat = getPlayerPartnerSeatNum(currentPlayerSeat);

            if (table != null) {
                Array<YokelSeat> seats = table.getSeats();
                if (currentPlayerSeat > -1) {
                    int playerOneSeat = currentPlayerSeat % 2;
                    int playerPartnerSeat = currentPartnerSeat % 2;
                    order[playerOneSeat] = table.getSeat(currentPlayerSeat);
                    order[playerPartnerSeat] = table.getSeat(currentPartnerSeat);

                    //Set up rest of active players+
                    Array<Integer> remaining = GdxArrays.newArray();
                    for (int i = 2; i < YokelTable.MAX_SEATS; i++) {
                        remaining.add(i);
                    }

                    Iterator<Integer> iterator = YokelUtilities.getArrayIterator(remaining);

                    for (int i = 0; i < YokelTable.MAX_SEATS; i++) {
                        if (i != currentPlayerSeat && i != currentPartnerSeat) {
                            if (iterator.hasNext()) {
                                order[iterator.next()] = seats.get(i);
                            }
                        }
                    }
                } else {
                    setUpDefaultSeats(table);
                }

                for (int i = 0; i < order.length; i++) {
                    YokelSeat seat = order[i];
                    logger.debug("seat = {}", seat);
                    if (seat != null) {
                        YokelPlayer player = seat.getSeatedPlayer();
                        logger.debug("player = {}", player);

                        String playerJsonObj = null;
                        logger.debug("getting seat number from seat = {}:", seat);
                        logger.debug("seat Name= {}", seat.getName());
                        int seatNumber = seat.getSeatNumber();

                        if (player != null) {
                            playerJsonObj = player.getJsonString();
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
            throw new ReflectionException();
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
            throw new ReflectionException();
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
                        updateUiBoard(uiAreas[board], game.getGameBoard(gameSeat));
                    }
                }
            }
            logger.exit("updateGameState");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
        }

    }

    private void updateUiBoard(GamePlayerBoard uiArea, YokelGameBoard gameBoard) throws ReflectionException {
        try {
            if (logger.isDebugOn()) {
                ObjectMap<String, Object> map = GdxMaps.newObjectMap();
                map.put("uiArea", uiArea);
                map.put("gameBoard", gameBoard);
                logger.enter("updateUiBoard", map);
            }
            if (uiArea != null) {
                //uiArea.renderPlayerBoard(gameBoard);
                int yahooDuration = 3;//gameBoard.fetchYahooDuration();
                int gameBoardSeat = Integer.parseInt(gameBoard.getName());

                if (isPlayerBoard(gameBoardSeat)) {
                /*if (gameBoard.hasPieceSet()) {
                    soundFXService.playPiecePlacedSound();
                }*/
                    Vector<YokelBlock> brokenCells = gameBoard.getBrokenCells();
                    logger.error("gameboard Name={}: gameboard broken cells={}", gameBoard.getName(), gameBoard.getBrokenCells());
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
            throw new ReflectionException();
        }

    }

    private static PlaySoundAction playSoundAction(Sound sound) {
        /*try {
            //logger.enter("setUpDefaultSeats");
            //if(table != null){
            //    for(int i = 0; i < YokelTable.MAX_SEATS; i++){
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
            throw new ReflectionException();
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
            throw new ReflectionException();
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
            throw new ReflectionException();
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
            throw new ReflectionException();
        }
    }

    private void checkIsGameReady(GameManager game) throws ReflectionException {
        try {
            logger.enter("checkIsGameReady");
            for (int i = 0; i < order.length; i++) {
                YokelSeat seat = order[i];
                logger.debug("seat= {}", seat.getJsonString());
                GamePlayerBoard uiArea = uiAreas[i];
                uiArea.setGameReady(seat.isSeatReady());
            }
            if (game != null) {
                isGameReady = game.isGameReady();
            }
            logger.exit("checkIsGameReady");
        } catch (Exception e) {
            String errorMsg = "Error in checkIsGameReady()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
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
            throw new ReflectionException();
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
            throw new ReflectionException();
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
            throw new ReflectionException();
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
            throw new ReflectionException();
        }
    }

    private void startGame() throws ReflectionException {
        try {
            logger.enter("startGame");
            if (!gameClock.isRunning()) {
                logger.debug("Starting clock now");
                gameClock.start();
                simulatedGame.startGame();
            }
            logger.exit("startGame");
        } catch (Exception e) {
            String errorMsg = "Error in startGame()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
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
            throw new ReflectionException();
        }
    }

    private void handleGameOver(Stage stage, GameManager game) throws ReflectionException {
        try {
            logger.enter("handleGameOver");
            if (game != null && stage != null) {
                if (game.showGameOver()) {
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
            throw new ReflectionException();
        }
    }

    private Actor getGameOverActor(GameManager game) throws ReflectionException {
        try {
            logger.enter("getGameOverActor");
            if (game != null) {
                Array<YokelPlayer> winners = game.getWinners();
                YokelPlayer player1 = getPlayer(winners, 0);
                YokelPlayer player2 = getPlayer(winners, 1);
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
            throw new ReflectionException();
        }
    }

    private YokelPlayer getPlayer(Array<YokelPlayer> players, int index) throws ReflectionException {
        try {
            if (logger.isDebugOn()) {
                ObjectMap<String, Object> map = GdxMaps.newObjectMap();
                map.put("players", players);
                map.put("index", index);
                logger.enter("getPlayer", map);
            }

            YokelPlayer returnedPlayer = null;
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
            throw new ReflectionException();
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
            throw new ReflectionException();
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
                    YokelTable table = sessionService.getCurrentTable();
                    table.getSeat(sessionService.getCurrentSeat()).standUp();
                }
            } catch (InterruptedException e) {
                sessionService.handleException(null, e);
            }
            logger.exit("playerStandUpAction");
        } catch (Exception e) {
            String errorMsg = "Error in playerStandUpAction()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
        }
    }

    private void handlePlayerInput() throws InterruptedException, ReflectionException {
        try {
            logger.enter("handlePlayerInput");
            if (!isGameOver) {
                sessionService.handlePlayerSimulatedInput(simulatedGame);
                if (isUsingServer) {
                    sessionService.handlePlayerInputToServer();
                }
            }
            logger.exit("handlePlayerInput");
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
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
                game.setTable(sessionService.getCurrentTable());
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
            throw new ReflectionException();
        }

    }

    private YokelGameBoardState fetchGameStateFromServer(float delta) throws InterruptedException, ReflectionException {
        try {
            logger.enter("fetchGameStateFromServer");
            //sessionService.asyncGameManagerFromServerRequest();

            GameManager game = null;
            YokelGameBoardState state = null;

            if (isUsingServer) {
                //TODO: Check if received new GameManager, return current simulation if null.
                game = sessionService.asyncGetGameManagerFromServerRequest();
            } else {
                game = simulatedGame;
            }

            //Fetch Game state from Manager
            if (game != null) {
                state = game.getBoardState(1);
            }

            logger.exit("fetchGameStateFromServer", state);
            return state;
        } catch (Exception e) {
            String errorMsg = "Error in setUpDefaultSeats()";
            logger.error(errorMsg, e);
            throw new ReflectionException();
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
            throw new ReflectionException();
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
            YokelPlayer player = sessionService.getCurrentPlayer();
            int currentSeat = sessionService.getCurrentSeat();
            YokelTable table = sessionService.getCurrentTable();

            logger.info("Sitting down at player: {} @Table[{}] @ Seat={}", player, table, seatNumber);
            if (table != null) {
                Array<YokelSeat> seats = table.getSeats();
                logger.debug("seats={}", seats);
                logger.debug("getCurrentSeat={}", currentSeat);
                logger.debug("getCurrentPlayer={}", player);

                if (seats != null) {
                    for (int s = 0; s < seats.size; s++) {
                        logger.debug("seat={}", table.getSeat(s));

                        if (s == seatNumber) {
                            YokelSeat seat = table.getSeat(s);
                            if (seat != null) {
                                sessionService.setCurrentSeat(s);
                                seat.sitDown(player);
                            }
                        }
                        if (s == currentSeat) {
                            YokelSeat seat = table.getSeat(s);
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
            throw new ReflectionException();
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
            throw new ReflectionException();
        }
    }
}