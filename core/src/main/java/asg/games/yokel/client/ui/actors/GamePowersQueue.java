package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerService;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.utils.YokelUtilities;

public class GamePowersQueue extends Table implements GameObject{
    private Logger logger = LoggerService.forClass(GamePowersQueue.class);
    private static final int MAX_VIEWABLE_BLOCKS = 4;

    private Queue<GameBlock> powers;
    private Table powersDisplay;

    public GamePowersQueue(Skin skin){
        super(skin);
        initialize();
    }

    private void initialize() {
        powers = new Queue<>();
        powersDisplay = new Table();
        powersDisplay.align(Align.bottom);
        //powersDisplay.columnAlign(Align.bottom);
        setSize(getPrefWidth(), getPrefHeight());
        //setCullingArea(new Rectangle(getX(), getY(), getWidth(), getHeight()));
        //setClip(true);
        add(getClearBlock()).row();
        add(getClearBlock()).row();
        add(getClearBlock()).row();
        add(getClearBlock()).row();
        add(getClearBlock()).row();
        add(getClearBlock()).row();
        add(getClearBlock()).row();
        add(getClearBlock()).row();
        System.out.println("Games Power Queue: " + this);
        //add(powersDisplay).bottom();

    }

    public void updateQueue(Queue<GameBlock> powerUps) {
        //logger.debug("Entering updateQueue(powerUps=" + powerUps + ")");
        if (powerUps != null) {
            this.powers = powerUps;
        }

        int i = -1;
        powersDisplay.clear();
        for (GameBlock gameBlock : YokelUtilities.safeIterable(powerUps)) {
            if (++i < 8) {
                powersDisplay.add(gameBlock).row();
            }
        }
        System.out.println("powersDisplay: " + powersDisplay);
        //logger.debug("Exiting updateQueue()");
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
        GameBlock gBlock = UIUtil.getBlock(YokelBlock.Y_BLOCK, false);
        System.out.println("gBlock: " + gBlock);
        return UIUtil.getBlock(YokelBlock.Y_BLOCK, false);
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