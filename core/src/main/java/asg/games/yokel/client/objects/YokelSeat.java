package asg.games.yokel.client.objects;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Objects;

import asg.games.yokel.client.utils.YokelUtilities;

/**
 * Created by Blakbro2k on 1/28/2018.
 */


public class YokelSeat extends AbstractYokelObject implements Disposable {
    private static final String ATTR_SEAT_NUM_SEPARATOR = "-";

    private YokelPlayer seatedPlayer;
    private String tableId;
    private boolean isSeatReady = false;

    public void setSeatedPlayer(YokelPlayer seatedPlayer) {
        this.seatedPlayer = seatedPlayer;
    }

    //Empty Constructor required for Json.Serializable
    public YokelSeat() {
    }

    public YokelSeat(String tableId, int seatNumber) {
        if (seatNumber < 0 || seatNumber > 7)
            throw new IllegalArgumentException("Seat number must be between 0 - 7.");
        setTableId(tableId);
        setName(tableId + ATTR_SEAT_NUM_SEPARATOR + seatNumber);
    }

    public boolean sitDown(YokelPlayer player) {
        if (!isOccupied()) {
            seatedPlayer = player;
            return true;
        }
        return false;
    }

    public YokelPlayer standUp() {
        YokelPlayer var = seatedPlayer;
        seatedPlayer = null;
        setSeatReady(false);
        return var;
    }

    public boolean isOccupied() {
        return seatedPlayer != null;
    }

    public void setSeatReady(boolean isSeatReady) {
        this.isSeatReady = isSeatReady;
    }

    public boolean isSeatReady() {
        return isOccupied() && isSeatReady;
    }

    public YokelPlayer getSeatedPlayer() {
        return seatedPlayer;
    }

    public int getSeatNumber() {
        return Integer.parseInt(YokelUtilities.split(getName(), ATTR_SEAT_NUM_SEPARATOR)[1]);
    }

    @Override
    public void dispose() {
        if (isOccupied()) {
            standUp();
        }
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        if (json != null) {
            json.writeValue("tableId", tableId);
            json.writeValue("isSeatReady", isSeatReady);
            if (seatedPlayer != null) {
                json.writeValue("seatedPlayer", seatedPlayer, YokelPlayer.class);
            }
        }
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        super.read(json, jsonValue);
        if (json != null) {
            tableId = json.readValue("tableId", String.class, jsonValue);
            isSeatReady = json.readValue("isSeatReady", Boolean.class, jsonValue);
            seatedPlayer = json.readValue("seatedPlayer", YokelPlayer.class, jsonValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        YokelSeat yokelSeat = (YokelSeat) o;
        return isSeatReady() == yokelSeat.isSeatReady() && Objects.equals(getSeatedPlayer(), yokelSeat.getSeatedPlayer()) && Objects.equals(getTableId(), yokelSeat.getTableId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSeatedPlayer(), getTableId(), isSeatReady());
    }
}