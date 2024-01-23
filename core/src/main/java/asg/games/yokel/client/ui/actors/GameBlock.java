package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pool;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

import java.util.Objects;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.Copyable;
import asg.games.yokel.utils.YokelUtilities;

/**
 * Created by Blakbro2k on 3/15/2018.
 */

public class GameBlock extends Table implements Pool.Poolable, Copyable<GameBlock> {
    private final static float DEFAULT_ANIMATION_DELAY = 0.12f;
    private final static float DEFENSE_ANIMATION_DELAY = 0.32f;
    private final static float MEDUSA_ANIMATION_DELAY = 0.22f;
    private final static float MIDAS_ANIMATION_DELAY = 0.2f;

    private AnimatedImage uiBlock;
    private int previousBlock = -1;
    private int currentBlock = 6;
    private boolean isActive;
    private boolean isPreview;
    private final float preferredHeight = 16;
    private final float preferredWidth = 16;

    //No-arg constructor required for Pools
    public GameBlock() {
    }

    //New block via image name
    public GameBlock(Skin skin, String blockName, boolean isPreview) {
        super(skin);
        reset();
        this.isPreview = isPreview;
        setImage(blockName);
        add(uiBlock);
    }

    //New block via block type
    public GameBlock(Skin skin, int block, boolean isPreview) {
        super(skin);
        reset();
        this.isPreview = isPreview;
        setImage(block);
        add(uiBlock);
    }

    //New block via block type
   /* public GameBlock(Skin skin, YokelBlock block, boolean isPreview) {
        super(skin);
        if (block == null) throw new GdxRuntimeException("Block cannot be null.");
        setBlock(block);
        reset();
        this.isPreview = isPreview;
        setImage(block.getBlockType());
        add(uiBlock);
    }*/

    public void setBlock(int block) {
        this.previousBlock = this.currentBlock;
        this.currentBlock = block;
    }

    public int getBlock() {
        return currentBlock;
    }

    public void setImage(Image image) {
        setDrawable(image);
    }

    public void setImageBlockName(String name) {
        uiBlock.setName(name);
    }

    public String getImageBlockName() {
        return uiBlock.getName();
    }

    public void setImage(String blockName) {
        if (isPreview) {
            setImage(UIUtil.getInstance().getPreviewBlockImage(blockName));
        } else {
            setImage(UIUtil.getInstance().getBlockImage(blockName));
        }
    }

    public void setImage(int blockValue) {
        if(isPreview){
            setImage(UIUtil.getInstance().getPreviewBlockImage(blockValue));
        } else {
            setImage(UIUtil.getInstance().getBlockImage(blockValue));
        }
    }

    public AnimatedImage getImage() {
        return uiBlock;
    }

    public void setActive(boolean b) {
        this.isActive = b;
    }

    public boolean isActive() {
        return uiBlock != null && isActive;
    }

    public void setPreview(boolean b) {
        this.isPreview = b;
    }

    public boolean isPreview() {
        return isPreview;
    }

    private float getDelay(Image image){
        if(image != null){
            if (YokelUtilities.containsIgnoreCase(image.getName(), "defense")) {
                return DEFENSE_ANIMATION_DELAY;
            } else if (YokelUtilities.containsIgnoreCase(image.getName(), "medusa")) {
                return MEDUSA_ANIMATION_DELAY;
            } else if (YokelUtilities.containsIgnoreCase(image.getName(), "midas")) {
                return MIDAS_ANIMATION_DELAY;
            }
        }
        return DEFAULT_ANIMATION_DELAY;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (uiBlock == null || (previousBlock != currentBlock)) {
            setImage(currentBlock);
            setBlock(currentBlock);
        }
    }

    private void setDrawable(Image image) {
        if (uiBlock == null) {
            uiBlock = new AnimatedImage();
        }
        uiBlock.setDelay(getDelay(image));

        if (image == null) return;
        Drawable drawable;
        if (image instanceof AnimatedImage) {
            drawable = ((AnimatedImage) image).getFrames().get(0);
            uiBlock.setFrames(UIUtil.getAniImageFrames((AnimatedImage) image));
        } else {
            drawable = image.getDrawable();
            uiBlock.setFrames(GdxArrays.newArray(drawable));
        }
        setImageBlockName(image.getName());
        uiBlock.setDrawable(drawable);
        if (YokelUtilities.containsIgnoreCase(getImageBlockName(), "broken")) {
            uiBlock.setPlayOnce(true);
        }

        calculateImageSize(drawable);
    }

    private void calculateImageSize(Drawable drawable) {
        if (getWidth() < 0) {
            YokelUtilities.setWidthFromDrawable(uiBlock, drawable);
            YokelUtilities.setWidthFromDrawable(this, drawable);
        }

        if (getHeight() < 0) {
            YokelUtilities.setHeightFromDrawable(uiBlock, drawable);
            YokelUtilities.setHeightFromDrawable(this, drawable);
        }
    }

    @Override
    public void reset() {
        setX(0);
        setY(0);
        uiBlock = null;
        setPreview(false);
        setActive(false);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        uiBlock.setPosition(x, y);
    }

    public void setCurrentFrame(int frame){
        uiBlock.setCurrentFrame(frame);
    }

    public int getCurrentFrame() {
        return uiBlock.getCurrentFrame();
    }

    @Override
    public float getPrefWidth() {
        Image image = getImage();
        if (image != null) {
            return image.getPrefWidth();
        } else {
            return preferredWidth;
        }
    }

    @Override
    public float getPrefHeight() {
        Image image = getImage();
        if (image != null) {
            return image.getPrefHeight();
        } else {
            return preferredHeight;
        }
    }

    @Override
    public float getWidth() {
        return getPrefWidth();
    }

    @Override
    public float getHeight() {
        return getPrefHeight();
    }

    @Override
    public void setDebug(boolean enabled) {
        super.setDebug(YokelUtilities.setDebug(enabled, uiBlock));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameBlock gameBlock = (GameBlock) o;
        return uiBlock.getName().equals(gameBlock.getName()) && isPreview == gameBlock.isPreview() && isActive == gameBlock.isActive();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uiBlock, isPreview, isActive);
    }

    @Override
    public GameBlock copy() {
        return new GameBlock(getSkin(), this.getName(), this.isPreview);
    }

    @Override
    public GameBlock deepCopy() {
        GameBlock deep = copy();
        deep.setImage(this.getImage());
        deep.setActive(this.isActive());
        return deep;
    }
}