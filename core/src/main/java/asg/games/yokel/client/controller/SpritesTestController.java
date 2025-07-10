package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import asg.games.yipee.libgdx.game.YipeeBlockEvalGDX;
import asg.games.yipee.libgdx.game.YipeeGameBoardGDX;
import asg.games.yipee.libgdx.objects.YipeeBrokenBlockGDX;
import asg.games.yipee.libgdx.objects.YipeeGameBoardStateGDX;
import asg.games.yipee.libgdx.objects.YipeeKeyMapGDX;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.dialog.GameOverDialogController;
import asg.games.yokel.client.controller.dialog.NextGameController;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.game.ClientGameManager;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.ui.actors.GameBlockGrid;
import asg.games.yokel.client.ui.actors.GameBrokenBlockSpriteContainer;
import asg.games.yokel.client.ui.actors.GameClock;
import asg.games.yokel.client.ui.actors.GameJoinWidget;
import asg.games.yokel.client.ui.actors.GameNameLabel;
import asg.games.yokel.client.ui.actors.GamePlayerBoard;
import asg.games.yokel.client.ui.actors.GamePowersQueue;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.client.utils.YokelUtilities;

@View(id = GlobalConstants.SPRITES_TEST_VIEW, value = GlobalConstants.SPRITES_TEST_VIEW_PATH)
public class SpritesTestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    Log4LibGDXLogger logger;

    private static final int BREAK_TIMER_MAX = 106;
    private static final float SCREEN_RADIUS = 600f;
    private static final float YAHOO_DURATION = 1.26f;
    private static final float BLOCK_BREAK_DURATION = 0.65f;
    private static final float BREAK_CHECK_INTERVAL = 4f;

    @Inject
    private UserInterfaceService uiService;
    @Inject
    private LoggerService loggerService;
    @Inject
    private SessionService sessionService;
    @Inject
    private InterfaceService interfaceService;

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
    @LmlActor("testGameBoard")
    private GamePlayerBoard testGameBoard;
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
    @LmlActor("powersQueue")
    private GamePowersQueue powersQueue;
    @LmlActor("togglePlayer")
    private VisCheckBox togglePlayer;

    ClientGameManager gameManager;

    long gameOverDialogTimestamp = 0;
    private boolean toggleYahoo, nextGameDialog, attemptGameStart, isGameReady = false;
    private long nextGame = 0;
    float brokenCheck = BREAK_CHECK_INTERVAL;
    private YipeeGameBoardGDX playerBoardData;
    private YipeeGameBoardGDX playerBoard2Data;
    private YipeeGameBoardGDX testBoardData;
    private boolean downKeyPressed = false;
    private final YipeeKeyMapGDX keyMap = new YipeeKeyMapGDX();
    private int breakTimer = -1;
    private int y;
    private int a;
    private int n;
    private int o;
    private int i;
    private int bash = 0;
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue1 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue2 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue3 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue4 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue5 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue6 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue7 = new OrderedSet<>();
    private final OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue8 = new OrderedSet<>();
    private final Queue<GameBlock> brokenTestBlock = new Queue<>();
    private final ObjectMap<String, Interpolation> interpolationMap = GdxMaps.newObjectMap();
    private boolean toggleOffensive, togglePlayerBool = true;
    Queue<Integer> queuePowers = new Queue<>();
    private final long gameSeed = 5;

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

            String[] prefixes = new String[]{
                    "Y_block_broken", "O_block_broken", "K_block_broken",
                    "E_block_broken", "L_block_broken", "bash_block_broken"
            };

            for (String prefix : prefixes) {
                try {
                    uiService.loadDrawable(UIUtil.getInstance().getBlockImage(prefix + "_left"));
                    uiService.loadDrawable(UIUtil.getInstance().getBlockImage(prefix + "_bottom"));
                    uiService.loadDrawable(UIUtil.getInstance().getBlockImage(prefix + "_right"));
                } catch (Exception e) {
                    //log.error("Failed to load broken block part for: " + prefix, e);
                }
            }

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

            togglePlayer.setChecked(togglePlayerBool);
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
            //gameManager.dispose();
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

            YipeePlayerGDX player1 = new YipeePlayerGDX("player1");
            YipeePlayerGDX player2 = new YipeePlayerGDX("player2");
            YipeePlayerGDX player3 = new YipeePlayerGDX("player3");
            ObjectMap<String, Object> arguments = GdxMaps.newObjectMap();
            arguments.put(YipeeTableGDX.ARG_TYPE, YipeeTableGDX.ENUM_VALUE_PRIVATE);
            arguments.put(YipeeTableGDX.ARG_RATED, true);
            YipeeTableGDX table = new YipeeTableGDX(1, arguments);

            table.getSeat(0).sitDown(player1);
            table.getSeat(0).setSeatReady(true);
            table.getSeat(1).sitDown(player2);
            table.getSeat(1).setSeatReady(true);
            table.getSeat(2).sitDown(player3);
            table.getSeat(2).setSeatReady(true);
            //table.setSeats(seats);


            gameManager = new ClientGameManager();
            gameManager.initialize(gameSeed, 2);
            gameManager.resetGameBoards();
            gameManager.startGameLoop();

            /*
            playerBoardData = new YipeeGameBoardGDX(gameSeed);
            playerBoard2Data = new YipeeGameBoardGDX(gameSeed);
            testBoardData = new YipeeGameBoardGDX(gameSeed);
            playerBoardData.begin();
            playerBoard2Data.begin();

            playerBoardData.setPartnerBoard(playerBoard2Data, true);
            playerBoard2Data.setPartnerBoard(playerBoardData, false);
            testBoardData.setPartnerBoard(playerBoardData, false);
            */

            //playerB.setNextPiece();
            playerBoard.setPreview(false);
            playerBoard.setActive(togglePlayerBool);
            playerBoard.setPlayerView(true);
            //playerBoard.renderBoard(playerBoardData.getGameState());

            previewBoard.setPreview(true);
            previewBoard.setActive(true);
            previewBoard.setPlayerView(false);
            //previewBoard.renderBoard(playerBoard2Data.getGameState());


            testGameBoard.setPreview(false);
            testGameBoard.setActive(true);
            testGameBoard.setPlayerView(true);
            //playerBoard.setName("2");
            //previewBoard.setName("5");
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

            YipeeGameBoardStateGDX state1 = UIUtil.getYipeeBoardState(gameManager, 0);
            if (state1 != null) {
                for (YipeeBrokenBlockGDX cell : state1.getBrokenCells()) {
                    GameBlock block = UIUtil.getInstance().getGameBlock(cell.getBlock(), playerBoard.isPreview());
                    addBrokenBlockActorToQueue(brokenBlocksQueue1, block, playerBoard, cell.getRow(), cell.getCol());
                }
            }
            YipeeGameBoardStateGDX state2 = UIUtil.getYipeeBoardState(gameManager, 1);
            if (state2 != null) {
                for (YipeeBrokenBlockGDX cell : state2.getBrokenCells()) {
                    GameBlock block = UIUtil.getInstance().getGameBlock(cell.getBlock(), previewBoard.isPreview());
                    addBrokenBlockActorToQueue(brokenBlocksQueue2, block, previewBoard, cell.getRow(), cell.getCol());
                }
            }
            YipeeGameBoardStateGDX state3 = UIUtil.getYipeeBoardState(gameManager, 2);
            boolean localYahoo = false;
            if (state3 != null) {
                localYahoo = state3.getYahooDuration() > 0;
                for (YipeeBrokenBlockGDX cell : state3.getBrokenCells()) {
                    GameBlock block = UIUtil.getInstance().getGameBlock(cell.getBlock(), testGameBoard.isPreview());
                    addBrokenBlockActorToQueue(brokenBlocksQueue3, block, testGameBoard.getGrid(), cell.getRow(), cell.getCol());
                }
            }

            // Animate broken blocks if timer triggers
            if (--brokenCheck == 0) {
                animateBrokenBlocks(stage, brokenBlocksQueue1, toggleYahoo);
                animateBrokenBlocks(stage, brokenBlocksQueue2, toggleYahoo);
                animateBrokenBlocks(stage, brokenBlocksQueue3, localYahoo);
                animateBrokenBlocks(stage, brokenBlocksQueue4, toggleYahoo);
                animateBrokenBlocks(stage, brokenBlocksQueue5, toggleYahoo);
                animateBrokenBlocks(stage, brokenBlocksQueue6, toggleYahoo);
                animateBrokenBlocks(stage, brokenBlocksQueue7, toggleYahoo);
                animateBrokenBlocks(stage, brokenBlocksQueue8, toggleYahoo);
                brokenCheck = BREAK_CHECK_INTERVAL;
            }

            checkGameStart();

            playerBoard.renderBoard(state1);
            previewBoard.renderBoard(state2);
            testGameBoard.renderPlayerBoard(state3);
            powersQueue.updatePowersQueue(queuePowers);

            gameManager.update(delta);


            stage.act(delta);
            stage.draw();

        } catch (Exception e) {
            String errorMsg = "Error in render()";
            logger.error(errorMsg, e);
            sessionService.showError(e);
        }
    }

    @LmlAction("showGameOver")
    public void showGameOver() {
        interfaceService.showDialog(GameOverDialogController.class);
    }

    private void animateBrokenBlocks(Stage stage, OrderedSet<GameBrokenBlockSpriteContainer> queue, boolean isYahoo) {
        if (queue == null || queue.size == 0) return;

        if (isYahoo) {
            UIUtil.animateYahooBlockBreak(stage, queue, 1.26f);
        } else {
            //UIUtil.animateStandardBlockBreak(stage, queue);
            UIUtil.animateStandardBlockBreak(stage, queue, interpolationMap.get(YokelUtilities.otos(interpolationSelectBox.getSelected())));
        }
    }

    private void handleBrokenBlocks(ClientGameManager game, Stage stage) throws Exception {
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

    private void addBrokenBlockActorToStage(Stage stage, OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue) {
        if (stage != null && brokenBlocksQueue != null) {
            ObjectSet.ObjectSetIterator<GameBrokenBlockSpriteContainer> brokenIterator = brokenBlocksQueue.iterator();

            if (toggleYahoo) {
                UIUtil.animateYahooBlockBreak(stage, brokenBlocksQueue, YAHOO_DURATION);
                //logger.error("size: " + brokenBlocksQueue.size);
            } else {
                UIUtil.animateStandardBlockBreak(stage, brokenBlocksQueue, interpolationMap.get(YokelUtilities.otos(interpolationSelectBox.getSelected())));
            }
            //logger.info("{}: ({},{})", "stage", stage.getWidth(), stage.getHeight());
            //logger.exit("addBrokenBlockActorToStage");
        }
    }

    public void handlePlayerSimulatedInput(YipeeGameBoardGDX game) throws ReflectionException {
        try {
            //logger.enter("handleLocalPlayerInput");

            //TODO: Remove, moves test player's key to the right
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                // logger.debug("Handling Z: ", Input.Keys.Z);
                // logger.debug("Adding Midas");
                game.addSpecialPiece(2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                // logger.debug("Handling X: ", Input.Keys.Z);
                // logger.debug("Adding Medusa");
                game.addSpecialPiece(1);
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getRightKey())) {
                // logger.debug("Player input key: ", keyMap.getRightKey());
                // logger.debug("Moving right");
                game.movePieceRight();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getLeftKey())) {
                // logger.debug("Player input key: ", keyMap.getLeftKey());
                // logger.debug("Moving Left");
                game.movePieceLeft();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getCycleDownKey())) {
                // logger.debug("Player input key: ", keyMap.getCycleDownKey());
                // logger.debug("Cycle down");
                game.cycleDown();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getCycleUpKey())) {
                // logger.debug("Player input key: ", keyMap.getCycleUpKey());
                // logger.debug("Cycle Up");
                game.cycleUp();
            }
            if (Gdx.input.isKeyPressed(keyMap.getDownKey())) {
                if (!downKeyPressed) {
                    downKeyPressed = true;
                }
                // logger.debug("Player input key: ", keyMap.getDownKey());
                // logger.debug("Moving Down");
                game.startMoveDown();
            }
            if (!Gdx.input.isKeyPressed(keyMap.getDownKey())) {
                downKeyPressed = false;
                // logger.debug("Player input key: ", keyMap.getRightKey());
                // logger.debug("Stop Moving Down");
                game.stopMoveDown();
            }
            if (Gdx.input.isKeyJustPressed(keyMap.getRandomAttackKey())) {
                //game.handleRandomAttack(currentSeat);
                // logger.debug("Player input key: ", keyMap.getRightKey());
                //logger.debug("Adding Midas");
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
            //logger.exit("handleLocalPlayerInput");
        } catch (Exception e) {
            String errorMsg = "Error in handlePlayerInput()";
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
                    //interfaceService.destroyDialog(NextGameController.class);
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

    @LmlAction("toggleGameOverOnePlayerWins")
    private void toggleGameOverOnePlayerWins() throws ReflectionException {
        try {
            logger.enter("toggleGameOverOnePlayerWins");
            sessionService.setWinner(true);
            sessionService.setPartnered(false);
            interfaceService.showDialog(GameOverDialogController.class);
            logger.exit("toggleGameOverOnePlayerWins");
        } catch (Exception e) {
            String errorMsg = "Error in toggleGameOverOnePlayerWins()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("toggleGameOverTwoPlayerWins")
    private void toggleGameOverTwoPlayerWins() throws ReflectionException {
        try {
            logger.enter("toggleGameOverTwoPlayerWins");
            sessionService.setWinner(true);
            sessionService.setPartnered(true);
            interfaceService.showDialog(GameOverDialogController.class);
            logger.exit("toggleGameOverTwoPlayerWins");
        } catch (Exception e) {
            String errorMsg = "Error in toggleGameOverTwoPlayerWins()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("toggleGameOverOnePlayerLoses")
    private void toggleGameOverOnePlayerLoses() throws ReflectionException {
        try {
            logger.enter("toggleGameOverOnePlayerLoses");
            sessionService.setWinner(false);
            sessionService.setPartnered(false);
            interfaceService.showDialog(GameOverDialogController.class);
            logger.exit("toggleGameOverOnePlayerLoses");
        } catch (Exception e) {
            String errorMsg = "Error in toggleGameOverOnePlayerLoses()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("toggleGameOverTwoPlayerLoses")
    private void toggleGameOverTwoPlayerLoses() throws ReflectionException {
        try {
            logger.enter("toggleGameOverTwoPlayerLoses");
            sessionService.setWinner(false);
            sessionService.setPartnered(true);
            interfaceService.showDialog(GameOverDialogController.class);
            logger.exit("toggleGameOverTwoPlayerLoses");
        } catch (Exception e) {
            String errorMsg = "Error in toggleGameOverTwoPlayerLoses()";
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
                    if (gameBlock != null) {
                        addBrokenBlockActorToQueue(brokenBlocksQueue4, gameBlock, null, -1, -1);
                    }
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

    private void addBrokenBlockActorToQueue(OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue, GameBlock gameBlock, GameBlockGrid grid, int row, int col) {
        logger.enter("addBrokenBlockActorToQueue");
        if (gameBlock != null && brokenBlocksQueue != null) {
            GameBrokenBlockSpriteContainer brokenSprite = UIUtil.getBrokenBlockSprites(gameBlock, gameBlock.getBlock(), grid, row, col);
            brokenBlocksQueue.add(brokenSprite);
        }
        logger.exit("addBrokenBlockActorToQueue");
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
            logger.enter("getElapsedSeconds");
            int elapsedSeconds = YokelUtilities.otoi(((TimeUtils.millis() - nextGame) / 1000));
            logger.exit("getElapsedSeconds");
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

    @LmlAction("removePower")
    private void removePower() {
        if (queuePowers.size > 0) {
            Integer power = queuePowers.removeLast();
        }
    }

    @LmlAction("addYAttack")
    private void addYAttack() throws ReflectionException {
        try {
            logger.enter("addYAttack");
            int block = 0;

            if (toggleOffensive) {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 3));
            } else {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 4));
            }
            queuePowers.addLast(block);
            //powersQueue.setPowers(queuePowers);
            //String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YipeeBlockEvalGDX.addBrokenFlag(block));
            //playerBoard2Data.handlePower(block);
            //System.out.println(playerBoard2Data);
            logger.exit("addYAttack");
        } catch (Exception e) {
            String errorMsg = "Error in addYAttack()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("addAAttack")
    private void addAAttack() throws ReflectionException {
        try {
            logger.enter("addYAttack");
            int block = 1;

            if (toggleOffensive) {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 3));
            } else {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 4));
            }
            queuePowers.addLast(block);
            //powersQueue.setPowers(queuePowers);
            //String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YipeeBlockEvalGDX.addBrokenFlag(block));
            //playerBoard2Data.handlePower(block);
            //System.out.println(playerBoard2Data);
            logger.exit("addYAttack");
        } catch (Exception e) {
            String errorMsg = "Error in addYAttack()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("addHAttack")
    private void addHAttack() throws ReflectionException {
        try {
            logger.enter("addYAttack");
            int block = 2;

            if (toggleOffensive) {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 3));
            } else {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 4));
            }
            queuePowers.addLast(block);
            //powersQueue.setPowers(queuePowers);
            //String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YipeeBlockEvalGDX.addBrokenFlag(block));
            //playerBoard2Data.handlePower(block);
            //System.out.println(playerBoard2Data);
            logger.exit("addYAttack");
        } catch (Exception e) {
            String errorMsg = "Error in addYAttack()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("addOAttack")
    private void addOAttack() throws ReflectionException {
        try {
            logger.enter("addYAttack");
            int block = 3;

            if (toggleOffensive) {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 3));
            } else {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 4));
            }
            queuePowers.addLast(block);
            //powersQueue.setPowers(queuePowers);
            //String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YipeeBlockEvalGDX.addBrokenFlag(block));
            //playerBoard2Data.handlePower(block);
            //System.out.println(playerBoard2Data);
            logger.exit("addYAttack");
        } catch (Exception e) {
            String errorMsg = "Error in addYAttack()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("add0Attack")
    private void add0Attack() throws ReflectionException {
        try {
            logger.enter("addYAttack");
            int block = 4;

            if (toggleOffensive) {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 3));
            } else {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 4));
            }
            queuePowers.addLast(block);
            //powersQueue.setPowers(queuePowers);
            //String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YipeeBlockEvalGDX.addBrokenFlag(block));
            //playerBoard2Data.handlePower(block);
            //System.out.println(playerBoard2Data);
            logger.exit("addYAttack");
        } catch (Exception e) {
            String errorMsg = "Error in addYAttack()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("add_Attack")
    private void add_Attack() throws ReflectionException {
        try {
            logger.enter("addYAttack");
            int block = 5;

            if (toggleOffensive) {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 3));
            } else {
                block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(block, 4));
            }
            queuePowers.addLast(block);
            //powersQueue.setPowers(queuePowers);
            //String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YipeeBlockEvalGDX.addBrokenFlag(block));
            //playerBoard2Data.handlePower(block);
            //System.out.println(playerBoard2Data);
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

    @LmlAction("cycleUp")
    private void cycleUp() throws ReflectionException {
        try {
            logger.enter("cycleUp");
            playerBoardData.cycleUp();
            logger.exit("cycleUp");
        } catch (Exception e) {
            String errorMsg = "Error in cycleUp()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("cycleDown")
    private void cycleDown() throws ReflectionException {
        try {
            logger.enter("cycleDown");
            playerBoardData.cycleDown();
            logger.exit("cycleDown");
        } catch (Exception e) {
            String errorMsg = "Error in cycleDown()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }
}