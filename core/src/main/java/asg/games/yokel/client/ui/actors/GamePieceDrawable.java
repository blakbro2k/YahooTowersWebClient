package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelPiece;

class GamePieceDrawable extends Actor {
    private final GameBlock[] blocks = new GameBlock[3];
    private Skin skin;
    private int row;
    private int col;
    private boolean isActive;
    private float fallOffset;
    private GameBlockGrid parent;

    GamePieceDrawable(Skin skin) {
        setSkin(skin);
        blocks[0] = UIUtil.getClearBlock(false);
        blocks[1] = UIUtil.getClearBlock(false);
        blocks[2] = UIUtil.getClearBlock(false);
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }

    void setActive(boolean b) {
        isActive = b;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isActive) {

            for (int i = 0; i < 3; i++) {
                if (blocks[i] != null) {
                    blocks[i].setDelay(2.05f);
                    blocks[i].act(delta);
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
        if (isActive) {
            computePosition();
            float x = getX();
            float y = getY();

            for (int i = 0; i < 3; i++) {
                if (this.blocks[i] != null) {
                    this.blocks[i].setPosition(x, y + (i * blocks[i].getHeight() / 2));
                    this.blocks[i].draw(batch, alpha);
                }
            }
        }
    }

    void setBlocks(YokelPiece piece) {
        if (piece != null && isActive) {
            updateIndex(0, piece.getBlock1());
            updateIndex(1, piece.getBlock2());
            updateIndex(2, piece.getBlock3());
            this.row = piece.row;
            this.col = piece.column;
        }
    }

    private void updateIndex(int index, int block) {
        if (index > -1 && index < blocks.length && blocks[index] != null) {
            blocks[index].setImage(block);
        }
    }

    void setParent(GameBlockGrid area) {
        this.parent = area;
    }

    private void computePosition() {
        GameBlock block = blocks[0];

        if (block != null) {
            //Set the x to the position of the grid
            Vector2 pos = localToParentCoordinates(new Vector2(0, 0));

            float SIDE_BAR_OFFSET = 12;
            pos.x = pos.x / 2 + SIDE_BAR_OFFSET;
            pos.y = pos.y / 2;

            pos.x -= block.getWidth();
            if (parent != null && parent.isDownCellFree(col, row)) {
                pos.y -= ((1 - fallOffset) * block.getHeight() / 2);
            }

            float offSetX = block.getWidth() / 2 * col;
            float offSetY = block.getHeight() / 2 * row;

            this.setPosition(pos.x + offSetX, pos.y + offSetY);
        }
    }

    void setFallOffset(float fallOffset) {
        this.fallOffset = fallOffset;
    }
}