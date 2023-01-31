package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;

import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.utils.YokelUtilities;

public class GameNameLabel extends Table implements GameObject {
    private static final Color YAHOO_ON = new Color(1f, 0f, 0f, 1);
    private static final Color YAHOO_OFF = new Color(1f, 1f, 1f, 1);

    private GameIcon icon;
    private Label playerName;
    private final YokelPlayer _blankPlayer;

    public GameNameLabel(Skin skin){
        super(skin);
        _blankPlayer = new YokelPlayer("", 1500, 0);
        //padTop(4);
        //padBottom(4);
        resetData();
        setNameTagData();
        setDebug(true);
    }

    @Override
    public void setData(String data) {
        Gdx.app.log("GameNameLabel","data: " + data);

        //Json json = new Json();
        //YokelPlayer player = YokelUtilities.getObjectFromJsonString(YokelPlayer.class, data);
        //YokelPlayer player = json.fromJson(YokelPlayer.class, data);
        YokelPlayer player = _blankPlayer;

        if(data != null) {
            player = new YokelPlayer("data", 5, 8);
        }

        setIcon(player.getIcon());
        setNameTag(player.getName());
    }

    private void resetData(){
        setData(null);
    }

    private void setNameTagData(){
        add(icon);
        add(playerName);
    }

    private void setNameTag(String name) {
        if(playerName == null){
            playerName = new Label(_blankPlayer.getName(), getSkin());
        }
        playerName.setText(name);
    }

    private void setIcon(int iconNumber) {
        if(icon == null){
            icon = new GameIcon(GameIcon.getGameDefaultIconStyle(getSkin()), getSkin());
        }
        icon.setIconNumber(iconNumber);
    }

    @Override
    public float getPrefHeight() {
        //float iconHeight = 28;
        float iconHeight = icon == null ? 28 : icon.getPrefHeight();
        float playerHeight = playerName == null ? 28 : playerName.getPrefHeight();
        float superHeight = super.getPrefHeight();
        return YokelUtilities.maxFloat(iconHeight, playerHeight, superHeight);
    }

    @Override
    public float getPrefWidth() {
        //float iconWidth = 28;
        float iconWidth = icon == null ? 32 : icon.getPrefWidth();
        float playerWidth = 20;
        float superWidth = super.getPrefWidth();
        return YokelUtilities.maxFloat(iconWidth, playerWidth, superWidth);
    }

    public void removePlayer() {
        resetData();
    }

    public void setYahoo(boolean b) {
        if(b){
            playerName.setColor(YAHOO_ON);
        } else {
            playerName.setColor(YAHOO_OFF);
        }
    }
}