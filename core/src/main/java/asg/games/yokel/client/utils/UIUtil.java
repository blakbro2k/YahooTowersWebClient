package asg.games.yokel.client.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

import asg.games.yokel.client.factories.YokelObjectFactory;
import asg.games.yokel.client.ui.actors.GameBlock;
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
        if(soundUtil != null) {
            return soundUtil.getDuration(sound);
        }
        throw new GdxRuntimeException("Cannot get Sound Duration, was SoundtUtil initialized?");
    }

    public Image getBlockImage(String blockName){
        Gdx.app.log(this.getClass().getSimpleName(), "getBlockImage()=" + blockName);
        return (Image) getFactory().getUserInterfaceService().getActor(blockName);
    }

    public Image getPreviewBlockImage(String blockName){
        Gdx.app.log(this.getClass().getSimpleName(), "getPreviewBlockImage()=" + blockName);
        if(YokelUtilities.containsIgnoreCase(blockName, PREVIEW_TAG)) {
            Gdx.app.log(this.getClass().getSimpleName(), "contains preview tag");
            blockName = blockName.substring(0, blockName.indexOf(PREVIEW_TAG));
            Gdx.app.log(this.getClass().getSimpleName(), "blockName=" + blockName);
        }
        return getBlockImage(blockName + PREVIEW_TAG);
    }

    public Image getBlockImage(int blockId){
        return getBlockImage(getFactory().getBlockImageName(blockId));
    }

    public Image getPreviewBlockImage(int blockId){
        Gdx.app.log(this.getClass().getSimpleName(), "getPreviewBlockImage()=" + blockId);
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

    public void freeObject(GameBlock block) {
        factory.freeObject(block);
    }

    public static GameBlock getBlock(int block){
        return UIUtil.getInstance().getGameBlock(block, false);
    }

    public static GameBlock getBlock(int block, boolean isPreview){
        return UIUtil.getInstance().getGameBlock(block, isPreview);
    }

    public static void setActorNameFromActor(Actor actor, Actor actorToName) {
        if(actor != null && actorToName != null){
            actor.setName(actorToName.getName());
        }
    }

    public static void freeBlock(GameBlock uiCell) {
        UIUtil.getInstance().freeObject(uiCell);
    }

    public static void updateGameBlock(GameBlock original, int block, boolean isPreview) {
        GameBlock incoming = getBlock(block, isPreview);
        //System.out.println("original=" + original);
        //System.out.println("incoming=" + incoming);
        if(original != null && !original.equals(incoming)){
            //System.out.println("equals?=" + original.equals(incoming));
            freeBlock(original);
            original = incoming;
        } else {
            freeBlock(incoming);
        }
    }

    public static int getTrueBlock(int block) {
        if(YokelBlockEval.hasAddedByYahooFlag(block) || YokelBlockEval.hasBrokenFlag(block)){
            return YokelBlockEval.getCellFlag(block);
        } else {
            return YokelBlockEval.getIDFlag(YokelBlockEval.getID(block), block);
        }
    }
}