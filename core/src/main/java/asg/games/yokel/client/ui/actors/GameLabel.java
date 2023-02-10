package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameLabel extends Label {
    public enum Direction {LEFT, RIGHT, UP, DOWN}
    public final float DEFAULT_VELOCITY = 350;
    private Direction direction;
    private float velocity = 1000;

    static private final Color tempColor = new Color(1, 1, 1, 1);

    //private final BitmapFontCache cache;
    //private final LabelStyle style;
    private boolean hasFinished = true;
    private boolean hasStarted = false;
    private Vector2 finalCords = new Vector2();
    private boolean animatedByLetter;
    private int start;
    private int end = getText().length;

    public GameLabel (CharSequence text, Skin skin) {
        this(text, skin.get(LabelStyle.class), Direction.UP, true);
    }

    public GameLabel (CharSequence text, Skin skin, Direction direction, boolean animatedByLetter) {
        this(text, skin.get(LabelStyle.class), direction, true);
    }

    public GameLabel (CharSequence text, LabelStyle style, Direction direction, boolean animatedByLetter) {
        super(text, style);
        this.direction = direction;
        this.animatedByLetter = animatedByLetter;
        this.velocity = DEFAULT_VELOCITY;
    }

    public void layout(){
        super.layout();
        System.err.println("GameLabel: layout Called.");
        System.err.println("(" + getX() + "," + getY() + ")");
        computeCoords();
    }

    private void computeCoords(){
        System.err.println("computeCoords");
        finalCords.x = getX();
        finalCords.y = getY();
        System.err.println("(" + finalCords.x + "," + finalCords.y + ")");

        if(direction == Direction.UP){
            setPosition(getX(), 0, 1000);
        }
    }

    public void act(float delta){
        //if(!hasFinished && hasStarted){
            if(direction == Direction.UP){
                if(getY() < finalCords.y){
                    setY(getY() + (velocity * delta));
                } else {
                    hasFinished = true;
                }
            }
        //}
    }

    /*
    public GameLabel(CharSequence text, Skin skin, Direction direction, boolean animatedByLetter) {
        super(text, skin);

        cache = getBitmapFontCache();
        style = getStyle();
        finalCords = new Vector2(0, 0);
        this.direction = direction;
        //setAnimation(animatedByLetter);
        hasFinished = false;
        hasStarted = false;
        start = 0;
        setVisible(false);
        System.err.println("initialize - gamelabel" + getText());
    }

    private void setAnimation(boolean animatedByLetter) {
         this.animatedByLetter = animatedByLetter;
    }

    public GameLabel(CharSequence text, Skin skin) {
        this(text, skin, Direction.UP,false);
    }

    public GameLabel(CharSequence text, Skin skin, boolean isPerLetter) {
        this(text, skin, Direction.UP, isPerLetter);
    }

    public void startAnimation(){
        if(!hasStarted){
            //computeCoords();
            hasStarted = true;
            setVisible(true);
        }
    }
/*


    @Override
    public void setColor(Color color) {
        super.setColor(color);
        System.err.println("Set Color!!");
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
        System.err.println("Set floating Color!!");

    }

    public boolean isEnabled(){
        return hasStarted;
    }

    public void setVelocity(float v){
        velocity = v;
    }

    public float getVelocity(){
        return velocity;
    }







    public void draw (Batch batch, float parentAlpha) {
        validate();
        Color color = tempColor.set(getColor());
        color.a *= parentAlpha;
        if (style.background != null) {
            batch.setColor(color.r, color.g, color.b, color.a);
            style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
        if (style.fontColor != null) color.mul(style.fontColor);
        cache.tint(color);
        cache.setPosition(getX(), getY());
        cache.draw(batch, start, end);
    }*/
}
