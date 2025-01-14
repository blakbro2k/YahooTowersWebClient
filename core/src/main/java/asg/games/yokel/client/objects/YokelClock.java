package asg.games.yokel.client.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Objects;

public class YokelClock extends AbstractYokelObject {
    private long start;
    private boolean isRunning;

    //Empty Constructor required for Json.Serializable
    public YokelClock() {
        resetTimer();
    }

    public void start() {
        isRunning = true;
        start = TimeUtils.millis();
    }

    public int getElapsedSeconds() {
        return (int) ((TimeUtils.millis() - start) / 1000);
    }

    public void stop() {
        resetTimer();
    }

    private void resetTimer() {
        start = -1;
        isRunning = false;
    }

    public long getStart() {
        return start;
    }

    public int getSeconds() {
        if (isRunning()) {
            return Math.round(getElapsedSeconds()) % 60;
        }
        return -1;
    }

    public int getMinutes() {
        if (isRunning()) {
            return Math.round(getElapsedSeconds()) / 60;
        }
        return -1;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void write(Json json) {
        if (json != null) {
            super.write(json);
            json.writeValue("start", start);
            json.writeValue("isRunning", isRunning);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        if (json != null) {
            super.read(json, jsonValue);
            start = json.readValue("start", Long.class, jsonValue);
            isRunning = json.readValue("isRunning", Boolean.class, jsonValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        YokelClock that = (YokelClock) o;
        return getStart() == that.getStart() && isRunning() == that.isRunning();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStart(), isRunning());
    }
}