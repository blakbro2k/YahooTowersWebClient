package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import asg.games.yokel.client.utils.UIUtil;

public class GameIcon extends ImageButton implements Disposable {
    private static final String ICON_ATTR_NAME = "player_icon";
    private static final int NO_ICON = 0;
    private static final int START_ICON = 1;
    private static final int MAX_ICON = 18;
    private int currentIconNumber = 0;
    private final ObjectMap<String, ImageButtonStyle> styles = GdxMaps.newObjectMap();

    public GameIcon(ImageButtonStyle imageButtonStyle, Skin skin){
        super(imageButtonStyle);
        setSkin(skin);
        addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                cycleIcon();
            }
        });
    }

    public void dispose(){
        if(styles != null){
            styles.clear();
        }
    }

    public static ImageButtonStyle getGameDefaultIconStyle(Skin skin){
        return getGameIconStyle(skin, NO_ICON);
    }

    private static ImageButtonStyle getGameIconStyle(Skin skin, int currentIconNumber){
        if(skin == null){
            throw new GdxRuntimeException("Skin cannot be null to set Default Icon Style.");
        }
        Drawable icon = skin.getDrawable(ICON_ATTR_NAME + currentIconNumber);
        return new ImageButtonStyle(icon, icon, icon, icon, icon, icon);
    }

    private void setDrawable(){
        if (currentIconNumber > MAX_ICON) currentIconNumber = MAX_ICON;
        if (currentIconNumber < NO_ICON) currentIconNumber = NO_ICON;
        String key = ICON_ATTR_NAME + currentIconNumber;
        ImageButtonStyle style = styles.get(key);
        if (style == null) {
            style = getGameIconStyle(getSkin(), currentIconNumber);
            styles.put(key, style);
        }
        UIUtil.setWidthFromDrawable(this, style.imageUp);
        UIUtil.setHeightFromDrawable(this, style.imageUp);
        setStyle(style);
    }

    private void cycleIcon(){
        if(currentIconNumber != 0 && ++currentIconNumber > MAX_ICON){
            currentIconNumber = START_ICON;
        }
        setDrawable();
    }

    public void setIconNumber(int num){
        currentIconNumber = num;
        setDrawable();
    }

    public int getCurrentIconNumber(){
        return currentIconNumber;
}

    public String getIconAttrName(){
        return ICON_ATTR_NAME + currentIconNumber;
    }
}