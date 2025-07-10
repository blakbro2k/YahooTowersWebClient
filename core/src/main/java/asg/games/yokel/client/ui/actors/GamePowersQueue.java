package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.client.objects.YokelBlock;
import asg.games.yokel.client.utils.YokelUtilities;

public class GamePowersQueue extends Table implements GameObject {
    private Logger logger = LoggerService.forClass(GamePowersQueue.class);
    private static final int MAX_VIEWABLE_BLOCKS = 8;
    private static final String CELL_ATTR = "uiCell";
    private static final String CELL_ATTR_SEPARATOR = "_";
    private static final String GRID_NAME = "Powers_Display";

    private Queue<GameBlock> powers;
    private final ObjectMap<String, GameBlock> uiBlocks = GdxMaps.newObjectMap();

    public GamePowersQueue(Skin skin) {
        super(skin);
        initialize();
    }

    private void initialize() {
        powers = new Queue<>();
        initializeGrid();
    }

    private void initializeGrid() {
        //this.clearChildren();
        this.setName(GRID_NAME);
        clearUiBlocks();

    }

    public void clearUiBlocks() {
        for (int c = 0; c < MAX_VIEWABLE_BLOCKS; c++) {
            GameBlock uiBlock = getClearBlock();
            uiBlocks.put(getCellAttrName(c), uiBlock);
            add(uiBlock).row();
        }
    }

    private String getCellAttrName(int r) {
        return CELL_ATTR + CELL_ATTR_SEPARATOR + r;
    }

    public void updateQueue(Queue<GameBlock> powerUps) {
        //logger.debug("Entering updateQueue(powerUps=" + powerUps + ")");
        if (powerUps != null) {
            //clearUiBlocks();
            powers = powerUps;
            if (powerUps.size < MAX_VIEWABLE_BLOCKS) {
                for (int x = powerUps.size; x < MAX_VIEWABLE_BLOCKS; x++) {
                    powerUps.addLast(getClearBlock());
                }
            }
        }

        int i = MAX_VIEWABLE_BLOCKS;
        for (GameBlock gameBlock : YokelUtilities.safeIterable(powerUps)) {
            if (--i > 0) {
                setUiBlock(gameBlock, i);
            }
        }
        //logger.debug("Exiting updateQueue()");
    }

    public void updatePowersQueue(Iterable<Integer> powerUps) {
        //logger.debug("Entering updateQueue(powerUps=" + powerUps + ")");
        Queue<GameBlock> gameBlockQueue = new Queue<>();
        for (int gameBlock : YokelUtilities.safeIterable(powerUps)) {
            gameBlockQueue.addFirst(UIUtil.getBlock(gameBlock));
        }
        updateQueue(gameBlockQueue);
        //logger.debug("Exiting updateQueue()");
    }

    private void setUiBlock(GameBlock block, int row) {
        if (row < 0 || row > MAX_VIEWABLE_BLOCKS) {
            return;
        }
        GameBlock uiBlock = uiBlocks.get(getCellAttrName(row));
        if (block != null) {
            uiBlock.setBlock(block.getBlock());
        }
    }

    public void setPowers(Array<Integer> powers) {
        Queue<GameBlock> powerUps = new Queue<>();
        for (int power : YokelUtilities.safeIterable(powers)) {
            if (powers != null) {
                powerUps.addLast(UIUtil.getBlock(power));
            }
        }
        updateQueue(powerUps);
    }

    private void updateGameBlock(Actor gameBlock, GameBlock block) {
        if (gameBlock == null || block == null || !gameBlock.getClass().equals(GameBlock.class))
            return;
        GameBlock gameBlock1 = (GameBlock) gameBlock;
        String imageName = gameBlock1.getName();
        if (!YokelUtilities.equalsIgnoreCase(imageName, block.getName())) {
            gameBlock1.setImage(block.deepCopy().getImage());
            //YokelUtilities.freeBlock(block);
        }
    }

    private GameBlock getClearBlock() {
        return UIUtil.getClearBlock(false);
    }

    public float getPrefWidth() {
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK).getPrefWidth();
    }

    public float getPrefHeight() {
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK).getPrefHeight() * MAX_VIEWABLE_BLOCKS;
    }

    @Override
    public void updateYokelData(String data) {
    }
}