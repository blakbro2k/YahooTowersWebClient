package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.dialog.NextGameController;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.ui.actors.GameBlockGrid;
import asg.games.yokel.client.ui.actors.GameBrokenBlockSpriteContainer;
import asg.games.yokel.client.ui.actors.GameClock;
import asg.games.yokel.client.ui.actors.GameJoinWidget;
import asg.games.yokel.client.ui.actors.GameNameLabel;
import asg.games.yokel.client.ui.actors.GamePowersQueue;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.managers.GameManager;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.objects.YokelBrokenBlock;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelKeyMap;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.objects.YokelSeat;
import asg.games.yokel.objects.YokelTable;
import asg.games.yokel.utils.YokelUtilities;

@View(id = GlobalConstants.UI_BLOCK_TEST_VIEW, value = GlobalConstants.UI_BLOCK_TEST_VIEW_PATH)
public class BlocksUITestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    private static final int BREAK_TIMER_MAX = 106;
    private static final float SCREEN_RADIUS = 600f;
    private static final float YAHOO_DURATION = 1.26f;
    private static final float BLOCK_BREAK_DURATION = 0.65f;
    private static final float BREAK_CHECK_INTERVAL = 4f;
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
    Log4LibGDXLogger logger;

    @LmlActor("Y_block")
    private Image yBlockImage;
    @LmlActor("O_block")
    private Image oBlockImage;
    @LmlActor("K_block")
    private Image kBlockImage;
    @LmlActor("E_block")
    private Image eBlockImage;
    @LmlActor("L_block")
    private Image lBlockImage;
    @LmlActor("bash_block")
    private Image bashBlockImage;
    @LmlActor("Y_block_preview")
    private Image yBlockImagePreview;
    @LmlActor("O_block_preview")
    private Image oBlockImagePreview;
    @LmlActor("K_block_preview")
    private Image kBlockImagePreview;
    @LmlActor("E_block_preview")
    private Image eBlockImagePreview;
    @LmlActor("L_block_preview")
    private Image lBlockImagePreview;
    @LmlActor("bash_block_preview")
    private Image bashBlockImagePreview;
    @LmlActor("power_Y_block_preview")
    private Image poweryBlockImagePreview;
    @LmlActor("power_O_block_preview")
    private Image poweroBlockImagePreview;
    @LmlActor("power_K_block_preview")
    private Image powerkBlockImagePreview;
    @LmlActor("power_E_block_preview")
    private Image powereBlockImagePreview;
    @LmlActor("power_L_block_preview")
    private Image powerlBlockImagePreview;
    @LmlActor("power_bash_block_preview")
    private Image powerbashBlockImagePreview;
    @LmlActor("defense_Y_block")
    private AnimatedImage defenseYBlockImage;
    @LmlActor("defense_O_block")
    private AnimatedImage defenseOBlockImage;
    @LmlActor("defense_K_block")
    private AnimatedImage defenseKBlockImage;
    @LmlActor("defense_E_block")
    private AnimatedImage defenseEBlockImage;
    @LmlActor("defense_L_block")
    private AnimatedImage defenseLBlockImage;
    @LmlActor("defense_bash_block")
    private AnimatedImage defenseBashBlockImage;
    @LmlActor("defense_Y_block_preview")
    private Image defenseYBlockImagePreview;
    @LmlActor("defense_O_block_preview")
    private Image defenseOBlockImagePreview;
    @LmlActor("defense_K_block_preview")
    private Image defenseKBlockImagePreview;
    @LmlActor("defense_E_block_preview")
    private Image defenseEBlockImagePreview;
    @LmlActor("defense_L_block_preview")
    private Image defenseLBlockImagePreview;
    @LmlActor("defense_bash_block_preview")
    private Image defenseBashBlockImagePreview;
    @LmlActor("power_Y_block")
    private AnimatedImage powerYBlockImage;
    @LmlActor("power_O_block")
    private AnimatedImage powerOBlockImage;
    @LmlActor("power_K_block")
    private AnimatedImage powerKBlockImage;
    @LmlActor("power_E_block")
    private AnimatedImage powerEBlockImage;
    @LmlActor("power_L_block")
    private AnimatedImage powerLBlockImage;
    @LmlActor("power_bash_block")
    private AnimatedImage powerBashBlockImage;
    @LmlActor("Y_block_broken")
    private AnimatedImage brokenYBlockImage;
    @LmlActor("O_block_broken")
    private AnimatedImage brokenOBlockImage;
    @LmlActor("K_block_broken")
    private AnimatedImage brokenKBlockImage;
    @LmlActor("E_block_broken")
    private AnimatedImage brokenEBlockImage;
    @LmlActor("L_block_broken")
    private AnimatedImage brokenLBlockImage;
    @LmlActor("bash_block_broken")
    private AnimatedImage brokenBashBlockImage;
    @LmlActor("medusa")
    private AnimatedImage medusaImage;
    @LmlActor("medusa2")
    private AnimatedImage medusa2Image;
    @LmlActor("medusa3")
    private AnimatedImage medusa3Image;
    @LmlActor("top_midas")
    private AnimatedImage topMidasImage;
    @LmlActor("mid_midas")
    private AnimatedImage midMidasImage;
    @LmlActor("bottom_midas")
    private AnimatedImage bottomMidasImage;
    @LmlActor("Y_block_broken_preview")
    private AnimatedImage brokenYBlockImagePreview;
    @LmlActor("O_block_broken_preview")
    private AnimatedImage brokenOBlockImagePreview;
    @LmlActor("K_block_broken_preview")
    private AnimatedImage brokenKBlockImagePreview;
    @LmlActor("E_block_broken_preview")
    private AnimatedImage brokenEBlockImagePreview;
    @LmlActor("L_block_broken_preview")
    private AnimatedImage brokenLBlockImagePreview;
    @LmlActor("bash_block_broken_preview")
    private AnimatedImage brokenBashBlockImagePreview;
    @LmlActor("stone")
    private Image stoneBlockImage;
    @LmlActor("stone")
    private Image stone2BlockImage;
    @LmlActor("gameClock")
    private GameClock gameClock;
    @LmlActor("clear_block")
    private Image clearBlock;
    @LmlActor("clear_block_preview")
    private Image clearBlockPreview;
    @LmlActor("join")
    private GameJoinWidget join;
    @LmlActor("joinReady")
    private GameJoinWidget joinReady;
    @LmlActor("playerOne")
    private GameNameLabel playerOne;
    @LmlActor("playerTwo")
    private GameNameLabel playerTwo;
    @LmlActor("preview")
    private GameBlockGrid previewBoard;
    @LmlActor("player")
    private GameBlockGrid playerBoard;
    @LmlActor("partner")
    private GameBlockGrid partnerBoard;
    @LmlActor("Y_block_breakTest")
    private GameBlock yBlockImageBreakTest;
    @LmlActor("O_block_breakTest")
    private GameBlock oBlockImageBreakTest;
    @LmlActor("K_block_breakTest")
    private GameBlock kBlockImageBreakTest;
    @LmlActor("E_block_breakTest")
    private GameBlock eBlockImageBreakTest;
    @LmlActor("L_block_breakTest")
    private GameBlock lBlockImageBreakTest;
    @LmlActor("bash_block_breakTest")
    private GameBlock bashBlockImageBreakTest;
    @LmlActor("interpolationSelect")
    private VisSelectBox<Object> interpolationSelectBox;
    @LmlActor("1:testBoard")
    private GameBlockGrid gameTestGrid;
    @LmlActor("powersQueue")
    private GamePowersQueue powersQueue;
    @LmlActor("powersQueueTable")
    private Table powersQueueTable;
    @LmlActor("togglePlayer")
    private VisCheckBox togglePlayer;

    GameManager gameManager;

    private boolean toggleYahoo, nextGameDialog, attemptGameStart, isGameReady = false;
    private long nextGame = 0;
    float brokenCheck = BREAK_CHECK_INTERVAL;
    private YokelGameBoard playerBoardData;
    private YokelGameBoard playerBoard2Data;
    private boolean downKeyPressed = false;
    private final YokelKeyMap keyMap = new YokelKeyMap();
    private int breakTimer = -1;
    private static int y;
    private int a;
    private int n;
    private int o;
    private int i;
    private int bash = 0;
    private final Array<GameBrokenBlockSpriteContainer> brokenBlocksQueue = GdxArrays.newArray();
    private final Queue<GameBlock> brokenTestBlock = new Queue<>();
    private final ObjectMap<String, Interpolation> interpolationMap = GdxMaps.newObjectMap();
    private boolean toggleOffensive, togglePlayerBool = false;
    Array<Integer> queuePowers = GdxArrays.newArray();

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        logger.setDebug();
        try {
            logger.enter("initialize");
            y = yBlockImageBreakTest.getBlock();
            a = oBlockImageBreakTest.getBlock();
            n = kBlockImageBreakTest.getBlock();
            o = eBlockImageBreakTest.getBlock();
            i = lBlockImageBreakTest.getBlock();
            bash = bashBlockImageBreakTest.getBlock();

            brokenTestBlock.addLast(yBlockImageBreakTest);
            brokenTestBlock.addLast(oBlockImageBreakTest);
            brokenTestBlock.addLast(kBlockImageBreakTest);
            brokenTestBlock.addLast(eBlockImageBreakTest);
            brokenTestBlock.addLast(lBlockImageBreakTest);
            brokenTestBlock.addLast(bashBlockImageBreakTest);

            interpolationMap.put("bounce", Interpolation.bounce);
            interpolationMap.put("bounceIn", Interpolation.bounceIn);
            interpolationMap.put("bounceOut", Interpolation.bounceOut);
            interpolationMap.put("circle", Interpolation.circle);
            interpolationMap.put("circleIn", Interpolation.circleIn);
            interpolationMap.put("circleOut", Interpolation.circleOut);
            interpolationMap.put("elastic", Interpolation.elastic);
            interpolationMap.put("elasticIn", Interpolation.elasticIn);
            interpolationMap.put("elasticOut", Interpolation.elasticOut);
            interpolationMap.put("exp5", Interpolation.exp5);
            interpolationMap.put("exp5In", Interpolation.exp5In);
            interpolationMap.put("exp5Out", Interpolation.exp5Out);
            interpolationMap.put("exp10", Interpolation.exp10);
            interpolationMap.put("exp10In", Interpolation.exp10In);
            interpolationMap.put("exp10Out", Interpolation.exp10Out);
            interpolationMap.put("fade", Interpolation.fade);
            interpolationMap.put("fastSlow", Interpolation.fastSlow);
            interpolationMap.put("linear", Interpolation.linear);
            interpolationMap.put("pow2", Interpolation.pow2);
            interpolationMap.put("pow2In", Interpolation.pow2In);
            interpolationMap.put("pow2InInverse", Interpolation.pow2InInverse);
            interpolationMap.put("pow2Out", Interpolation.pow2Out);
            interpolationMap.put("pow2OutInverse", Interpolation.pow2OutInverse);
            interpolationMap.put("pow3", Interpolation.pow3);
            interpolationMap.put("pow3In", Interpolation.pow3In);
            interpolationMap.put("pow3InInverse", Interpolation.pow3InInverse);
            interpolationMap.put("pow3Out", Interpolation.pow3Out);
            interpolationMap.put("pow3OutInverse", Interpolation.pow3OutInverse);
            interpolationMap.put("pow4", Interpolation.pow4);
            interpolationMap.put("pow4In", Interpolation.pow4In);
            interpolationMap.put("pow4Out", Interpolation.pow4Out);
            interpolationMap.put("pow5", Interpolation.pow5);
            interpolationMap.put("pow5In", Interpolation.pow5In);
            interpolationMap.put("pow5Out", Interpolation.pow5Out);
            interpolationMap.put("sine", Interpolation.sine);
            interpolationMap.put("sineIn", Interpolation.sineIn);
            interpolationMap.put("sineOut", Interpolation.sineOut);
            interpolationMap.put("slowFast", Interpolation.slowFast);
            interpolationMap.put("smooth", Interpolation.smooth);
            interpolationMap.put("smooth2", Interpolation.smooth2);
            interpolationMap.put("smoother", Interpolation.smoother);
            interpolationMap.put("swing", Interpolation.swing);
            interpolationMap.put("swingIn", Interpolation.swingIn);
            interpolationMap.put("swingOut", Interpolation.swingOut);

            initiate();
/*
            powersQueueTable.add(getClearBlock()).row();
            powersQueueTable.add(getClearBlock()).row();
            powersQueueTable.add(getClearBlock()).row();
            powersQueueTable.add(getClearBlock()).row();
            powersQueueTable.add(getClearBlock()).row();
            powersQueueTable.add(getClearBlock()).row();
            powersQueueTable.add(getClearBlock()).row();
            powersQueueTable.add(getClearBlock()).row();
*/
            System.out.println("start: " + powersQueueTable);
            //powersQueueTable.add(getClearBlock()).row();
            //powersQueueTable.clear();
            System.out.println("end: " + powersQueueTable);
            togglePlayerBool = togglePlayer.isChecked();
            playerBoard.setActive(togglePlayerBool);

            //initializeGameOverLabels(stage);
            logger.exit("initialize");

        } catch (Exception e) {
            String errorMsg = "Error in initialize()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    private GameBlock getClearBlock() {
        GameBlock gBlock = UIUtil.getBlock(YokelBlock.Y_BLOCK, false);
        System.out.println("gBlock: " + gBlock);
        return UIUtil.getBlock(YokelBlock.Y_BLOCK, false);
    }

    private void triggerGameOverLabels(Stage stage, String winner1, String winner2) {
        TypingLabel gameOverLabel = new TypingLabel("{SLOW}{EASE=-500;30.1;0}Game Over", interfaceService.getSkin());
        TypingLabel youWinLabel = new TypingLabel("{SLOW}{EASE=-500;30.1;0}You Win!", interfaceService.getSkin());
        TypingLabel youLoseLabel = new TypingLabel("{SLOW}{EASE=-500;30.1;0}You Lose!", interfaceService.getSkin());
        TypingLabel congratulationsLabel = new TypingLabel("{SLOW}{EASE=-500;30.1;0}Congratulations", interfaceService.getSkin());
        gameOverLabel.setX(stage.getWidth() / 2);
        gameOverLabel.setY(stage.getHeight() / 2);
        youWinLabel.setX(stage.getWidth() / 2);
        youWinLabel.setY(stage.getHeight() / 2 - youWinLabel.getHeight());
        congratulationsLabel.setX(stage.getWidth() / 2);
        congratulationsLabel.setY(stage.getHeight() / 2 - youWinLabel.getHeight() - congratulationsLabel.getHeight());

        stage.addActor(gameOverLabel);
        stage.addActor(youWinLabel);
        stage.addActor(congratulationsLabel);
    }

    @Override
    public void destroy(ViewController viewController) {
        try {
            logger.enter("destroy");
            //boardState.dispose();
            gameManager.dispose();
            logger.exit("destroy");
        } catch (Exception e) {
            String errorMsg = "Error in destroy()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    private void initiate() throws ReflectionException {
        try {
            logger.enter("initiate");
            initiateActors();
            YokelPlayer player = new YokelPlayer();
            player.setName("player1");
            player.setRating(2000);
            player.setIcon(6);
            YokelPlayer player2 = new YokelPlayer();
            player2.setName("player2");
            player2.setRating(2000);
            player2.setIcon(6);
            logger.info("Set Yokelplayer: {}", player);

            YokelTable table = new YokelTable("1", 1);
            Array<YokelSeat> seats = table.getSeats();
            seats.get(0).sitDown(player);
            seats.get(2).sitDown(player2);
            table.setSeats(seats);

            gameManager = new GameManager(table);
            gameManager.startGame();

            //previewBoard.sitPlayerDown(player);
            //previewBoard.setGameReady(true);
            //previewBoard.setIsPlayerReady(true);
            //previewBoard.hideJoinButton();
            //previewBoard.setName("1");


            playerBoardData = new YokelGameBoard(1);
            playerBoard2Data = new YokelGameBoard(1);
            playerBoardData.begin();
            playerBoard2Data.end();

            playerBoardData.setPartnerBoard(playerBoard2Data, true);
            playerBoard2Data.setPartnerBoard(playerBoardData, false);

            //playerB.setNextPiece();
            playerBoard.setPreview(false);
            playerBoard.setActive(togglePlayerBool);
            playerBoard.setPlayerView(true);
            playerBoard.renderBoard(playerBoardData.getGameState());

            //playerBoard.sitPlayerDown(player);
            //playerBoard.setGameReady(true);
            //playerBoard.setIsPlayerReady(true);
            //playerBoard.hideJoinButton();
            playerBoard.setName("2");
            joinReady.setIsGameReady(true);

            logger.exit("initiate");
        } catch (Exception e) {
            String errorMsg = "Issue in initiate()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    private void initiateActors() throws ReflectionException {
        try {
            logger.enter("initiateActors");
            uiService.loadDrawable(yBlockImage);
            uiService.loadDrawable(oBlockImage);
            uiService.loadDrawable(kBlockImage);
            uiService.loadDrawable(eBlockImage);
            uiService.loadDrawable(lBlockImage);
            uiService.loadDrawable(bashBlockImage);
            uiService.loadDrawable(defenseYBlockImage);
            uiService.loadDrawable(defenseOBlockImage);
            uiService.loadDrawable(defenseKBlockImage);
            uiService.loadDrawable(defenseEBlockImage);
            uiService.loadDrawable(defenseLBlockImage);
            uiService.loadDrawable(defenseBashBlockImage);
            uiService.loadDrawable(powerYBlockImage);
            uiService.loadDrawable(powerOBlockImage);
            uiService.loadDrawable(powerKBlockImage);
            uiService.loadDrawable(powerEBlockImage);
            uiService.loadDrawable(powerLBlockImage);
            uiService.loadDrawable(powerBashBlockImage);
            uiService.loadDrawable(brokenYBlockImage);
            uiService.loadDrawable(brokenOBlockImage);
            uiService.loadDrawable(brokenKBlockImage);
            uiService.loadDrawable(brokenEBlockImage);
            uiService.loadDrawable(brokenLBlockImage);
            uiService.loadDrawable(brokenBashBlockImage);
            uiService.loadDrawable(stoneBlockImage);
            uiService.loadDrawable(stone2BlockImage);
            uiService.loadDrawable(clearBlock);
            uiService.loadDrawable(clearBlockPreview);
            uiService.loadDrawable(yBlockImagePreview);
            uiService.loadDrawable(oBlockImagePreview);
            uiService.loadDrawable(kBlockImagePreview);
            uiService.loadDrawable(eBlockImagePreview);
            uiService.loadDrawable(lBlockImagePreview);
            uiService.loadDrawable(bashBlockImagePreview);
            uiService.loadDrawable(poweryBlockImagePreview);
            uiService.loadDrawable(powerkBlockImagePreview);
            uiService.loadDrawable(poweroBlockImagePreview);
            uiService.loadDrawable(powereBlockImagePreview);
            uiService.loadDrawable(powerlBlockImagePreview);
            uiService.loadDrawable(powerbashBlockImagePreview);
            uiService.loadDrawable(defenseYBlockImagePreview);
            uiService.loadDrawable(defenseOBlockImagePreview);
            uiService.loadDrawable(defenseKBlockImagePreview);
            uiService.loadDrawable(defenseEBlockImagePreview);
            uiService.loadDrawable(defenseLBlockImagePreview);
            uiService.loadDrawable(defenseBashBlockImagePreview);
            uiService.loadDrawable(medusaImage);
            medusa2Image.setName("medusa");
            uiService.loadDrawable(medusa2Image);
            medusa3Image.setName("medusa");
            uiService.loadDrawable(medusa3Image);
            uiService.loadDrawable(topMidasImage);
            uiService.loadDrawable(midMidasImage);
            uiService.loadDrawable(bottomMidasImage);
            uiService.loadDrawable(brokenYBlockImagePreview);
            uiService.loadDrawable(brokenOBlockImagePreview);
            uiService.loadDrawable(brokenKBlockImagePreview);
            uiService.loadDrawable(brokenEBlockImagePreview);
            uiService.loadDrawable(brokenLBlockImagePreview);
            uiService.loadDrawable(brokenBashBlockImagePreview);
            if (interpolationSelectBox != null) {
                ObjectMap.Keys<String> keys = interpolationMap.keys();
                Array<String> itemsArray = keys.toArray();
                //interpolationSelectBox.
                String[] items = YokelUtilities.toStringArray(itemsArray);
                interpolationSelectBox.setItems((Object[]) items);
                interpolationSelectBox.setSelected("sineIn");
            }
            logger.exit("initiateActors");
        } catch (Exception e) {
            String errorMsg = "Error in initiateActors()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @Override
    public void render(Stage stage, float delta) {
        try {
            if (breakTimer == 0) {
                yBlockImageBreakTest.setBlock(y);
                oBlockImageBreakTest.setBlock(a);
                kBlockImageBreakTest.setBlock(n);
                eBlockImageBreakTest.setBlock(o);
                lBlockImageBreakTest.setBlock(i);
                bashBlockImageBreakTest.setBlock(bash);

                brokenTestBlock.addLast(yBlockImageBreakTest);
                brokenTestBlock.addLast(oBlockImageBreakTest);
                brokenTestBlock.addLast(kBlockImageBreakTest);
                brokenTestBlock.addLast(eBlockImageBreakTest);
                brokenTestBlock.addLast(lBlockImageBreakTest);
                brokenTestBlock.addLast(bashBlockImageBreakTest);
            }
            --breakTimer;


            if (brokenBlocksQueue.size > 0 && --brokenCheck == 0) {
                brokenCheck = BREAK_CHECK_INTERVAL;
                addBrokenBlockActorToStage(stage);
            }

            //logger.enter("render");
            checkGameStart();

            /*YokelGameBoardState state = gameManager.getBoardState(0);
            YokelGameBoardState state = playerBoard2Data.getGameState();

            if(state != null) {
            }*/
            gameTestGrid.renderBoard(playerBoard2Data.getGameState());
            gameManager.update(delta);

            /*if(state2 != null) {
                YokelGameBoardState state = playerBoardData.getGameState();
                gamePlayerBoard.renderPlayerBoard(state);
            }*/
            playerBoardData.update(delta);
            playerBoard2Data.update(delta);

            //System.out.println(playerBoardData.getGameState());

            Iterable<YokelBrokenBlock> cellsBroken = playerBoardData.getGameState().getBrokenCells();

            //if(cellsBroken.size() > 0) {
            for (YokelBrokenBlock cellBroken : cellsBroken) {

                GameBlock gameBlock = UIUtil.getInstance().getGameBlock(cellBroken.getBlock(), false);
                //addBrokenBlockActorToQueue(gameBlock, gameTestGrid, cellBroken.getRow(), cellBroken.getCol());
            }
            //}
            playerBoard.renderBoard(playerBoardData.getGameState());

            //Handle Player input
            //handlePlayerSimulatedInput(playerBoardData);

            //showGameOver(stage);
            //System.out.println("Stage:\n" + stage.getActors());
            //System.out.println("Stage:\n" + stage.getRoot().getClass());

            stage.act(delta);
            stage.draw();
            // logger.exit("render");
        } catch (Exception e) {
            String errorMsg = "Error in render()";
            logger.error(errorMsg, e);
            sessionService.showError(e);
        }
    }

    private void addBrokenBlockActorToStage(Stage stage) {
        if (stage != null) {
            Array.ArrayIterator<GameBrokenBlockSpriteContainer> brokenIterator = brokenBlocksQueue.iterator();

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
                            Vector2 pos = parent.getParent().localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                            Vector2 ppos = parent.getParent().parentToLocalCoordinates(new Vector2(blockV.x, blockV.y));
                            Vector2 ppos1 = parent.getParent().localToScreenCoordinates(new Vector2(parent.getX(), parent.getY()));
                            logger.info("p.l2p pos: ({},{})", pos.x, pos.y);
                            logger.info("pp.p2l pos: ({},{})", ppos.x, ppos.y);
                            logger.info("pp.l2s pos: ({},{})", ppos1.x, ppos1.y);
                            logger.info("parent pos: ({},{})", parent.getX(), parent.getY());
                            logger.info("blockV pos: ({},{})", blockV.x, blockV.y);
                            //logger.info("parent pos: ({},{})", parent.getParent(), parent.getParent().getClass());

                            //Get the screen coordinates
                            Vector2 blockV2 = left.localToScreenCoordinates(new Vector2(blockV.x, blockV.y));
                            //Shift block x slightly
                            //blockV2.x -= parent.getX();
                            //blockV2.x -= brokenOffSet;
                            //Shift (and flip) block y
                            blockV2.x -= parent.getX();
                            blockV2.y = 1 - (blockV2.y) + stage.getHeight();

                            block.setVisible(true);
                            block.setBounds(ppos1.x, ppos1.y, block.getWidth(), block.getHeight());
                            //block.setPosition(ppos1.x / 2, ppos1.y / 2);

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


                        //Interpolation interpolation = Interpolation.pow3In;
                        Interpolation interpolation = interpolationMap.get(YokelUtilities.otos(interpolationSelectBox.getSelected()));

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

    public void handlePlayerSimulatedInput(YokelGameBoard game) throws ReflectionException {
        try {
            logger.enter("handleLocalPlayerInput");

            //TODO: Remove, moves test player's key to the right
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                logger.debug("Handling Z: " + Input.Keys.Z);
                logger.debug("Adding Midas");
                game.addSpecialPiece(2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                logger.debug("Handling X: " + Input.Keys.Z);
                logger.debug("Adding Medusa");
                game.addSpecialPiece(1);
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getRightKey())) {
                logger.debug("Player input key: " + keyMap.getRightKey());
                logger.debug("Moving right");
                game.movePieceRight();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getLeftKey())) {
                logger.debug("Player input key: " + keyMap.getLeftKey());
                logger.debug("Moving Left");
                game.movePieceLeft();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getCycleDownKey())) {
                logger.debug("Player input key: " + keyMap.getCycleDownKey());
                logger.debug("Cycle down");
                game.cycleDown();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getCycleUpKey())) {
                logger.debug("Player input key: " + keyMap.getCycleUpKey());
                logger.debug("Cycle Up");
                game.cycleUp();
            }
            if (Gdx.input.isKeyPressed(keyMap.getDownKey())) {
                if (!downKeyPressed) {
                    downKeyPressed = true;
                }
                logger.debug("Player input key: " + keyMap.getDownKey());
                logger.debug("Moving Down");
                game.startMoveDown();
            }
            if (!Gdx.input.isKeyPressed(keyMap.getDownKey())) {
                downKeyPressed = false;
                logger.debug("Player input key: " + keyMap.getRightKey());
                logger.debug("Stop Moving Down");
                game.stopMoveDown();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getRandomAttackKey())) {
                //game.handleRandomAttack(currentSeat);
                logger.debug("Player input key: " + keyMap.getRightKey());
                logger.debug("Adding Midas");
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
        } catch (Exception e) {
            String errorMsg = "Error in handlePlayerSimulatedInput()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    private void checkGameStart() throws ReflectionException {
        try {
            //logger.enter("checkGameStart");
            if (gameClock == null) return;

            if (attemptGameStart) {
                if (!nextGameDialog) {
                    nextGameDialog = true;
                    interfaceService.showDialog(NextGameController.class);
                    nextGame = TimeUtils.millis();
                }

                if (getElapsedSeconds() > NextGameController.NEXT_GAME_SECONDS) {
                    interfaceService.destroyDialog(NextGameController.class);
                    attemptGameStart = false;
                    startGame();
                }
            }
            //logger.exit("checkGameStart");
        } catch (Exception e) {
            String errorMsg = "Error in checkGameStart()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
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
            logger.exit("toggleGameStart");
        } catch (Exception e) {
            String errorMsg = "Error in toggleGameStart()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("breakBlocks")
    private void breakBlocks() throws ReflectionException {
        try {
            logger.enter("breakBlocks");

            if (breakTimer < 0) {
                while (brokenTestBlock.size > 0) {
                    GameBlock gameBlock = brokenTestBlock.removeFirst();
                    addBrokenBlockActorToQueue(gameBlock, null, -1, -1);
                }
                breakTimer = BREAK_TIMER_MAX;
            }
            logger.exit("breakBlocks");
        } catch (Exception e) {
            String errorMsg = "Error in breakBlocks()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    private void addBrokenBlockActorToQueue(GameBlock gameBlock, GameBlockGrid grid, int row, int col) {
        if (gameBlock != null) {
            GameBrokenBlockSpriteContainer brokenSprite = UIUtil.getBrokenBlockSprites(gameBlock, gameBlock.getBlock());
            //brokenSprite.setGrid(grid);
            //brokenSprite.setRow(row);
            //brokenSprite.setCol(col);
            brokenBlocksQueue.add(brokenSprite);
        }
    }

    @LmlAction("stopClock")
    private void stopClock() throws ReflectionException {
        try {
            logger.enter("stopClock");
            if (nextGameDialog) {
                nextGameDialog = false;
            }
            stopGame();
            logger.exit("stopClock");
        } catch (Exception e) {
            String errorMsg = "Error in stopClock()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    int getElapsedSeconds() throws ReflectionException {
        try {
            //logger.enter("getElapsedSeconds");

            int elapsedSeconds = YokelUtilities.otoi(((TimeUtils.millis() - nextGame) / 1000));
            //logger.exit("getElapsedSeconds");
            return elapsedSeconds;
        } catch (Exception e) {
            String errorMsg = "Error in getElapsedSeconds()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    private void startGame() throws ReflectionException {
        try {
            //logger.enter("startGame");
            if (!gameClock.isRunning()) {
                gameClock.start();
            }
            //logger.exit("startGame");
        } catch (Exception e) {
            String errorMsg = "Error in startGame()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }

    }

    private void stopGame() throws ReflectionException {
        try {
            logger.enter("stopGame");
            if (gameClock.isRunning()) {
                gameClock.stop();
            }
            logger.exit("stopGame");
        } catch (Exception e) {
            String errorMsg = "Error in stopGame()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }

    }

    @LmlAction("toggleYahoo")
    private void toggleYahoo() throws ReflectionException {
        try {
            logger.enter("toggleYahoo");
            toggleYahoo = !toggleYahoo;
            if (playerOne != null) {
                playerOne.setYahoo(toggleYahoo);
            }
            if (playerTwo != null) {
                playerTwo.setYahoo(toggleYahoo);
            }
            logger.exit("toggleYahoo");
        } catch (Exception e) {
            String errorMsg = "Error in toggleYahoo()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("toggleOffensive")
    private void toggleOffensive() throws ReflectionException {
        try {
            logger.enter("toggleOffensive");
            toggleOffensive = !toggleOffensive;
            logger.exit("toggleOffensive");
        } catch (Exception e) {
            String errorMsg = "Error in toggleOffensive()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("togglePlayer")
    private void togglePlayer(final Actor actor) throws ReflectionException {
        try {
            logger.enter("togglePlayer");
            togglePlayerBool = !togglePlayerBool;
            logger.info("togglePlayerBool={}", togglePlayerBool);
            playerBoard.setActive(togglePlayerBool);
            logger.exit("togglePlayer");
        } catch (Exception e) {
            String errorMsg = "Error in togglePlayer()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("addYAttack")
    private void addYAttack() throws ReflectionException {
        try {
            logger.enter("addYAttack");
            int block = 0;

            if (toggleOffensive) {
                block = YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(block, 3));
            } else {
                block = YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(block, 4));
            }
            queuePowers.add(block);
            //powersQueue.setPowers(queuePowers);
            //String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YokelBlockEval.addBrokenFlag(block));

            playerBoard2Data.handlePower(block);
            System.out.println(playerBoard2Data);
            logger.exit("addYAttack");
        } catch (Exception e) {
            String errorMsg = "Error in addYAttack()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("resetAllBoards")
    private void resetAllBoards() throws ReflectionException {
        try {
            logger.enter("resetAllBoards");
            join.setIsGameReady(false);
            join.setIsPlayerReady(false);
            join.setSeated(false);
            joinReady.setIsPlayerReady(false);
            joinReady.setIsGameReady(true);
            joinReady.setSeated(false);
            try {
                initiate();
            } catch (ReflectionException e) {
                logger.error("init error", e);
            }
            logger.exit("resetAllBoards");
        } catch (Exception e) {
            String errorMsg = "Error in resetAllBoards()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    private Array<Vector2> getPolygonVertices(int n, float radius, int h, int k) {
        //logger.error("n={}",n);
        //logger.error("radius={}",radius);
        Array<Vector2> verts = GdxArrays.newArray();
        double angle_between_vertices = 2 * Math.PI / n;

        for (int i = n; i >= 0; i--) {
            //logger.error("n={}",n);
            double theta = i * angle_between_vertices;
            double x = h + radius * Math.cos(theta);
            double y = k + radius * Math.sin(theta);
            //logger.error("theta={}",theta);
            //logger.error("x={}",x);
            //logger.error("y={}",y);
            verts.add(new Vector2((float) x, (float) y));
        }
        return verts;
    }
}