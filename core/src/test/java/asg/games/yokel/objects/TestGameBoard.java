package asg.games.yokel.objects;

import com.badlogic.gdx.utils.TimeUtils;

import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import asg.games.yipee.game.GameBoardState;
import asg.games.yipee.libgdx.game.YipeeGameBoardGDX;
import asg.games.yipee.libgdx.objects.YipeeGameBoardStateGDX;


public class TestGameBoard {

    @Test()
    public void testGameBoard() {
        //Set up a table for testing (use a provider)
        //Set up a GameManager
        //Set up a list of inputs to process for the game manager
        //view outputs

        System.out.println("Start Test Game Board.");
        //ServerGameState serverGameState = new ServerGameState();

        int gameSeed = -1;
        int maxTicks = 129;
        YipeeGameBoardGDX playerB = new YipeeGameBoardGDX(gameSeed);
        playerB.setDebug(true);
        YipeeGameBoardGDX partnerB = new YipeeGameBoardGDX(gameSeed);
        partnerB.setDebug(false);
        boolean isPartnerRight = true;
        playerB.begin();
        playerB.startMoveDown();
        //partnerB.begin();
        int playerBIndex = 0;
        int partnerBIndex = 1;
        //serverGameState.setGameState(playerBIndex, playerB.getGameState());
        //serverGameState.setGameState(partnerBIndex, partnerB.getGameState());
        List<GameBoardState> states = new LinkedList<>();

        long gameStartTime = TimeUtils.millis();
        long serverTickTime;
        long prevServerTickTime = TimeUtils.millis();

        for (int tick = 0; tick < maxTicks; tick++) {
            if (tick == 1) {
                playerB.cycleDown();
            }
            if (tick == 1) {
                //playerB.testVerticalYahoo();
            }
            if (tick == 71) {
                playerB.cycleDown();
            }
            if (tick == 52) {
                //playerB.movePieceLeft();
            }
            if (tick == 125) {
                //playerB.movePieceRight();
            }
            serverTickTime = TimeUtils.millis();
            float delta = serverTickTime - prevServerTickTime;
            prevServerTickTime = serverTickTime;

            System.out.println("#---------- Start iteration tick: " + tick + "  --------------------#");
            playerB.setPartnerBoard(partnerB, isPartnerRight);
            partnerB.setPartnerBoard(playerB, !isPartnerRight);
            playerB.update(delta);
            partnerB.update(delta);

            YipeeGameBoardStateGDX gameState = playerB.exportGameState();
            gameState.setServerGameStartTime(gameStartTime);
            gameState.setCurrentStateTimeStamp(TimeUtils.millis());
            states.add(gameState);
            //System.out.println(playerB);
            System.out.println("#----------  Game State -------------------#");
            System.out.println(gameState);
            System.out.println("#---------- end iteration tick: " + tick + "  --------------------#");
        }
        playerB.end();
        partnerB.end();
        System.out.println("Total States: " + states.size());
        System.out.println("End Test Game Board");
        System.out.println("\n\n\n" + states.get(2));
    }
}