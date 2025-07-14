package asg.games.yokel.client.service;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

import asg.games.yipee.common.packets.PlayerAction;
import asg.games.yipee.libgdx.game.YipeeGameBoardGDX;
import asg.games.yipee.libgdx.objects.YipeeGameBoardStateGDX;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientPredictionService {

    private YipeeGameBoardGDX authoritativeBoard;
    private YipeeGameBoardGDX predictedBoard;

    private int lastAuthoritativeTick = 0;
    private final Array<PlayerAction> pendingActions = GdxArrays.newArray();

    public void initialize(long seed) {
        this.authoritativeBoard = new YipeeGameBoardGDX(seed);
        this.predictedBoard = new YipeeGameBoardGDX(seed);
        this.pendingActions.clear();
    }

    /**
     * Called when the server confirms an authoritative state
     */
    public void setAuthoritativeState(YipeeGameBoardStateGDX state, float delta) {
        authoritativeBoard.updateStateAndAll(state, delta);
        resimulatePrediction(delta);
    }

    /**
     * Client adds a new local input
     */
    public void queueLocalAction(PlayerAction action, float delta) {
        pendingActions.add(action);
        predictedBoard.applyPlayerAction(action);
        predictedBoard.update(delta);
    }

    /**
     * Re-apply all local inputs to fresh authoritative state
     */
    private void resimulatePrediction(float delta) {
        predictedBoard.updateStateAndAll(authoritativeBoard.exportGameState().deepCopy(), delta);

        for (PlayerAction action : pendingActions) {
            predictedBoard.applyPlayerAction(action);
            predictedBoard.update(delta);
        }
    }

    /**
     * Get predicted state for rendering
     */
    public YipeeGameBoardStateGDX getPredictedState() {
        return predictedBoard.exportGameState();
    }

    /**
     * For test harness: step prediction manually
     */
    public void stepPrediction(PlayerAction action, float delta) {
        queueLocalAction(action, delta);
    }

    /** Reset everything (e.g. on reconnect) */
    public void reset() {
        this.authoritativeBoard = null;
        this.predictedBoard = null;
        this.pendingActions.clear();
    }
}