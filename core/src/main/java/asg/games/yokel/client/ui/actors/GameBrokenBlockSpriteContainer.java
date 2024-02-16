package asg.games.yokel.client.ui.actors;


import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;

import java.util.Objects;

public class GameBrokenBlockSpriteContainer implements Pool.Poolable {
    private final Image[] sprites = new Image[3];
    private GameBlock parentGameBlock = null;
    private GameBlockGrid grid = null;
    private int row, col, block = 0;
    private String name;

    //No-arg constructor required for Pools
    public GameBrokenBlockSpriteContainer() {
    }

    public GameBrokenBlockSpriteContainer(String name, Image left, Image bottom, Image right, int block, int row, int col, GameBlockGrid grid) {
        sprites[0] = left;
        sprites[1] = bottom;
        sprites[2] = right;
        setBlock(block);
        setRow(row);
        setCol(col);
        setGrid(grid);
        setName(name);
    }

    public void setGrid(GameBlockGrid grid) {
        this.grid = grid;
    }

    public GameBlockGrid getGrid() {
        return grid;
    }

    public Image getLeftSprite() {
        return sprites[0];
    }

    public Image getBottomSprite() {
        return sprites[1];
    }

    public Image getRightSprite() {
        return sprites[2];
    }

    public void setLeftSprite(Image left) {
        sprites[0] = left;
    }

    public void setBottomSprite(Image bottom) {
        sprites[1] = bottom;
    }

    public void setRightSprite(Image right) {
        sprites[2] = right;
    }

    @Override
    public void reset() {
        sprites[0] = null;
        sprites[1] = null;
        sprites[2] = null;
    }

    public void setParentGameBlock(GameBlock parentGameBlock) {
        this.parentGameBlock = parentGameBlock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public GameBlock getParentGameBlock() {
        return parentGameBlock;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameBrokenBlockSpriteContainer that = (GameBrokenBlockSpriteContainer) o;
        return getRow() == that.getRow() && getCol() == that.getCol() && getBlock() == that.getBlock() && getParentGameBlock().equals(that.getParentGameBlock()) && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParentGameBlock(), getRow(), getCol(), getBlock(), getName());
    }
}