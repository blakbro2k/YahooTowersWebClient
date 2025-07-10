package asg.games.yokel.client.factories;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

import asg.games.yipee.libgdx.game.YipeeBlockEvalGDX;
import asg.games.yipee.libgdx.objects.YipeeBlockGDX;
import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlock;

public class YokelObjectFactory implements Disposable {
    private final UserInterfaceService userInterfaceService;

    public YokelObjectFactory(UserInterfaceService userInterfaceService, Array<String> images, Array<String> animatedImages){
        if (userInterfaceService == null) {
            throw new GdxRuntimeException("UserInterfaceService was not initialized.");
        }
        if (images == null) throw new GdxRuntimeException("Images to load cannot be null.");
        if(animatedImages == null) throw new GdxRuntimeException("Animated Images to load cannot be null.");
        this.userInterfaceService = userInterfaceService;
        userInterfaceService.loadActors(createActorsArray(images, animatedImages));
        userInterfaceService.loadDrawables();
    }

    private Iterable<? extends Actor> createActorsArray(Array<String> imageNames, Array<String> animatedImageNames) {
        Array<Actor> actors = new Array<>();

        for (String imageName : GdxArrays.newSnapshotArray(imageNames)) {
            addActor(actors, userInterfaceService.getNewImage(imageName));
        }

        for (String aniImageName : GdxArrays.newSnapshotArray(animatedImageNames)) {
            addActor(actors, userInterfaceService.getNewAnimatedImage(aniImageName));
        }
        return actors;
    }

    private void addActor(Array<Actor> actors, Actor actor){
        if(actor != null && actors != null){
            actors.add(actor);
        }
    }

    public UserInterfaceService getUserInterfaceService() {
        return userInterfaceService;
    }

    @Override
    public void dispose() {
        yokelGameBlockPool.clear();
    }

    // YipeeBlockGDX pool.
    private final Pool<GameBlock> yokelGameBlockPool = new Pool<GameBlock>() {
        @Override
        protected GameBlock newObject() {
            return new GameBlock(userInterfaceService.getSkin(), getBlockImageName(YipeeBlockGDX.CLEAR_BLOCK), false);
        }
    };

    public GameBlock getGameBlock(int blockType, boolean isPreview){
        GameBlock block = yokelGameBlockPool.obtain();
        block.setBlock(blockType);

        boolean isBroken = YipeeBlockEvalGDX.hasBrokenFlag(blockType);
        if (blockType != YipeeBlockGDX.CLEAR_BLOCK) {
            if (YipeeBlockEvalGDX.hasAddedByYahooFlag(blockType) || YipeeBlockEvalGDX.hasBrokenFlag(blockType)) {
                blockType = YipeeBlockEvalGDX.getCellFlag(blockType);
            } else {
                blockType = YipeeBlockEvalGDX.getIDFlag(YipeeBlockEvalGDX.getID(blockType), blockType);
            }
        }

        block.setActive(true);
        block.setPreview(isPreview);
        if (isBroken) blockType = YipeeBlockEvalGDX.addBrokenFlag(blockType);
        block.setImage(blockType);
        return block;
    }

    public void freeObject(GameBlock block) {
        if (block != null) {
            yokelGameBlockPool.free(block);
        }
    }

    public static String getBrokenBlockName(int block) {
        switch (block) {
            case YipeeBlockGDX.Y_BLOCK:
                return "Y_block_broken";
            case YipeeBlockGDX.A_BLOCK:
                return "O_block_broken";
            case YipeeBlockGDX.H_BLOCK:
                return "K_block_broken";
            case YipeeBlockGDX.Op_BLOCK:
                return "E_block_broken";
            case YipeeBlockGDX.Oy_BLOCK:
                return "L_block_broken";
            case YipeeBlockGDX.EX_BLOCK:
                return "bash_block_broken";
            default:
                return "clear_block";
        }
    }

    public int getBlockNumber(String blockName) {
        switch (blockName) {
            case "Y_block":
            case "power_Y_block":
            case "defense_Y_block":
                return YipeeBlockGDX.Y_BLOCK;
            case "O_block":
            case "power_O_block":
            case "defense_O_block":
                return YipeeBlockGDX.A_BLOCK;
            case "K_block":
            case "power_K_block":
            case "defense_K_block":
                return YipeeBlockGDX.H_BLOCK;
            case "E_block":
            case "power_E_block":
            case "defense_E_block":
                return YipeeBlockGDX.Op_BLOCK;
            case "L_block":
            case "power_L_block":
            case "defense_L_block":
                return YipeeBlockGDX.Oy_BLOCK;
            case "bash_block":
            case "power_bash_block":
            case "defense_bash_block":
                return YipeeBlockGDX.EX_BLOCK;
            case "top_midas":
                return YipeeBlockGDX.TOP_MIDAS;
            case "mid_midas":
                return YipeeBlockGDX.MID_MIDAS;
            case "bot_midas":
                return YipeeBlockGDX.BOT_MIDAS;
                case "medusa":
                    return YipeeBlockGDX.MEDUSA;
            case "stone":
                return YipeeBlockGDX.STONE;
            case "clear_block" :
            default:
                return YipeeBlockGDX.CLEAR_BLOCK;
        }
    }

    public String getBlockImageName(int blockValue){
        if (YipeeBlockEvalGDX.hasBrokenFlag(blockValue)) {
            return getBrokenBlockImageName(YipeeBlockEvalGDX.getCellFlag(blockValue));
         }
        switch (blockValue) {
            case YipeeBlockGDX.CLEAR_BLOCK:
                return "clear_block";
            case YipeeBlockGDX.Y_BLOCK:
                return "Y_block";
            case YipeeBlockGDX.A_BLOCK:
                return "O_block";
            case YipeeBlockGDX.H_BLOCK:
                return "K_block";
            case YipeeBlockGDX.Op_BLOCK:
                return "E_block";
            case YipeeBlockGDX.Oy_BLOCK:
                return "L_block";
            case YipeeBlockGDX.EX_BLOCK:
                return "bash_block";
            case YipeeBlockGDX.TOP_MIDAS:
            case YipeeBlockGDX.ACTIVE_TOP_MIDAS:
                return "top_midas";
            case YipeeBlockGDX.MID_MIDAS:
            case YipeeBlockGDX.ACTIVE_MID_MIDAS:
                return "mid_midas";
            case YipeeBlockGDX.BOT_MIDAS:
            case YipeeBlockGDX.ACTIVE_BOT_MIDAS:
                return "bottom_midas";
            case YipeeBlockGDX.MEDUSA:
            case YipeeBlockGDX.ACTIVE_MEDUSA:
                return "medusa";
            case YipeeBlockGDX.STONE:
                return "stone";
            case YipeeBlockGDX.OFFENSIVE_Y_BLOCK_MINOR:
            case YipeeBlockGDX.OFFENSIVE_Y_BLOCK_REGULAR:
            case YipeeBlockGDX.OFFENSIVE_Y_BLOCK_MEGA:
                return "power_Y_block";
            case YipeeBlockGDX.DEFENSIVE_Y_BLOCK_MINOR:
            case YipeeBlockGDX.DEFENSIVE_Y_BLOCK_REGULAR:
            case YipeeBlockGDX.DEFENSIVE_Y_BLOCK_MEGA:
                return "defense_Y_block";
            case YipeeBlockGDX.OFFENSIVE_O_BLOCK_MINOR:
            case YipeeBlockGDX.OFFENSIVE_O_BLOCK_REGULAR:
            case YipeeBlockGDX.OFFENSIVE_O_BLOCK_MEGA:
                return "power_O_block";
            case YipeeBlockGDX.DEFENSIVE_O_BLOCK_MINOR:
            case YipeeBlockGDX.DEFENSIVE_O_BLOCK_REGULAR:
            case YipeeBlockGDX.DEFENSIVE_O_BLOCK_MEGA:
                return "defense_O_block";
            case YipeeBlockGDX.OFFENSIVE_K_BLOCK_MINOR:
            case YipeeBlockGDX.OFFENSIVE_K_BLOCK_REGULAR:
            case YipeeBlockGDX.OFFENSIVE_K_BLOCK_MEGA:
                return "power_K_block";
            case YipeeBlockGDX.DEFENSIVE_K_BLOCK_MINOR:
            case YipeeBlockGDX.DEFENSIVE_K_BLOCK_REGULAR:
            case YipeeBlockGDX.DEFENSIVE_K_BLOCK_MEGA:
                return "defense_K_block";
            case YipeeBlockGDX.OFFENSIVE_E_BLOCK_MINOR:
            case YipeeBlockGDX.OFFENSIVE_E_BLOCK_REGULAR:
            case YipeeBlockGDX.OFFENSIVE_E_BLOCK_MEGA:
                return "power_E_block";
            case YipeeBlockGDX.DEFENSIVE_E_BLOCK_MINOR:
            case YipeeBlockGDX.DEFENSIVE_E_BLOCK_REGULAR:
            case YipeeBlockGDX.DEFENSIVE_E_BLOCK_MEGA:
                return "defense_E_block";
            case YipeeBlockGDX.OFFENSIVE_L_BLOCK_MINOR:
            case YipeeBlockGDX.OFFENSIVE_L_BLOCK_REGULAR:
            case YipeeBlockGDX.OFFENSIVE_L_BLOCK_MEGA:
                return "power_L_block";
            case YipeeBlockGDX.DEFENSIVE_L_BLOCK_MINOR:
            case YipeeBlockGDX.DEFENSIVE_L_BLOCK_REGULAR:
            case YipeeBlockGDX.DEFENSIVE_L_BLOCK_MEGA:
                return "defense_L_block";
            case YipeeBlockGDX.OFFENSIVE_BASH_BLOCK_MINOR:
            case YipeeBlockGDX.OFFENSIVE_BASH_BLOCK_REGULAR:
            case YipeeBlockGDX.OFFENSIVE_BASH_BLOCK_MEGA:
                return "power_bash_block";
            case YipeeBlockGDX.DEFENSIVE_BASH_BLOCK_MINOR:
            case YipeeBlockGDX.DEFENSIVE_BASH_BLOCK_REGULAR:
            case YipeeBlockGDX.DEFENSIVE_BASH_BLOCK_MEGA:
                return "defense_bash_block";
            default:
                return "";
        }
    }

    private String getBrokenBlockImageName(int cellFlag) {
        switch (cellFlag) {
            case YipeeBlockGDX.Y_BLOCK:
                return "Y_block_broken";
            case YipeeBlockGDX.A_BLOCK:
                return "O_block_broken";
            case YipeeBlockGDX.H_BLOCK:
                return "K_block_broken";
            case YipeeBlockGDX.Op_BLOCK:
                return "E_block_broken";
            case YipeeBlockGDX.Oy_BLOCK:
                return "L_block_broken";
            case YipeeBlockGDX.EX_BLOCK:
                return "bash_block_broken";
            default:
                return "";
        }
    }
}