package asg.games.yokel.client.ui.actors;


import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool;

public class GameBrokenSprite extends Table implements Pool.Poolable {
    private Image sprite;

    //No-arg constructor required for Pools
    public GameBrokenSprite() {
        init();
    }

    public GameBrokenSprite(Image image) {
        this.sprite = image;
        init();
    }

    private void init() {
        add(sprite);
    }

    @Override
    public void reset() {
        sprite.clear();
    }

    public void setImage(Image image) {
        sprite = image;
    }

    public Image getImage() {
        return sprite;
    }
}
