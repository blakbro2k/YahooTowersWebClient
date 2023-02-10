package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.SnapshotArray;
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

    private void initialize(){
        powers = new Queue<>();
        powersDisplay = new Table();
        powersDisplay.align(Align.bottom);
        //powersDisplay.columnAlign(Align.bottom);
        setSize(getPrefWidth(), getPrefHeight());
        setCullingArea(new Rectangle(getX(), getY(), getWidth(), getHeight()));
        setClip(true);
        add(powersDisplay).bottom();
        powersDisplay.add(getClearBlock()).row();
        powersDisplay.add(getClearBlock()).row();
        powersDisplay.add(getClearBlock()).row();
        powersDisplay.add(getClearBlock()).row();
        powersDisplay.add(getClearBlock()).row();
        powersDisplay.add(getClearBlock()).row();
        powersDisplay.add(getClearBlock()).row();
        powersDisplay.add(getClearBlock()).row();
    }

    public void updateQueue(Queue<GameBlock> powerUps){
        //logger.debug("Entering updateQueue(powerUps=" + powerUps + ")");
        if(powerUps != null) {
            this.powers = powerUps;
        }
        SnapshotArray<Actor> children = powersDisplay.getChildren();
        //Actor[] actors = children.begin();
        for(int i = 0; i < powers.size;  i++){
            if(i < children.size){
                if(children.get(i) != null) {
                    updateGameBlock(children.get(i), powers.get(i));
                }
            } else {
                powersDisplay.addActor(powers.get(i));
            }
        }
        //children.end();
        //flush the display
        for(int f = powers.size; f < children.size; f++){
            children.removeIndex(f);
        }
        //logger.debug("Exiting updateQueue()");
    }

    private void updateGameBlock(Actor gameBlock, GameBlock block) {
        if(gameBlock == null || block == null || !gameBlock.getClass().equals(GameBlock.class)) return;
        GameBlock gameBlock1 = (GameBlock) gameBlock;
        String imageName = gameBlock1.getName();
        if(!YokelUtilities.equalsIgnoreCase(imageName, block.getName())){
            gameBlock1.setImage(block.deepCopy().getImage());
            //YokelUtilities.freeBlock(block);
        }
    }

    private GameBlock getClearBlock(){
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK, false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public float getPrefWidth() {
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK).getPrefWidth();
    }

    public float getPrefHeight() {
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK).getPrefHeight() * MAX_VIEWABLE_BLOCKS;
    }

    @Override
    public void setData(String data) {
    }
}