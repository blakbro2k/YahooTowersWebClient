package asg.games.yokel.client.controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
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

import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBoard;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelPlayer;

@View(id = "clientViewTest", value = "ui/templates/clientViewTest.lml")
public class ClientViewTestController extends ApplicationAdapter implements ViewRenderer, ViewInitializer, ActionContainer {
    @Inject
    private UserInterfaceService uiService;
    //@Inject private SessionService sessionService;
    @Inject private InterfaceService interfaceService;
    @Inject private MusicService musicService;
    @Inject private LoadingController assetController;

    @LmlActor("1:area") private GameBoard area1;
    @LmlActor("2:area") private GameBoard area2;
    @LmlActor("3:area") private GameBoard area3;
    @LmlActor("4:area") private GameBoard area4;

    private YokelGameBoard boardState;
    private boolean nextGameDialog, attemptGameStart, isGameReady = false;
    private long nextGame = 0;
    private boolean yahoo = false;
    //private GameOverText gameOverText;
    private boolean showGameOver;

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
        YokelPlayer player = ClassReflection.newInstance(YokelPlayer.class);
        player.setName("test");
        player.setRating(2000);
        player.setIcon(6);

        boardState = getTestBoard();
        //boardState = new YokelGameBoard(1L);
        area1.hideJoinButton();
        area1.setData(player.toString());
        area1.update(boardState);
        System.out.println(boardState);

        //area = new GameBoard(uiService.getSkin());
        YokelPlayer player2 = new YokelPlayer("Test Player One",2000, 5);
        //area2.setBoardNumber(2);
        area2.hideJoinButton();
        area2.setData(player2.toString());
        area2.update(getTestBoard());

        area3.hideJoinButton();
        area3.setPlayerView(false);
        area3.update(getTestBoard());

        area4.hideJoinButton();
        area4.setPlayerView(false);
        area4.update(getTestBoard());
    }

    @Override
    public void render(Stage stage, float delta) {
        //showGameOver(stage);
        stage.act(delta);
        stage.draw();
    }



/*
    private void showGameOver(Stage stage){
        if(showGameOver){
            stage.addActor(getGameOverActor());
        }
    }

    private GameOverText getGameOverActor(){
        if(gameOverText == null){
            gameOverText = new GameOverText(false, new YokelPlayer("ReadyPlayerOne"), new YokelPlayer("Player2"), uiService.getSkin());
        }
        return gameOverText;
    }*/

    @LmlAction("getTestBoard")
    private YokelGameBoard getTestBoard(){
        YokelGameBoard board = new YokelGameBoard(1L);

        board.setCell(0,0, YokelBlock.Y_BLOCK);
        board.setCell(0,1, YokelBlock.A_BLOCK);
        board.setCell(0,2, YokelBlock.H_BLOCK);
        board.setCell(0,3, YokelBlock.Op_BLOCK);
        board.setCell(0,4, YokelBlock.Oy_BLOCK);
        board.setCell(0,5, YokelBlock.EX_BLOCK);


        board.setCell(1,0, YokelBlock.DEFENSIVE_Y_BLOCK_MINOR);
        board.setCell(1,1, YokelBlock.DEFENSIVE_O_BLOCK_REGULAR);
        board.setCell(1,2, YokelBlock.DEFENSIVE_K_BLOCK_MEGA);
        board.setCell(1,3, YokelBlock.DEFENSIVE_E_BLOCK_MINOR);
        board.setCell(1,4, YokelBlock.DEFENSIVE_L_BLOCK_MINOR);
        board.setCell(1,5, YokelBlock.DEFENSIVE_BASH_BLOCK_REGULAR);

        board.setCell(2,0, YokelBlock.OFFENSIVE_Y_BLOCK_MEGA);
        board.setCell(2,1, YokelBlock.OFFENSIVE_O_BLOCK_MEGA);
        board.setCell(2,2, YokelBlock.OFFENSIVE_K_BLOCK_MEGA);
        board.setCell(2,3, YokelBlock.OFFENSIVE_E_BLOCK_REGULAR);
        board.setCell(2,4, YokelBlock.OFFENSIVE_L_BLOCK_REGULAR);
        board.setCell(2,5, YokelBlock.OFFENSIVE_BASH_BLOCK_MINOR);

        board.setCell(3,0, YokelBlock.STONE);
        board.setCell(3,1, getRandomBlockId());
        board.setCell(3,2, getRandomBlockId());
        board.setCell(3,3, getRandomBlockId());
        board.setCell(3,4, getRandomBlockId());
        board.setCell(3,5, getRandomBlockId());

        board.setCell(4,0, getRandomBlockId());
        board.setCell(4,1, getRandomBlockId());
        board.setCell(4,2, getRandomBlockId());
        board.setCell(4,3, getRandomBlockId());
        board.setCell(4,4, getRandomBlockId());
        board.setCell(4,5, getRandomBlockId());

        board.setCell(5,0, getRandomBlockId());
        board.setCell(5,1, getRandomBlockId());
        board.setCell(5,2, getRandomBlockId());
        board.setCell(5,3, getRandomBlockId());
        board.setCell(5,4, getRandomBlockId());
        board.setCell(5,5, getRandomBlockId());

        board.setCell(6,0, getRandomBlockId());
        board.setCell(3,0, YokelBlock.STONE);
        board.setCell(6,2, getRandomBlockId());
        board.setCell(6,3, getRandomBlockId());
        board.setCell(6,4, getRandomBlockId());
        board.setCell(6,5, getRandomBlockId());

        board.setCell(7,0, getRandomBlockId());
        board.setCell(7,1, getRandomBlockId());
        board.setCell(7,2, getRandomBlockId());
        board.setCell(7,3, getRandomBlockId());
        board.setCell(7,4, getRandomBlockId());
        board.setCell(7,5, getRandomBlockId());

        board.setCell(8,0, getRandomBlockId());
        board.setCell(8,1, getRandomBlockId());
        board.setCell(8,2, getRandomBlockId());
        board.setCell(8,3, getRandomBlockId());
        board.setCell(8,4, getRandomBlockId());
        board.setCell(8,5, getRandomBlockId());

        board.setCell(9,0, getRandomBlockId());
        board.setCell(9,1, getRandomBlockId());
        board.setCell(9,2, getRandomBlockId());
        board.setCell(3,0, YokelBlock.STONE);
        board.setCell(9,4, getRandomBlockId());
        board.setCell(9,5, getRandomBlockId());

        board.setCell(10,0, getRandomBlockId());
        board.setCell(10,1, getRandomBlockId());
        board.setCell(10,2, getRandomBlockId());
        board.setCell(10,3, getRandomBlockId());
        board.setCell(10,4, getRandomBlockId());
        board.setCell(10,5, getRandomBlockId());

        board.setCell(11,0, getRandomBlockId());
        board.setCell(11,1, getRandomBlockId());
        board.setCell(11,2, getRandomBlockId());
        board.setCell(11,3, getRandomBlockId());
        board.setCell(11,4, getRandomBlockId());
        board.setCell(11,5, getRandomBlockId());

        board.setCell(12,0, getRandomBlockId());
        board.setCell(12,1, getRandomBlockId());
        board.setCell(12,2, getRandomBlockId());
        board.setCell(12,3, getRandomBlockId());
        board.setCell(3,0, YokelBlock.STONE);
        board.setCell(12,5, getRandomBlockId());

        board.setCell(13,0, getRandomBlockId());
        board.setCell(13,1, getRandomBlockId());
        board.setCell(13,2, getRandomBlockId());
        board.setCell(13,3, getRandomBlockId());
        board.setCell(13,4, getRandomBlockId());
        board.setCell(13,5, getRandomBlockId());

        board.setCell(14,0, getRandomBlockId());
        board.setCell(14,1, getRandomBlockId());
        board.setCell(14,2, getRandomBlockId());
        board.setCell(14,3, getRandomBlockId());
        board.setCell(14,4, getRandomBlockId());
        board.setCell(14,5, getRandomBlockId());

        board.setCell(15,0, getRandomBlockId());
        board.setCell(15,1, getRandomBlockId());
        board.setCell(15,2, getRandomBlockId());
        board.setCell(15,3, getRandomBlockId());
        board.setCell(15,4, getRandomBlockId());
        board.setCell(15,5, getRandomBlockId());

        return board;
    }

    private int getRandomBlockId(){
        return MathUtils.random(YokelBlock.EX_BLOCK);
    }

    public void checkInput(){
        //if(area1 == null) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.Y_BLOCK, 3)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.Y_BLOCK, 2)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.A_BLOCK, 3)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.A_BLOCK, 2)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.H_BLOCK, 5)));
            System.out.println(boardState);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.H_BLOCK, 2)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.Op_BLOCK, 3)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.Op_BLOCK, 2)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.EX_BLOCK, 3)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.EX_BLOCK, 2)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            //boardState.handlePower(YokelBlockEval.addPowerBlockFlag(YokelBlockEval.setPowerFlag(YokelBlock.EX_BLOCK, 2)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            System.out.println(12292);
            System.out.println(YokelBlockEval.getID(12292));
            System.out.println(YokelBlockEval.getIDFlag(YokelBlockEval.getID(12292), 12292));

            System.out.println(32820);
            System.out.println(YokelBlockEval.hasAddedByYahooFlag(32820));
            System.out.println(YokelBlockEval.getID(32820));
            System.out.println(YokelBlockEval.getIDFlag(YokelBlockEval.getID(32820), 32820));
        }
    }

    @LmlAction("playGameOver")
    private void playGameOver(){
        showGameOver = !showGameOver;
    }
}