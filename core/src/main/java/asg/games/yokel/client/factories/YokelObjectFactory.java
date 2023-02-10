package asg.games.yokel.client.factories;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

import asg.games.yokel.client.service.UserInterfaceService;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelBlockEval;

public class YokelObjectFactory implements Disposable {
    private final UserInterfaceService userInterfaceService;

    public YokelObjectFactory(UserInterfaceService userInterfaceService, Array<String> images, Array<String> animatedImages){
        if(userInterfaceService == null) throw new GdxRuntimeException("userInterfaceService was not initialized.");
        if(images == null) throw new GdxRuntimeException("Images to load cannot be null.");
        if(animatedImages == null) throw new GdxRuntimeException("Animated Images to load cannot be null.");
        this.userInterfaceService = userInterfaceService;
        userInterfaceService.loadActors(createActors(images, animatedImages));
        userInterfaceService.loadDrawables();
    }

    private Iterable<? extends Actor> createActors(Array<String> imageNames, Array<String> animatedImageNames) {
        Array<Actor> actors = new Array<>();

        for(String imageName : GdxArrays.newSnapshotArray(imageNames)){
            addActor(actors, userInterfaceService.getImage(imageName));
        }

        for(String aniImageName : GdxArrays.newSnapshotArray(animatedImageNames)){
            addActor(actors, userInterfaceService.getAnimatedImage(aniImageName));
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
        //yokelGameBlockPool.clear();
    }

    // YokelBlock pool.
    private final Pool<GameBlock> yokelGameBlockPool = new Pool<GameBlock>() {
        @Override
        protected GameBlock newObject() {
            return new GameBlock(userInterfaceService.getSkin(), getBlockImageName(YokelBlock.CLEAR_BLOCK), false);
        }
    };

    public GameBlock getGameBlock(int blockType, boolean isPreview){
        GameBlock block = yokelGameBlockPool.obtain();

        boolean isBroken = YokelBlockEval.hasBrokenFlag(blockType);
        if(blockType != YokelBlock.CLEAR_BLOCK){
            if(YokelBlockEval.hasAddedByYahooFlag(blockType) || YokelBlockEval.hasBrokenFlag(blockType)){
                blockType = YokelBlockEval.getCellFlag(blockType);
            } else {
                blockType = YokelBlockEval.getIDFlag(YokelBlockEval.getID(blockType), blockType);
            }
        }

        block.setActive(true);
        block.setPreview(isPreview);
        if(isBroken) blockType = YokelBlockEval.addBrokenFlag(blockType);
        block.setImage(blockType);
        return block;
    }

    public void freeObject(GameBlock block){
        if(block != null){
            yokelGameBlockPool.free(block);
        }
    }

    public int getBlockNumber(String blockName){
        switch (blockName) {
            case "Y_block":
            case "power_Y_block":
            case "defense_Y_block":
                return YokelBlock.Y_BLOCK;
            case "O_block":
            case "power_O_block":
            case "defense_O_block":
                return YokelBlock.A_BLOCK;
            case "K_block":
            case "power_K_block":
            case "defense_K_block":
                return YokelBlock.H_BLOCK;
            case "E_block":
            case "power_E_block":
            case "defense_E_block":
                return YokelBlock.Op_BLOCK;
            case "L_block":
            case "power_L_block":
            case "defense_L_block":
                return YokelBlock.Oy_BLOCK;
            case "Bash_block":
            case "power_bash_block":
            case "defense_bash_block":
                return YokelBlock.EX_BLOCK;
            case "top_midas":
                return YokelBlock.TOP_MIDAS;
            case "mid_midas":
                return YokelBlock.MID_MIDAS;
            case "bot_midas":
                return YokelBlock.BOT_MIDAS;
                case "medusa":
                return YokelBlock.MEDUSA;
            case "stone":
                return YokelBlock.STONE;
            case "clear_block" :
            default:
                return YokelBlock.CLEAR_BLOCK;
        }
    }

    public String getBlockImageName(int blockValue){
         if(YokelBlockEval.hasBrokenFlag(blockValue)){
             return getBrokenBlockImageName(YokelBlockEval.getCellFlag(blockValue));
         }
        switch (blockValue) {
            case YokelBlock.CLEAR_BLOCK:
                return "clear_block";
            case YokelBlock.Y_BLOCK:
                return "Y_block";
            case YokelBlock.A_BLOCK:
                return "O_block";
            case YokelBlock.H_BLOCK:
                return "K_block";
            case YokelBlock.Op_BLOCK:
                return "E_block";
            case YokelBlock.Oy_BLOCK:
                return "L_block";
            case YokelBlock.EX_BLOCK:
                return "Bash_block";
            case YokelBlock.TOP_MIDAS:
            case YokelBlock.ACTIVE_TOP_MIDAS:
                return "top_midas";
            case YokelBlock.MID_MIDAS:
            case YokelBlock.ACTIVE_MID_MIDAS:
                return "mid_midas";
            case YokelBlock.BOT_MIDAS:
            case YokelBlock.ACTIVE_BOT_MIDAS:
                return "bottom_midas";
            case YokelBlock.MEDUSA:
            case YokelBlock.ACTIVE_MEDUSA:
                return "medusa";
            case YokelBlock.STONE:
                return "stone";
            case YokelBlock.OFFENSIVE_Y_BLOCK_MINOR:
            case YokelBlock.OFFENSIVE_Y_BLOCK_REGULAR:
            case YokelBlock.OFFENSIVE_Y_BLOCK_MEGA:
                return "power_Y_block";
            case YokelBlock.DEFENSIVE_Y_BLOCK_MINOR:
            case YokelBlock.DEFENSIVE_Y_BLOCK_REGULAR:
            case YokelBlock.DEFENSIVE_Y_BLOCK_MEGA:
                return "defense_Y_block";
            case YokelBlock.OFFENSIVE_O_BLOCK_MINOR:
            case YokelBlock.OFFENSIVE_O_BLOCK_REGULAR:
            case YokelBlock.OFFENSIVE_O_BLOCK_MEGA:
                return "power_O_block";
            case YokelBlock.DEFENSIVE_O_BLOCK_MINOR:
            case YokelBlock.DEFENSIVE_O_BLOCK_REGULAR:
            case YokelBlock.DEFENSIVE_O_BLOCK_MEGA:
                return "defense_O_block";
            case YokelBlock.OFFENSIVE_K_BLOCK_MINOR:
            case YokelBlock.OFFENSIVE_K_BLOCK_REGULAR:
            case YokelBlock.OFFENSIVE_K_BLOCK_MEGA:
                return "power_K_block";
            case YokelBlock.DEFENSIVE_K_BLOCK_MINOR:
            case YokelBlock.DEFENSIVE_K_BLOCK_REGULAR:
            case YokelBlock.DEFENSIVE_K_BLOCK_MEGA:
                return "defense_K_block";
            case YokelBlock.OFFENSIVE_E_BLOCK_MINOR:
            case YokelBlock.OFFENSIVE_E_BLOCK_REGULAR:
            case YokelBlock.OFFENSIVE_E_BLOCK_MEGA:
                return "power_E_block";
            case YokelBlock.DEFENSIVE_E_BLOCK_MINOR:
            case YokelBlock.DEFENSIVE_E_BLOCK_REGULAR:
            case YokelBlock.DEFENSIVE_E_BLOCK_MEGA:
                return "defense_E_block";
            case YokelBlock.OFFENSIVE_L_BLOCK_MINOR:
            case YokelBlock.OFFENSIVE_L_BLOCK_REGULAR:
            case YokelBlock.OFFENSIVE_L_BLOCK_MEGA:
                return "power_L_block";
            case YokelBlock.DEFENSIVE_L_BLOCK_MINOR:
            case YokelBlock.DEFENSIVE_L_BLOCK_REGULAR:
            case YokelBlock.DEFENSIVE_L_BLOCK_MEGA:
                return "defense_L_block";
            case YokelBlock.OFFENSIVE_BASH_BLOCK_MINOR:
            case YokelBlock.OFFENSIVE_BASH_BLOCK_REGULAR:
            case YokelBlock.OFFENSIVE_BASH_BLOCK_MEGA:
                return "power_bash_block";
            case YokelBlock.DEFENSIVE_BASH_BLOCK_MINOR:
            case YokelBlock.DEFENSIVE_BASH_BLOCK_REGULAR:
            case YokelBlock.DEFENSIVE_BASH_BLOCK_MEGA:
                return "defense_Bash_block";
            default:
                return "";
        }
    }

    private String getBrokenBlockImageName(int cellFlag) {
        switch (cellFlag) {
            case YokelBlock.Y_BLOCK:
                return "Y_block_Broken";
            case YokelBlock.A_BLOCK:
                return "O_block_Broken";
            case YokelBlock.H_BLOCK:
                return "K_block_Broken";
            case YokelBlock.Op_BLOCK:
                return "E_block_Broken";
            case YokelBlock.Oy_BLOCK:
                return "L_block_Broken";
            case YokelBlock.EX_BLOCK:
                return "Bash_block_Broken";
            default:
                return "";
        }
    }
}