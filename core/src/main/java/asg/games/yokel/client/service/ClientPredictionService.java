package asg.games.yokel.client.service;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Component;

import asg.games.yipee.game.PlayerAction;
import asg.games.yipee.game.YipeeGameBoard;
import asg.games.yipee.objects.YipeeGameBoardState;

@Component
public class ClientPredictionService {

    private YipeeGameBoard localBoard;
    private final Array<YipeeGameBoardState> predictedStates = new Array<>();
    private int currentTick = 0;

    public void initialize(long seed) {
        this.localBoard = new YipeeGameBoard(seed);
        this.predictedStates.clear();
        this.currentTick = 0;
    }

    public void applyAction(PlayerAction action) {
        localBoard.applyAction(action); // mutate board locally
        localBoard.update(1 / 60f);     // simulate a tick (or however your loop runs)
        predictedStates.add(localBoard.getCurrentState());
        currentTick++;
    }

    public Array<YipeeGameBoardState> getPredictedStates() {
        return predictedStates;
    }

    public YipeeGameBoardState getLatestState() {
        return predictedStates.size > 0 ? predictedStates.peek() : null;
    }

    public void reset() {
        localBoard = null;
        predictedStates.clear();
        currentTick = 0;
    }
}
