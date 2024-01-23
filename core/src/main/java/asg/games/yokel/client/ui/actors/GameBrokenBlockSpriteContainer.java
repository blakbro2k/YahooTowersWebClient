package asg.games.yokel.client.ui.actors;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;

public class GameBrokenBlockSpriteContainer implements Pool.Poolable {
    Image[] sprites = new Image[3];
    Actor parent = null;
    Skin skin = null;
    Rectangle bounds = new Rectangle();
    private String name;

    //No-arg constructor required for Pools
    public GameBrokenBlockSpriteContainer() {
    }

    public GameBrokenBlockSpriteContainer(Skin skin, Image left, Image bottom, Image right) {
        this.skin = skin;
        sprites[0] = left;
        sprites[1] = bottom;
        sprites[2] = right;
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

    public void setParent(Actor parent) {
        this.parent = parent;
    }

    public void setBounds(float x, float y, float width, float height) {
        bounds.x = x;
        bounds.y = y;
        bounds.width = width;
        bounds.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Actor getParent() {
        return parent;
    }
}