package asg.games.yokel.client.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import asg.games.yipee.common.game.GameBoardState;
import asg.games.yipee.common.game.PlayerAction;
import asg.games.yipee.libgdx.game.YipeeGameBoardGDX;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.service.ClientPredictionService;
import asg.games.yokel.client.utils.LogUtil;
import asg.games.yokel.client.utils.YokelUtilities;
import lombok.Getter;
import lombok.Setter;


/**
 * The GameManager class serves as the backbone for managing a Yipee game session.
 * It handles the game loop, player actions, and the state of each game board for up to 8 players.
 * <p>
 * Key Responsibilities:
 * - Initializes game boards and players for each seat.
 * - Manages a fixed-timestep game loop and player actions.
 * - Synchronizes game states and broadcasts updates to clients.
 * - Provides hooks for game-specific logic like state broadcasting and win/loss conditions.
 * <p>
 * Thread Safety:
 * - Uses thread-safe data structures such as ConcurrentHashMap and ConcurrentLinkedQueue.
 * - Employs executors for managing game loop and player action processing.
 */
@Setter
@Getter
public class ClientGameManager implements Disposable {
    private LoggerService loggerService;
    private ClientPredictionService clientPredictionService;
    private Log4LibGDXLogger logger;

    private static final String CONST_TITLE = "Yipee! Game Manager";
    private final Queue<PlayerAction> playersActionQueue = new Queue<>(); // Stores pending player actions
    private final ObjectMap<Integer, Array<GameSeatActionPair>> tickToActions = GdxMaps.newObjectMap();
    private final ObjectMap<Integer, GamePlayerBoard> gameBoardMap = GdxMaps.newObjectMap(); // Maps seat IDs to game boards
    private long gameSeed = 0;
    private int tickRate = 0;
    private float accumulatedTime = 0;
    private int currentTick = 0;
    private boolean isRunning;

    /**
     * Constructor initializes game boards, executors, and logging for game session setup.
     */
    public ClientGameManager(long seed, int tickRate, YipeeTableGDX table, int playerSeat, LoggerService loggerService, ClientPredictionService clientPredictionService) {
        this.loggerService = loggerService;
        this.clientPredictionService = clientPredictionService;
        logger = LogUtil.getLogger(loggerService, this.getClass());
        logger.enter("ClientGameManager");
        initialize(seed, tickRate);

        initializePrediction(playerSeat, seed, table);
        logger.exit("ClientGameManager");
    }

    public void initialize(long gameSeed, int tickRate) {
        logger.enter("initialize");
        System.out.println("initialize");

        this.gameSeed = gameSeed;
        this.tickRate = tickRate;

        logger.debug("gameSeed={}", gameSeed);
        logger.debug("tickRate={}", tickRate);
        //Set up 8 actions array and 8 board snapshot arrays
        for (int index = 0; index < 8; index++) {
            gameBoardMap.put(index, new GamePlayerBoard(gameSeed));
        }
        logger.debug("gameBoardMap={}", gameBoardMap);
        logger.exit("initialize");
    }

    public void initializePrediction(int playerSeat, long seed, YipeeTableGDX table) {
        logger.enter("initializePrediction");
        clientPredictionService = new ClientPredictionService();
        clientPredictionService.initialize(seed, table, playerSeat);
        setGameSeed(seed);
        logger.exit("initializePrediction");
    }

    public void update(float delta) {
        accumulatedTime += delta;
        float tickInterval = 1f / tickRate;

        while (accumulatedTime >= tickInterval) {
            currentTick++;
            applyActionsForTick(currentTick);
            accumulatedTime -= tickInterval;
        }
    }

    private void applyActionsForTick(int tick) {
        Array<GameSeatActionPair> actions = tickToActions.remove(tick); // consume once
        if (actions == null) return;

        for (GameSeatActionPair pair : actions) {
            processPlayerAction(pair.getAction(), 0f);
        }
    }

    public void addAction(GameSeatActionPair actionPair) {
        Array<GameSeatActionPair> gameActions = tickToActions.get(actionPair.getTick());
        gameActions.add(actionPair);
    }

    @Override
    public void dispose() {
        playersActionQueue.clear();
        gameBoardMap.clear();
        tickToActions.clear();
    }

    public Array<YipeePlayerGDX> getWinners() {
        return GdxArrays.newArray();
    }

    public GameBoardState getBoardState(int seatNumber) {
        return gameBoardMap.get(seatNumber).getBoard().exportGameState();
    }

    /**
     * Validates that the seat ID is within acceptable bounds (0-7).
     * Adjust this if using 1-based indexing for seats externally.
     *
     * @param seatId the seat ID to validate
     * @throws IllegalArgumentException if the seat ID is out of bounds
     */
    private void validateSeat(int seatId) {
        if (seatId < 0 || seatId > 7) {
            //logger.error("Seat ID [{}] is out of bounds. Valid range is 0-7.", seatId);
            throw new IllegalArgumentException("Seat ID must be between 0 and 7.");
        }
    }

    /**
     * Retrieves the {@link YipeeGameBoardGDX} game board associated with a specific seat ID.
     *
     * @param seatId the ID of the seat (1-8)
     * @return the {@link YipeeGameBoardGDX} instance or null if none exists
     */
    public YipeeGameBoardGDX getGameBoard(int seatId) {
        validateSeat(seatId);
        GamePlayerBoard gameBoardObj = gameBoardMap.get(seatId);
        YipeeGameBoardGDX board = null;
        if (gameBoardObj != null) {
            board = gameBoardObj.getBoard();
        }
        return board;
    }

    public YipeeGameBoardGDX getPartnerGameBoard(int seatId) {
        validateSeat(seatId);
        int partnerSeat = (seatId % 2 == 0) ? seatId + 1 : seatId - 1;
        return getGameBoard(partnerSeat);
    }

    /**
     * Retrieves a {@link YipeePlayerGDX} player associated with a specific seat ID.
     *
     * @param seatId the ID of the seat (1-8)
     * @return the {@link YipeePlayerGDX} player or null if none exists
     */
    public YipeePlayerGDX getGameBoardPlayer(int seatId) {
        validateSeat(seatId);
        GamePlayerBoard gameBoardObj = gameBoardMap.get(seatId);
        YipeePlayerGDX player = null;
        if (gameBoardObj != null) {
            player = gameBoardObj.getPlayer();
        }
        return player;
    }

    /**
     * Retrieves the {@link YipeeGameBoardGDX} Game States associated with a specific seat ID.
     *
     * @param seatId the ID of the seat (1-8)
     * @return the YipeeGameBoardGDX instance or null if none exists
     */
    public Queue<GameBoardState> getGameBoardStates(int seatId) {
        validateSeat(seatId);
        GamePlayerBoard gameBoardObj = gameBoardMap.get(seatId);
        Queue<GameBoardState> states = null;
        if (gameBoardObj != null) {
            states = gameBoardObj.getGameBoardStates();
        }
        return states;
    }

    /**
     * Sets a {@link YipeePlayerGDX} player in the given seatId to associate with the {@link YipeeGameBoardGDX} GameBoard.
     *
     * @param seatId
     * @param player
     */
    public void setGameBoardObjectPlayer(int seatId, YipeePlayerGDX player) {
        validateSeat(seatId);
        GamePlayerBoard gameBoardObj = gameBoardMap.get(seatId);
        if (gameBoardObj != null) {
            gameBoardObj.setPlayer(player);
        }
    }


    /**
     * Resets all game boards and clears associated states.
     */
    public void resetGameBoards() {
        for (int seatId = 0; seatId < 8; seatId++) {
            resetGameBoard(gameSeed, seatId);
        }
    }

    /**
     * Resets the given board.
     *
     * @param seed the gameseed
     */
    public void resetGameBoard(long seed, int seatId) {
        GamePlayerBoard gameBoard = gameBoardMap.get(seatId);
        if (gameBoard != null) {
            gameBoard.reset(seed);
        }
    }

    /**
     * Processes a single player action and updates the target game board.
     *
     * @param action the player action to process
     * @param delta  the time step for the game loop
     */
    public void processPlayerAction(PlayerAction action, float delta) {
        int targetSeatId = action.getTargetBoardId();
        YipeeGameBoardGDX board = getGameBoard(targetSeatId);
        YipeeGameBoardGDX partnerBoard = getPartnerGameBoard(targetSeatId);

        // logger.info("BoardSeat: {} is taking action: {} on target boardSeat: {}.",
        // action.getInitiatingBoardId(), action.getActionType(), action.getTargetBoardId());

        if (board == null) {
            //logger.error("No game board found for seat [{}]. Skipping action [{}].", targetSeatId, action.getActionType());
            return;
        }

        //  logger.debug("Processing action [{}] for seat [{}]", action.getActionType(), targetSeatId);

        board.updateGameState(delta, YokelUtilities.getYipeeGDXGameState(board.exportGameState()), YokelUtilities.getYipeeGDXGameState(partnerBoard.exportGameState()));
        board.applyPlayerAction(action);
        //addGameState(targetSeatId, board.exportGameState());
    }


    /**
     * Stops the game server by shutting down executors and cleaning up resources.
     */
    public void stop() {
        //logger.info("Attempting to shutdown GameServer...");
    }

    /**
     * @param action
     */
    public void addPlayerAction(PlayerAction action) {
        playersActionQueue.addFirst(action);
    }

    /**
     * @param seatId
     * @return YipeeGameBoardGDX
     */
    public GameBoardState getLatestGameBoardState(int seatId) {
        Queue<GameBoardState> states = getGameBoardStates(seatId);
        if (!states.isEmpty()) {
            return states.first(); // Access a specific game board
        } else {
            return null;
        }
    }

    /**
     * Finds the seat ID for the given player.
     *
     * @param player the player to look for
     * @return the seat ID or -1 if not found
     */
    public int getSeatForPlayer(YipeePlayerGDX player) {
        for (ObjectMap.Entry<Integer, GamePlayerBoard> entry : gameBoardMap.entries()) {
            YipeePlayerGDX p = entry.value.getPlayer();
            if (p != null && p.equals(player)) {
                return entry.key;
            }
        }
        return -1;
    }

    /**
     * Returns the partner's GamePlayerBoard (full wrapper) for the given player.
     */
    public GamePlayerBoard getPartnerBoard(YipeePlayerGDX player) {
        int playerSeat = getSeatForPlayer(player);
        if (playerSeat == -1) return null;
        int partnerSeat = (playerSeat % 2 == 0) ? playerSeat + 1 : playerSeat - 1;
        return gameBoardMap.get(partnerSeat);
    }

    /**
     * Returns the partner's raw YipeeGameBoardGDX for the given player.
     */
    public YipeeGameBoardGDX getPartnerGameBoard(YipeePlayerGDX player) {
        GamePlayerBoard partner = getPartnerBoard(player);
        return partner != null ? partner.getBoard() : null;
    }

    /**
     * Returns a map of seat ID to enemy GamePlayerBoards (full wrapper).
     */
    public ObjectMap<Integer, GamePlayerBoard> getEnemyBoards(YipeePlayerGDX player) {
        ObjectMap<Integer, GamePlayerBoard> enemies = GdxMaps.newObjectMap();
        int playerSeat = getSeatForPlayer(player);
        if (playerSeat == -1) return enemies;
        int partnerSeat = (playerSeat % 2 == 0) ? playerSeat + 1 : playerSeat - 1;

        for (ObjectMap.Entry<Integer, GamePlayerBoard> entry : gameBoardMap.entries()) {
            int seatId = entry.key;
            if (seatId != playerSeat && seatId != partnerSeat) {
                GamePlayerBoard board = entry.value;
                if (board.getPlayer() != null) {
                    enemies.put(seatId, board);
                }
            }
        }
        return enemies;
    }

    /**
     * Returns a map of seat ID to raw YipeeGameBoardGDX for all enemies.
     */
    public ObjectMap<Integer, YipeeGameBoardGDX> getEnemyGameBoards(YipeePlayerGDX player) {
        ObjectMap<Integer, YipeeGameBoardGDX> enemyBoards = GdxMaps.newObjectMap();
        for (ObjectMap.Entry<Integer, GamePlayerBoard> entry : getEnemyBoards(player).entries()) {
            enemyBoards.put(entry.key, entry.value.getBoard());
        }
        return enemyBoards;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPlayerDead(int gameSeat) {
        GamePlayerBoard gameBoard = gameBoardMap.get(gameSeat);
        if (gameBoard != null) {
            return gameBoard.isBoardDead();
        }
        return true;
    }

    /**
     * Helper class that represents one seat with information on the game states and the player seated
     */
    @Getter
    @Setter
    public static class GamePlayerBoard {
        private YipeePlayerGDX player;
        private YipeeGameBoardGDX board;
        private final Queue<GameBoardState> gameBoardStates = new Queue<>(); // Tracks states by seat ID

        public boolean addState(GameBoardState state) {
            gameBoardStates.addFirst(state);
            return true;
        }

        public boolean removeState(GameBoardState state) {
            return gameBoardStates.removeValue(state, false);
        }

        public GamePlayerBoard() {
            this(-1);
        }

        public GamePlayerBoard(long seed) {
            board = new YipeeGameBoardGDX(seed);
        }

        /**
         * Resets the board with a default seed of -1
         */
        public void reset() {
            reset(-1);
        }

        /**
         * Resets the board with the given seed
         *
         * @param seed Game seed
         */
        public void reset(long seed) {
            setBoardSeed(seed);
            setPlayer(null);
            gameBoardStates.clear();
        }

        public void setBoardSeed(long seed) {
            board.reset(seed);
        }

        public void startBoard() {
            board.begin();
        }

        public void stopBoard() {
            board.end();
        }

        public GameBoardState getLatestGameState() {
            return gameBoardStates.first();
        }

        public boolean isBoardDead() {
            if (board == null) {
                return true;
            }
            return board.hasPlayerDied();
        }
    }
}
