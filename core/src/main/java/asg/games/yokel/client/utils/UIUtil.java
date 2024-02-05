package asg.games.yokel.client.utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

import asg.games.yokel.client.factories.YokelObjectFactory;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.ui.actors.GameBrokenBlockSpriteContainer;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.utils.YokelUtilities;

public class UIUtil {
    private static final UIUtil myInstance = new UIUtil();
    static final private String PREVIEW_TAG = "_preview";
    private SoundUtil soundUtil;

    private YokelObjectFactory factory;
    public static UIUtil getInstance(){
        return myInstance;
    }

    public void setFactory(YokelObjectFactory factory){
        this.factory = factory;
    }

    public YokelObjectFactory getFactory(){
        if (factory == null) throw new GdxRuntimeException("YokelObjectFactory was not set.  Please call setFactory(YokelObjectFactory factory) to set an active YokelObjectFactory object");
        return factory;
    }

    public void setSoundUtil(SoundUtil soundUtil) {
        if(soundUtil != null) {
            this.soundUtil = soundUtil;
        }
    }

    public float getSoundDuration(Sound sound) {
        if (soundUtil != null) {
            return soundUtil.getDuration(sound);
        }
        throw new GdxRuntimeException("Cannot get Sound Duration, was SoundUtil initialized?");
    }

    public Image getBlockImage(String blockName) {
        return (Image) getFactory().getUserInterfaceService().getActor(blockName);
    }

    public Image getBrokenBlockImage(String brokenBlockName) {
        return (Image) getFactory().getUserInterfaceService().getActor(brokenBlockName);
    }

    public Image getPreviewBlockImage(String blockName) {
        if (YokelUtilities.containsIgnoreCase(blockName, PREVIEW_TAG)) {
            blockName = blockName.substring(0, blockName.indexOf(PREVIEW_TAG));
        }
        return getBlockImage(blockName + PREVIEW_TAG);
    }

    public Image getBlockImage(int blockId) {
        return getBlockImage(getFactory().getBlockImageName(blockId));
    }

    public Image getPreviewBlockImage(int blockId) {
        return getBlockImage(getFactory().getBlockImageName(blockId) + PREVIEW_TAG);
    }

    public static Array<Drawable> getAniImageFrames(AnimatedImage image){
        Array<Drawable> drawables = new Array<>();
        if(image != null){
            for(Drawable frame : YokelUtilities.safeIterable(image.getFrames())){
                if(frame != null){
                    drawables.add(frame);
                }
            }
        }
        return drawables;
    }

    public GameBlock getGameBlock(int blockId, boolean preview){
        return getFactory().getGameBlock(blockId, preview);
    }

    public static GameBlock getBlock(int block) {
        return UIUtil.getInstance().getGameBlock(block, false);
    }

    public static GameBlock getBlock(int block, boolean isPreview) {
        return UIUtil.getInstance().getGameBlock(block, isPreview);
    }

    public static Image getBrokenBlock(String brokenBlockName) {
        return UIUtil.getInstance().getBrokenBlockImage(brokenBlockName);
    }

    public static void setActorNameFromActor(Actor actor, Actor actorToName) {
        if (actor != null && actorToName != null) {
            actor.setName(actorToName.getName());
        }
    }

    public static void freeBlock(GameBlock uiCell) {
        Pools.free(uiCell);
    }

    public static void updateGameBlock(GameBlock original, int block, boolean isPreview) {
        GameBlock incoming = getBlock(block, isPreview);
        if(original != null && !original.equals(incoming)){
            Pools.free(original);
            original = incoming;
        } else {
            Pools.free(incoming);
        }
    }

    public static int getTrueBlock(int block) {
        if (YokelBlockEval.hasAddedByYahooFlag(block) || YokelBlockEval.hasBrokenFlag(block)) {
            return YokelBlockEval.getCellFlag(block);
        } else {
            return YokelBlockEval.getIDFlag(YokelBlockEval.getID(block), block);
        }
    }

    public static Image getNewImage(Image image) {
        Image nuImage = null;
        if (image != null) {
            nuImage = new Image(image.getDrawable());
        }
        return nuImage;
    }

    public static GameBrokenBlockSpriteContainer getBrokenBlockSprites(GameBlock parent, int block) {
        String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YokelBlockEval.addBrokenFlag(block));
        GameBrokenBlockSpriteContainer spriteContainer = Pools.obtain(GameBrokenBlockSpriteContainer.class);
        Image left = getNewImage(myInstance.getBlockImage(brokenBlockString + "_left"));
        Image bottom = getNewImage(myInstance.getBlockImage(brokenBlockString + "_bottom"));
        Image right = getNewImage(myInstance.getBlockImage(brokenBlockString + "_right"));
        parent.addActor(left);
        parent.addActor(bottom);
        parent.addActor(right);

        spriteContainer.setParentGameBlock(parent);
        spriteContainer.setLeftSprite(left);
        spriteContainer.setBottomSprite(bottom);
        spriteContainer.setRightSprite(right);

        return spriteContainer;
    }
}