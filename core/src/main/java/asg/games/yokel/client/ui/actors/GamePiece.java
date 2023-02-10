package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelPiece;
import asg.games.yokel.utils.YokelUtilities;

public class GamePiece extends Table implements GameObject {
    private GameBlock top;
    private GameBlock mid;
    private GameBlock bot;

    public GamePiece(Skin skin){
        this(skin, YokelBlock.CLEAR_BLOCK, YokelBlock.CLEAR_BLOCK, YokelBlock.CLEAR_BLOCK);
    }

    private GamePiece(Skin skin, GameBlock top, GameBlock mid, GameBlock bottom){
        setSkin(skin);
        initialize(top, mid, bottom);
    }

    private GamePiece(Skin skin, int top, int mid, int bottom){
        this(skin, UIUtil.getBlock(top), UIUtil.getBlock(mid), UIUtil.getBlock(bottom));
    }

    public GamePiece(Skin skin, String data){
        this(skin);
        setSkin(skin);
        setData(data);
    }

    private void initialize(GameBlock top, GameBlock middle, GameBlock bottom){
        if(top == null){
            top = new GameBlock(getSkin(), YokelBlock.CLEAR_BLOCK, false);
        }
        if(middle == null){
            middle = new GameBlock(getSkin(), YokelBlock.CLEAR_BLOCK, false);
        }
        if(bottom == null){
            bottom = new GameBlock(getSkin(), YokelBlock.CLEAR_BLOCK, false);
        }
        this.top = top;
        this.mid = middle;
        this.bot = bottom;

        add(top).row();
        add(middle).row();
        add(bottom).row();
    }

    public void setDebug(boolean enabled) {
        super.setDebug(enabled);
    }

    @Override
    public void setData(String data) {
        YokelPiece piece = YokelUtilities.getObjectFromJsonString(YokelPiece.class, data);
        if(piece != null){
            top.update(piece.getBlock3(), false);
            mid.update(piece.getBlock2(), false);
            bot.update(piece.getBlock1(), false);
        }
    }
}