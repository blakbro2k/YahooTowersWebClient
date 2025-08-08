package asg.games.yokel.client.service;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

import java.util.Iterator;

import asg.games.yipee.common.packets.PlayerAction;
import asg.games.yipee.libgdx.game.YipeeGameBoardGDX;
import asg.games.yipee.libgdx.objects.YipeeGameBoardStateGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import lombok.Getter;
import lombok.Setter;

/**
 * Client-side prediction manager for simulating Yipee game board state.
 * <p>
 * Maintains two copies of the board:
 * - An authoritative copy updated from the server,
 * - A predicted copy updated using unacknowledged player inputs.
 * <p>
 * Automatically rolls back and reapplies local inputs when a new authoritative
 * state is received, enabling low-latency local simulation.
 */
@Getter
@Setter
public class ClientPredictionService {
    private YipeeGameBoardGDX authoritativeBoard;
    private YipeeGameBoardGDX predictedBoard;
    private int playerSeat;
    private long localSeed;

    private int lastAuthoritativeTick = 0;
    private final Array<TimedAction> pendingActions = GdxArrays.newArray();
    private YipeeTableGDX table;
    private int tick; // the client-side tick this action occurred

    public void initialize(long seed, YipeeTableGDX table, int playerSeat) {
        reset(seed, table, playerSeat);
        resetAuthoritativeBoards(seed);
        predictedBoard = new YipeeGameBoardGDX(seed);
    }

    private void resetAuthoritativeBoards(long seed) {
        if (authoritativeBoard == null) {
            authoritativeBoard = new YipeeGameBoardGDX(seed);
        } else {
            authoritativeBoard.reset(seed);
        }
        if (predictedBoard == null) {
            predictedBoard = new YipeeGameBoardGDX(seed);
        } else {
            predictedBoard.reset(seed);
        }
    }

    /**
     * Called when a new authoritative state is received from the server.
     * Updates the authoritative board and re-simulates local prediction.
     *
     * @param state the confirmed game board state
     * @param delta the update delta
     */
    public void setAuthoritativeState(YipeeGameBoardStateGDX state, float delta) {
        YipeeGameBoardGDX authoritativeBoard = getAuthoritativeBoard();
        if (authoritativeBoard == null) return;
        authoritativeBoard.updateGameState(delta, state, null);
        reSimulatePrediction(delta);
    }

    /**
     * Applies a new local input and updates the predicted board immediately.
     *
     * @param action the player action to simulate
     * @param delta  frame delta time used for game logic progression
     */
    public void queueLocalAction(PlayerAction action, float delta) {
        pendingActions.add(new TimedAction(action, tick));
        predictedBoard.applyPlayerAction(action);
        predictedBoard.updateGameState(delta, predictedBoard.exportGameState(), null);
    }

    public void queueTickedLocalAction(PlayerAction action, float delta, int tick) {
        setTick(tick);
        queueLocalAction(action, delta);
    }

    /**
     * Re-simulates the predicted board from the last confirmed authoritative state,
     * reapplying all pending (unacknowledged) local actions.
     *
     * @param delta the time delta used to update game logic
     */
    private void reSimulatePrediction(float delta) {
        YipeeGameBoardGDX authoritativeBoard = getAuthoritativeBoard();
        if (authoritativeBoard == null) return;
        predictedBoard.updateGameState(delta, authoritativeBoard.exportGameState().deepCopy(), null);

        for (TimedAction action : pendingActions) {
            if (action != null) {
                setTick(action.tick);
                predictedBoard.applyPlayerAction(action.action);
                predictedBoard.updateGameState(delta, predictedBoard.exportGameState(), null);
            }

        }
    }

    public void clearAcknowledgedActions(int serverTick) {
        // You can track ticks per action later for smarter cleanup
        //pendingActions.removeIf(timed -> timed.tick <= serverTick);
        //pendingActions.clear(); // placeholder logic
        for (Iterator<TimedAction> it = pendingActions.iterator(); it.hasNext(); ) {
            if (it.next().tick <= serverTick) {
                it.remove();
            }
        }
    }

    /**
     * Get predicted state for rendering
     */
    public YipeeGameBoardStateGDX getPredictedState() {
        return predictedBoard.exportGameState();
    }

    /** Reset everything (e.g. on reconnect) */
    public void reset(long seed, YipeeTableGDX table, int playerSeat) {
        this.localSeed = seed;
        this.playerSeat = playerSeat;
        this.table = table;
        this.pendingActions.clear();
        resetAuthoritativeBoards(seed);
    }

    public static class TimedAction {
        public final PlayerAction action;
        public final int tick;

        public TimedAction(PlayerAction action, int tick) {
            this.action = action;
            this.tick = tick;
        }
    }
}