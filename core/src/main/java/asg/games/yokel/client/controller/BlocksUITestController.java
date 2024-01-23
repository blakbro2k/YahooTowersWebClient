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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
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
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

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
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelKeyMap;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.utils.YokelUtilities;

@View(id = GlobalConstants.UI_BLOCK_TEST_VIEW, value = GlobalConstants.UI_BLOCK_TEST_VIEW_PATH)
public class BlocksUITestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    private static final int BREAK_TIMER_MAX = 106;
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

    private boolean nextGameDialog, attemptGameStart, isGameReady = false;
    private long nextGame = 0;
    private YokelGameBoard playerBoardData;
    private boolean downKeyPressed = false;
    private final YokelKeyMap keyMap = new YokelKeyMap();
    private int breakTimer = -1;
    private int y, a, h, o, i, bash = 0;
    private final Array<GameBrokenBlockSpriteContainer> brokenBlocksQueue = GdxArrays.newArray();
    private final Queue<GameBlock> brokenBlocks = new Queue<>();


    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        logger.setDebug();
        try {
            logger.enter("initialize");
            y = yBlockImageBreakTest.getBlock();
            a = oBlockImageBreakTest.getBlock();
            h = kBlockImageBreakTest.getBlock();
            o = eBlockImageBreakTest.getBlock();
            i = lBlockImageBreakTest.getBlock();
            bash = bashBlockImageBreakTest.getBlock();

            brokenBlocks.addLast(yBlockImageBreakTest);
            brokenBlocks.addLast(oBlockImageBreakTest);
            brokenBlocks.addLast(kBlockImageBreakTest);
            brokenBlocks.addLast(eBlockImageBreakTest);
            brokenBlocks.addLast(lBlockImageBreakTest);
            brokenBlocks.addLast(bashBlockImageBreakTest);

            initiate();
            logger.exit("initialize");

        } catch (Exception e) {
            String errorMsg = "Error in initialize()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    @Override
    public void destroy(ViewController viewController) {
        try {
            logger.enter("destroy");
            //boardState.dispose();
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
            YokelPlayer player = ClassReflection.newInstance(YokelPlayer.class);
            player.setName("test");
            player.setRating(2000);
            player.setIcon(6);

            logger.info("Set Yokelplayer: {}", player);

            //previewBoard.sitPlayerDown(player);
            //previewBoard.setGameReady(true);
            //previewBoard.setIsPlayerReady(true);
            //previewBoard.hideJoinButton();
            previewBoard.setName("1");

            playerBoardData = new YokelGameBoard(1);
            playerBoardData.begin();

            //playerB.setNextPiece();
            playerBoard.renderBoard(playerBoardData);
            playerBoard.setPreview(false);
            playerBoard.setActive(true);
            playerBoard.setPlayerView(true);

            //playerBoard.sitPlayerDown(player);
            //playerBoard.setGameReady(true);
            //playerBoard.setIsPlayerReady(true);
            //playerBoard.hideJoinButton();
            playerBoard.setName("2");
            joinReady.setIsGameReady(true);
            int seatNumber = 4;
            logger.info("setting player {} @ seat[{}]", player, seatNumber);
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

            logger.exit("initiateActors");
        } catch (Exception e) {
            String errorMsg = "Error in initiateActors()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }

        logger.exit("initiateActors");
    }

    @Override
    public void render(Stage stage, float delta) {
        try {
            if (breakTimer == 0) {
                yBlockImageBreakTest.setBlock(y);
                oBlockImageBreakTest.setBlock(a);
                kBlockImageBreakTest.setBlock(h);
                eBlockImageBreakTest.setBlock(o);
                lBlockImageBreakTest.setBlock(i);
                bashBlockImageBreakTest.setBlock(bash);

                brokenBlocks.addLast(yBlockImageBreakTest);
                brokenBlocks.addLast(oBlockImageBreakTest);
                brokenBlocks.addLast(kBlockImageBreakTest);
                brokenBlocks.addLast(eBlockImageBreakTest);
                brokenBlocks.addLast(lBlockImageBreakTest);
                brokenBlocks.addLast(bashBlockImageBreakTest);
            }
            --breakTimer;

            if (brokenBlocksQueue.size > 0) {
                //System.out.println("BEFORE: Broken Array Size: " + GdxArrays.sizeOf(brokenBlocksQueue));
                addBrokenBlockActorToStage(stage);
                //System.out.println("AFTER: Broken Array Size: " + GdxArrays.sizeOf(brokenBlocksQueue));
                //System.out.println("Stage: " + stage.getActors());
            }

            //logger.enter("render");
            //checkGameStart();
            //playerBoardData.update(delta);
            //playerBoard.renderBoard(playerBoardData);

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
            if (brokenIterator.hasNext()) {
                GameBrokenBlockSpriteContainer brokenGameSprite = brokenIterator.next();
                if (brokenGameSprite != null) {
                    float duration = 0.6f;
                    float delay = 0.0f;
                    float endOfScreen = 0f;
                    float blockOffSet = 20;
                    float brokenOffSet = 12;
                    Interpolation interpolation = Interpolation.swingIn;

                    Image left = brokenGameSprite.getLeftSprite();
                    Image bottom = brokenGameSprite.getBottomSprite();
                    Image right = brokenGameSprite.getRightSprite();
                    Actor parent = brokenGameSprite.getParent();

                    if (parent != null) {
                        //Get the coordinates of the parent block
                        Vector2 leftV = left.localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                        //Get the screen coordinates
                        Vector2 leftV2 = left.localToScreenCoordinates(new Vector2(leftV.x, leftV.y));
                        //Shift block x slightly
                        leftV2.x -= parent.getX();
                        leftV2.x -= brokenOffSet;
                        //Shift (and flip) block y
                        leftV2.y = 1 - (leftV2.y - (left.getHeight() / 2)) + stage.getHeight();

                        /*
                        logger.error("parent: {} ({},{})", parent.getName(), parent.getX(), parent.getY());
                        logger.error("left: {} ({},{})", "left", left.getX(), left.getY());
                        logger.error("{}: ({},{})", "leftV", leftV.x, leftV.y);
                        logger.error("{}: ({},{})", "leftV2", leftV2.x, leftV2.y);*/

                        Vector2 bottomV = bottom.localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                        Vector2 bottomV2 = bottom.localToScreenCoordinates(new Vector2(bottomV.x, bottomV.y));
                        bottomV2.x -= parent.getX();
                        bottomV2.y = 1 - (bottomV2.y - (bottom.getHeight() / 2)) + stage.getHeight();

                        Vector2 rightV = right.localToParentCoordinates(new Vector2(parent.getX(), parent.getY()));
                        Vector2 rightV2 = right.localToScreenCoordinates(new Vector2(rightV.x, rightV.y));
                        rightV2.x -= parent.getX();
                        rightV2.x += brokenOffSet;
                        rightV2.y = 1 - (rightV2.y - (right.getHeight() / 2)) + stage.getHeight();

                        left.setBounds(leftV2.x, leftV2.y, left.getWidth(), left.getHeight());
                        left.addAction(
                                Actions.sequence(Actions.delay(delay),
                                        Actions.moveTo(leftV2.x - blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(left)));

                        bottom.setBounds(bottomV2.x, bottomV2.y, bottom.getWidth(), bottom.getHeight());
                        bottom.addAction(
                                Actions.sequence(Actions.delay(delay),
                                        Actions.moveTo(bottomV2.x, endOfScreen, duration, interpolation), Actions.removeActor(bottom)));

                        right.setBounds(rightV2.x, rightV2.y, right.getWidth(), right.getHeight());
                        right.addAction(
                                Actions.sequence(Actions.delay(delay),
                                        Actions.moveTo(rightV2.x + blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(right)));

                        stage.addActor(left);
                        stage.addActor(bottom);
                        stage.addActor(right);
                    }



                   /* if (isPreview()) {
                        getStage().addActor(brokenBlockImageLeft);
                    } else {/*
                    stage.addActor(brokenBlockImageLeft);
                    stage.addActor(brokenBlockImageBottom);
                    stage.addActor(brokenBlockImageRight);
                    //}*/
                    brokenIterator.remove();
                    Pools.free(brokenGameSprite);
                }
            }
            logger.info("{}: ({},{})", "stage", stage.getWidth(), stage.getHeight());
            logger.exit("addBrokenBlockActorToStage");
        }
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
            logger.enter("checkGameStart");
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
            logger.exit("checkGameStart");
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
                while (brokenBlocks.size > 0) {
                    GameBlock gameBlock = brokenBlocks.removeFirst();
                    addBrokenBlockActorToQueue(gameBlock);
                    gameBlock.setBlock(YokelBlockEval.addBrokenFlag(gameBlock.getBlock()));
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

    private void addBrokenBlockActorToQueue(GameBlock gameBlock) {
        if (gameBlock != null) {
            GameBrokenBlockSpriteContainer brokenSprite = UIUtil.getBrokenBlockSprites(gameBlock, gameBlock.getBlock());
            //logger.error("{}\n{}\n{}\n{}\n", brokenSprite.getParent().getName(), brokenSprite.getLeftSprite().getName(), brokenSprite.getBottomSprite().getName(), brokenSprite.getRightSprite().getName());
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
            logger.enter("startGame");
            if (!gameClock.isRunning()) {
                gameClock.start();
            }
            logger.exit("startGame");
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
}