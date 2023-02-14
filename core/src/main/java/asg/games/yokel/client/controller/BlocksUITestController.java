package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
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
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.dialog.NextGameController;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlockArea;
import asg.games.yokel.client.ui.actors.GameClock;
import asg.games.yokel.client.ui.actors.GameJoinWidget;
import asg.games.yokel.client.ui.actors.GameNameLabel;
import asg.games.yokel.objects.YokelPlayer;

@View(id = GlobalConstants.UI_BLOCK_TEST_VIEW, value = GlobalConstants.UI_BLOCK_TEST_VIEW_PATH)
public class BlocksUITestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    @Inject
    private UserInterfaceService uiService;
    //@Inject private SessionService sessionService;
    @Inject private InterfaceService interfaceService;
    @Inject private MusicService musicService;
    @Inject private LoadingController assetController;

    @LmlActor("Y_block") private Image yBlockImage;
    @LmlActor("O_block") private Image oBlockImage;
    @LmlActor("K_block") private Image kBlockImage;
    @LmlActor("E_block") private Image eBlockImage;
    @LmlActor("L_block") private Image lBlockImage;
    @LmlActor("Bash_block") private Image bashBlockImage;
    @LmlActor("Y_block_preview") private Image yBlockImagePreview;
    @LmlActor("O_block_preview") private Image oBlockImagePreview;
    @LmlActor("K_block_preview") private Image kBlockImagePreview;
    @LmlActor("E_block_preview") private Image eBlockImagePreview;
    @LmlActor("L_block_preview") private Image lBlockImagePreview;
    @LmlActor("Bash_block_preview") private Image bashBlockImagePreview;
    @LmlActor("power_Y_block_preview") private Image poweryBlockImagePreview;
    @LmlActor("power_O_block_preview") private Image poweroBlockImagePreview;
    @LmlActor("power_K_block_preview") private Image powerkBlockImagePreview;
    @LmlActor("power_E_block_preview") private Image powereBlockImagePreview;
    @LmlActor("power_L_block_preview") private Image powerlBlockImagePreview;
    @LmlActor("power_Bash_block_preview") private Image powerbashBlockImagePreview;
    @LmlActor("defense_Y_block") private AnimatedImage defenseYBlockImage;
    @LmlActor("defense_O_block") private AnimatedImage defenseOBlockImage;
    @LmlActor("defense_K_block") private AnimatedImage defenseKBlockImage;
    @LmlActor("defense_E_block") private AnimatedImage defenseEBlockImage;
    @LmlActor("defense_L_block") private AnimatedImage defenseLBlockImage;
    @LmlActor("defense_Bash_block") private AnimatedImage defenseBashBlockImage;
    @LmlActor("defense_Y_block_preview") private Image defenseYBlockImagePreview;
    @LmlActor("defense_O_block_preview") private Image defenseOBlockImagePreview;
    @LmlActor("defense_K_block_preview") private Image defenseKBlockImagePreview;
    @LmlActor("defense_E_block_preview") private Image defenseEBlockImagePreview;
    @LmlActor("defense_L_block_preview") private Image defenseLBlockImagePreview;
    @LmlActor("defense_Bash_block_preview") private Image defenseBashBlockImagePreview;
    @LmlActor("power_Y_block") private AnimatedImage powerYBlockImage;
    @LmlActor("power_O_block") private AnimatedImage powerOBlockImage;
    @LmlActor("power_K_block") private AnimatedImage powerKBlockImage;
    @LmlActor("power_E_block") private AnimatedImage powerEBlockImage;
    @LmlActor("power_L_block") private AnimatedImage powerLBlockImage;
    @LmlActor("power_bash_block") private AnimatedImage powerBashBlockImage;
    @LmlActor("Y_block_Broken") private AnimatedImage brokenYBlockImage;
    @LmlActor("O_block_Broken") private AnimatedImage brokenOBlockImage;
    @LmlActor("K_block_Broken") private AnimatedImage brokenKBlockImage;
    @LmlActor("E_block_Broken") private AnimatedImage brokenEBlockImage;
    @LmlActor("L_block_Broken") private AnimatedImage brokenLBlockImage;
    @LmlActor("Bash_block_Broken") private AnimatedImage brokenBashBlockImage;
    @LmlActor("medusa") private AnimatedImage medusaImage;
    @LmlActor("medusa2") private AnimatedImage medusa2Image;
    @LmlActor("medusa3") private AnimatedImage medusa3Image;
    @LmlActor("top_midas") private AnimatedImage topMidasImage;
    @LmlActor("mid_midas") private AnimatedImage midMidasImage;
    @LmlActor("bottom_midas") private AnimatedImage bottomMidasImage;
    @LmlActor("Y_block_Broken_preview") private AnimatedImage brokenYBlockImagePreview;
    @LmlActor("O_block_Broken_preview") private AnimatedImage brokenOBlockImagePreview;
    @LmlActor("K_block_Broken_preview") private AnimatedImage brokenKBlockImagePreview;
    @LmlActor("E_block_Broken_preview") private AnimatedImage brokenEBlockImagePreview;
    @LmlActor("L_block_Broken_preview") private AnimatedImage brokenLBlockImagePreview;
    @LmlActor("Bash_block_Broken_preview") private AnimatedImage brokenBashBlockImagePreview;
    @LmlActor("stone") private Image stoneBlockImage;
    @LmlActor("stone") private Image stone2BlockImage;
    @LmlActor("gameClock") private GameClock gameClock;
    @LmlActor("clear_block") private Image clearBlock;
    @LmlActor("clear_block_preview") private Image clearBlockPreview;
    @LmlActor("join") private GameJoinWidget join;
    @LmlActor("joinReady") private GameJoinWidget joinReady;
    @LmlActor("playerOne") private GameNameLabel playerOne;
    @LmlActor("playerTwo") private GameNameLabel playerTwo;
    @LmlActor("preview") private GameBlockArea preview;
    @LmlActor("board") private GameBlockArea board;

    private boolean nextGameDialog, attemptGameStart, isGameReady = false;
    private long nextGame = 0;

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        try {
            initiate();
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(ViewController viewController) {
        //boardState.dispose();
    }

    private void initiate() throws ReflectionException {
        initiateActors();
        YokelPlayer player = ClassReflection.newInstance(YokelPlayer.class);
        player.setName("test");
        player.setRating(2000);
        player.setIcon(6);

        preview.sitPlayerDown(player);
        preview.setGameReady(true);
        preview.setIsPlayerReady(true);
        preview.hideJoinButton();
        preview.setName("1");
        board.sitPlayerDown(player);
        board.setGameReady(true);
        board.setIsPlayerReady(true);
        board.hideJoinButton();
        board.setName("2");
        joinReady.setIsGameReady(true);
    }

    private void initiateActors() {
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
    }

    @Override
    public void render(Stage stage, float delta) {
        checkGameStart();
        //showGameOver(stage);
        stage.act(delta);
        stage.draw();
    }

    private void checkGameStart() {
        if(gameClock == null) return;

        if(attemptGameStart){
            if(!nextGameDialog) {
                nextGameDialog = true;
                interfaceService.showDialog(NextGameController.class);
                nextGame = TimeUtils.millis();
            }

            if(getElapsedSeconds() > NextGameController.NEXT_GAME_SECONDS){
                interfaceService.destroyDialog(NextGameController.class);
                attemptGameStart = false;
                startGame();
            }
        }
    }

    @LmlAction("toggleGameStart")
    private void toggleGameStart() {
        if(!attemptGameStart){
            attemptGameStart = true;
        }
        if(nextGameDialog){
            nextGameDialog = false;
            stopGame();
        }
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
    private void resetAllBoards(){
        join.setIsGameReady(false);
        join.setIsPlayerReady(false);
        join.setSeated(false);
        joinReady.setIsPlayerReady(false);
        joinReady.setIsGameReady(true);
        joinReady.setSeated(false);
    }
}