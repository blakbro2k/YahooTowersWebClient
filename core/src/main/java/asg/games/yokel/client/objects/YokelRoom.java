package asg.games.yokel.client.objects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import java.util.Objects;

import asg.games.yokel.client.utils.YokelUtilities;

/**
 * Created by Blakbro2k on 1/28/2018.
 */

public class YokelRoom extends AbstractYokelObject implements Copyable<YokelRoom>, Disposable {
    public static final String SOCIAL_LOUNGE = "Social";
    public static final String BEGINNER_LOUNGE = "Beginner";
    public static final String INTERMEDIATE_LOUNGE = "Intermediate";
    public static final String ADVANCED_LOUNGE = "Advanced";

    private Array<YokelPlayer> players = GdxArrays.newArray();
    private ObjectMap<Integer, YokelTable> tables = GdxMaps.newObjectMap();

    private String loungeName = "_NoTableName";

    //Empty Constructor required for Json.Serializable
    public YokelRoom() {
    }

    public YokelRoom(String name) {
        setName(name);
    }

    public YokelRoom(String name, String loungeName) {
        setName(name);
        setLoungeName(loungeName);
    }

    public Array<YokelTable> getAllTables() {
        return YokelUtilities.getMapValues(tables).toArray();
    }

    public Array<Integer> getAllTableIndexes() {
        return YokelUtilities.getMapKeys(tables).toArray();
    }

    public void setAllTables(ObjectMap<Integer, YokelTable> tables) {
        this.tables = tables;
    }

    public Array<YokelPlayer> getAllPlayers() {
        return players;
    }

    public void setAllPlayers(Array<YokelPlayer> players) {
        this.players = players;
    }

    public void joinRoom(YokelPlayer player) {
        if (player != null && !players.contains(player, false)) {
            players.add(player);
        }
    }

    public void leaveRoom(YokelPlayer player) {
        players.removeValue(player, false);
    }

    public YokelTable addTable() {
        return addTable(null);
    }

    public YokelTable addTable(OrderedMap<String, Object> arguments) {
        YokelTable table;
        int tableNumber = YokelUtilities.getNextTableNumber(this);

        if (arguments != null) {
            table = new YokelTable(getId(), tableNumber, arguments);
        } else {
            table = new YokelTable(getId(), tableNumber);
        }
        tables.put(tableNumber, table);
        return table;
    }

    public YokelTable getTable(int tableNumber) {
        return tables.get(tableNumber);
    }

    public void removeTable(int tableNumber) {
        tables.remove(tableNumber);
    }

    public String getLoungeName() {
        return loungeName;
    }

    public void setLoungeName(String loungeName) {
        this.loungeName = loungeName;
    }

    @Override
    public void dispose() {
        YokelUtilities.clearArrays(players);
        if (tables != null) {
            tables.clear();
        }
    }

    @Override
    public YokelRoom copy() {
        YokelRoom copy = new YokelRoom();
        copy.setName(this.name);
        copy.setLoungeName(this.loungeName);
        return copy;
    }

    @Override
    public YokelRoom deepCopy() {
        YokelRoom copy = copy();
        copyParent(copy);
        copy.setAllPlayers(players);
        copy.setAllTables(tables);
        return copy;
    }


    @Override
    public void write(Json json) {
        super.write(json);
        if (json != null) {
            json.writeValue("loungeName", loungeName);
            json.writeValue("players", players);
            json.writeValue("tables", tables);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        super.read(json, jsonValue);
        if (json != null) {
            loungeName = json.readValue("loungeName", String.class, jsonValue);
            players = json.readValue("players", Array.class, jsonValue);
            //need to add manually to preserve order
            ObjectMap<Object, Object> tablesJson = json.readValue("tables", ObjectMap.class, jsonValue);
            if (tablesJson != null) {
                for (Object keyStringObj : YokelUtilities.getMapKeys(tablesJson)) {
                    if (keyStringObj instanceof String) {
                        int key = Integer.parseInt(keyStringObj.toString());
                        Object o = tablesJson.get(keyStringObj);
                        if (o instanceof YokelTable) {
                            tables.put(key, (YokelTable) o);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        YokelRoom yokelRoom = (YokelRoom) o;
        return players.equals(yokelRoom.players) && tables.equals(yokelRoom.tables) && getLoungeName().equals(yokelRoom.getLoungeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), players, tables, getLoungeName());
    }
}