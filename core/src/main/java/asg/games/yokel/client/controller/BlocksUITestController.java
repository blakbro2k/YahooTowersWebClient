package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
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
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.dialog.NextGameController;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlockArea;
import asg.games.yokel.client.ui.actors.GameClock;
import asg.games.yokel.client.ui.actors.GameJoinWidget;
import asg.games.yokel.client.ui.actors.GameNameLabel;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelKeyMap;
import asg.games.yokel.objects.YokelPlayer;

@View(id = GlobalConstants.UI_BLOCK_TEST_VIEW, value = GlobalConstants.UI_BLOCK_TEST_VIEW_PATH)
public class BlocksUITestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
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
    private GameBlockArea previewBoard;
    @LmlActor("player")
    private GameBlockArea playerBoard;
    @LmlActor("partner")
    private GameBlockArea partnerBoard;

    private boolean nextGameDialog, attemptGameStart, isGameReady = false;
    private long nextGame = 0;
    private YokelGameBoard playerB;
    private boolean downKeyPressed = false;
    private final YokelKeyMap keyMap = new YokelKeyMap();

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());

        try {
            initiate();
        } catch (ReflectionException e) {
            sessionService.showError(e);
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(ViewController viewController) {
        //boardState.dispose();
    }

    private void initiate() throws ReflectionException {
        logger.enter("initiate");
        initiateActors();
        YokelPlayer player = ClassReflection.newInstance(YokelPlayer.class);
        player.setName("test");
        player.setRating(2000);
        player.setIcon(6);

        logger.info("Set Yokelplayer: {}", player);

        previewBoard.sitPlayerDown(player);
        previewBoard.setGameReady(true);
        previewBoard.setIsPlayerReady(true);
        previewBoard.hideJoinButton();
        previewBoard.setName("1");

        playerB = new YokelGameBoard(1);
        playerB.begin();

        //playerB.setNextPiece();
        playerBoard.updateData(playerB);
        playerBoard.setPreview(false);
        playerBoard.setActive(true);
        playerBoard.setPlayerView(true);

        playerBoard.sitPlayerDown(player);
        playerBoard.setGameReady(true);
        playerBoard.setIsPlayerReady(true);
        playerBoard.hideJoinButton();
        playerBoard.setName("2");
        joinReady.setIsGameReady(true);
        int seatNumber = 4;
        logger.error("setting player {} @ seat[{}]", player, seatNumber);

        logger.exit("initiate");
    }

    private void initiateActors() {
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
    }

    @Override
    public void render(Stage stage, float delta) {
        checkGameStart();
        playerB.update(delta);
        playerBoard.updateData(playerB);

        //Handle Player input
        handlePlayerSimulatedInput(playerB);

        //showGameOver(stage);
        stage.act(delta);
        stage.draw();
    }

    public void handlePlayerSimulatedInput(YokelGameBoard game) {
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
    }

    private void checkGameStart() {
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
    }

    @LmlAction("toggleGameStart")
    private void toggleGameStart() {
        logger.enter("toggleGameStart");

        if (!attemptGameStart) {
            attemptGameStart = true;
        }
        if (nextGameDialog) {
            nextGameDialog = false;
            stopGame();
        }
        logger.exit("toggleGameStart");
    }

    @LmlAction("stopClock")
    private void stopClock() {
        if(nextGameDialog){
            nextGameDialog = false;
        }
        stopGame();
    }

    int getElapsedSeconds(){
        return (int) ((TimeUtils.millis() - nextGame) / 1000);
    }

    private void startGame(){
        if(!gameClock.isRunning()) {
            gameClock.start();
        }
    }

    private void stopGame(){
        if(gameClock.isRunning()) {
            gameClock.stop();
        }
    }

    @LmlAction("resetAllBoards")
    private void resetAllBoards() {
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
    }
}