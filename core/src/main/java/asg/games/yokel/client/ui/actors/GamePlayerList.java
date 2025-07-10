package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.client.utils.YokelUtilities;

public class GamePlayerList extends Table {
    private final static String HEADER_NAME_STR = "Name";
    private final static String HEADER_RATING_STR = "Rtng";
    private final static String HEADER_TABLE_STR = "Tbl";

    private Table header;
    private Table playerList;

    public GamePlayerList(Skin skin){
        super(skin);

        header = new Table(skin);
        playerList = new Table(skin);

        //this.setFillParent(true);
        setUpHeader();
        setUpPlayerList();
    }

    private void setUpPlayerList() {
        ScrollPane scrollPane = new ScrollPane(playerList);
        scrollPane.setScrollbarsVisible(true);
        playerList.pad(2);
        add(scrollPane);
    }

    private void setUpHeader() {
        header.pad(2);
        header.add(HEADER_NAME_STR).left();
        header.add(HEADER_RATING_STR).left();
        header.add(HEADER_TABLE_STR).left();
        add(header).row();
    }

    private void addPlayer(YipeePlayerGDX player) {
        Table table = new Table(getSkin());
        table.add(labelize(player.getName())).left();
        table.add(labelize(player.getRating())).left();
        // removeActor();
        playerList.add(table).row();
    }

    private Label labelize(Object text){
        return UIUtil.createLabel(getSkin(), text.toString(), 1f);
    }

    public void updatePlayerList(Array<YipeePlayerGDX> players) {
        playerList.clearChildren();
        for (YipeePlayerGDX player : YokelUtilities.safeIterable(players)) {
            if(player != null){
                addPlayer(player);
            }
        }
    }
}
