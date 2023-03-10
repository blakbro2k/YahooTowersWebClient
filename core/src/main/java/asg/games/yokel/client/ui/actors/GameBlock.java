package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.Gdx;
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
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.utils.YokelUtilities;

/**
 * Created by Blakbro2k on 3/15/2018.
 */

public class GameBlock extends Table implements Pool.Poolable, GameObject, Copyable<GameBlock> {
    private final static float DEFAULT_ANIMATION_DELAY = 0.12f;
    private final static float DEFENSE_ANIMATION_DELAY = 0.32f;
    private final static float MEDUSA_ANIMATION_DELAY = 0.22f;
    private final static float MIDAS_ANIMATION_DELAY = 0.2f;

    private AnimatedImage uiBlock;
    private boolean isActive;
    private boolean isPreview;

    //No-arg constructor required for Pools
    public GameBlock() {}

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

    public void setImage(Image image) {
        setDrawable(image);
    }

    public void setImage(String blockName) {
        Gdx.app.log(this.getClass().getSimpleName(), "start setImage()=" + blockName);
        if(isPreview){
            setImage(UIUtil.getInstance().getPreviewBlockImage(blockName));
        } else {
            setImage(UIUtil.getInstance().getBlockImage(blockName));
        }
        Gdx.app.log(this.getClass().getSimpleName(), "end setImage()=" + blockName);
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
            if(YokelUtilities.containsIgnoreCase(image.getName(), "defense")){
                return DEFENSE_ANIMATION_DELAY;
            } else if(YokelUtilities.containsIgnoreCase(image.getName(), "medusa")){
                return MEDUSA_ANIMATION_DELAY;
            } else if(YokelUtilities.containsIgnoreCase(image.getName(), "midas")){
                return MIDAS_ANIMATION_DELAY;
            }
        }
        return DEFAULT_ANIMATION_DELAY;
    }

    private void setDrawable(Image image) {
        if (uiBlock == null) {
            uiBlock = new AnimatedImage();
        }
        uiBlock.setDelay(getDelay(image));

        if(image == null) return;
        Drawable drawable;
        if (image instanceof AnimatedImage) {
            drawable = ((AnimatedImage) image).getFrames().get(0);
            uiBlock.setFrames(UIUtil.getAniImageFrames((AnimatedImage) image));
        } else {
            drawable = image.getDrawable();
            uiBlock.setFrames(GdxArrays.newArray(drawable));
        }

        setName(image.getName());

        uiBlock.setDrawable(drawable);
        if(YokelUtilities.containsIgnoreCase(getName(), "broken")){
            uiBlock.setPlayOnce(true);
        }
        YokelUtilities.setSizeFromDrawable(uiBlock, drawable);
        YokelUtilities.setSizeFromDrawable(this, drawable);
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
        uiBlock.setName(name);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        uiBlock.setPosition(x, y);
    }

    @Override
    public void setData(String data) {
        YokelBlock block = YokelUtilities.getObjectFromJsonString(YokelBlock.class, data);
        if (block != null) setImage(YokelUtilities.otoi(block));
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
        if(image != null){
            return image.getWidth();
        } else {
            return 20;
        }
    }

    @Override
    public float getPrefHeight() {
        Image image = getImage();
        if(image != null){
            return image.getHeight();
        } else {
            return 20;
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

    public void update(int block, boolean isPreview) {
        Gdx.app.log(this.getClass().getSimpleName(), "start update()");

        if(needsUpdate(block, isPreview)){
            GameBlock blockUi = UIUtil.getBlock(block, isPreview);
            Gdx.app.log(this.getClass().getSimpleName(), "blockUi="+blockUi);

            if(blockUi != null){
                AnimatedImage clone = blockUi.deepCopy().getImage();
                Gdx.app.log(this.getClass().getSimpleName(), "clone=" + clone);

                setImage(clone);
                setName(blockUi.getName());
                UIUtil.freeBlock(blockUi);
            }
        }
        Gdx.app.log(this.getClass().getSimpleName(), "end update()");
    }

    private boolean needsUpdate(int block, boolean isPreview) {
        Gdx.app.log(this.getClass().getSimpleName(), "start needsUpdate()");
        Gdx.app.log(this.getClass().getSimpleName(), "block=" + block);

        String blockName = "";
        Image blockImage;
        boolean isBroken = YokelBlockEval.hasBrokenFlag(block);
        Gdx.app.log(this.getClass().getSimpleName(), "isBroken=" + isBroken);

        block = UIUtil.getTrueBlock(block);
        Gdx.app.log(this.getClass().getSimpleName(), "true block=" + block);

        if(isBroken) block = YokelBlockEval.addBrokenFlag(block);

        if (isPreview) {
            Gdx.app.log(this.getClass().getSimpleName(), "#getting Preview image#");
            blockImage = UIUtil.getInstance().getPreviewBlockImage(block);
        } else {
            Gdx.app.log(this.getClass().getSimpleName(), "#getting NON-Preview image#");
            blockImage = UIUtil.getInstance().getBlockImage(block);
        }

        Gdx.app.log(this.getClass().getSimpleName(), "blockImage=" + blockImage);

        if (blockImage != null) {
            blockName = blockImage.getName();
        }
        Gdx.app.log(this.getClass().getSimpleName(), "blockName=" + blockName);
        Gdx.app.log(this.getClass().getSimpleName(), "end needsUpdate()= " + !YokelUtilities.equalsIgnoreCase(blockName, uiBlock.getName()));
        return !YokelUtilities.equalsIgnoreCase(blockName, uiBlock.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameBlock gameBlock = (GameBlock) o;
        return uiBlock.getName().equals(gameBlock.getName()) && isPreview == gameBlock.isPreview();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uiBlock, isPreview);
    }

    @Override
    public GameBlock copy() {
        return new GameBlock(getSkin(), this.getName(), this.isPreview);
    }

    @Override
    public GameBlock deepCopy() {
        System.out.println("start DeepCopy()");

        GameBlock deep = copy();
        System.out.println("### this: \n" + this);
        System.out.println("### deep: " + deep);
        deep.setImage(this.getImage());
        deep.setActive(this.isActive());
        System.out.println("end DeepCopy()");


        return deep;
    }
}