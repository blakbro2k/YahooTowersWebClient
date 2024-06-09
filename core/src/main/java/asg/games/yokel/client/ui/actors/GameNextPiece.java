package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelPiece;
import asg.games.yokel.utils.YokelUtilities;

public class GameNextPiece extends Table implements GameObject {
    private GameBlock top;
    private GameBlock mid;
    private GameBlock bot;

    public GameNextPiece(Skin skin) {
        this(skin, YokelBlock.CLEAR_BLOCK, YokelBlock.CLEAR_BLOCK, YokelBlock.CLEAR_BLOCK);
    }

    private GameNextPiece(Skin skin, GameBlock top, GameBlock mid, GameBlock bottom) {
        setSkin(skin);
        initialize(top, mid, bottom);
    }

    private GameNextPiece(Skin skin, int top, int mid, int bottom) {
        this(skin, UIUtil.getBlock(top), UIUtil.getBlock(mid), UIUtil.getBlock(bottom));
    }

    public GameNextPiece(Skin skin, String data) {
        this(skin);
        setSkin(skin);
        updateYokelData(data);
    }

    private void initialize(GameBlock top, GameBlock middle, GameBlock bottom){
        if(top == null){
            top = UIUtil.getClearBlock(false);
        }
        if(middle == null){
            middle = UIUtil.getClearBlock(false);
        }
        if(bottom == null){
            bottom = UIUtil.getClearBlock(false);
        }
        this.top = top;
        this.mid = middle;
        this.bot = bottom;

        add(top).row();
        add(middle).row();
        add(bottom).row();
    }

    public void setDebug(boolean enabled) {
        super.setDebug(YokelUtilities.setDebug(enabled, top, mid, bot));
    }

    public void setAsMedusa() {
        setBlockImage(top, YokelBlock.MEDUSA);
        setBlockImage(mid, YokelBlock.MEDUSA);
        setBlockImage(bot, YokelBlock.MEDUSA);
    }

    public void setAsMidas() {
        setBlockImage(top, YokelBlock.TOP_MIDAS);
        setBlockImage(mid, YokelBlock.MID_MIDAS);
        setBlockImage(bot, YokelBlock.BOT_MIDAS);
    }

    @Override
    public void updateYokelData(String data) {
        YokelPiece piece = YokelUtilities.getObjectFromJsonString(YokelPiece.class, data);
        if (piece != null) {
            setBlockImage(top, piece.getBlock3());
            setBlockImage(mid, piece.getBlock2());
            setBlockImage(bot, piece.getBlock1());
        }
    }

    private void setBlockImage(GameBlock gameBlock, int block) {
        if (gameBlock != null) {
            gameBlock.setImage(block);
        }
    }

    public void updateTopBlock(int block) {
        setBlockImage(top, block);
    }

    public void updateMiddleBlock(int block) {
        setBlockImage(mid, block);
    }

    public void updateBottomBlock(int block) {
        setBlockImage(bot, block);
    }
}