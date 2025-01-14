package asg.games.yokel.client.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Objects;

import asg.games.yokel.client.utils.YokelUtilities;

public abstract class AbstractYokelObject implements YokelObject, Json.Serializable {
    protected String id;
    protected String name;
    protected long created;
    protected long modified;

    AbstractYokelObject() {
        setCreated(TimeUtils.millis());
        setModified(TimeUtils.millis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractYokelObject object = (AbstractYokelObject) o;
        return getCreated() == object.getCreated() && getModified() == object.getModified() && Objects.equals(getId(), object.getId()) && Objects.equals(getName(), object.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCreated(), getModified());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.getId() + "," + this.getName() + "]";
    }

    public String getJsonString() {
        return YokelUtilities.getJsonString(this.getClass(), this);
    }

    @Override
    public void write(Json json) {
        if (json != null) {
            json.writeValue("id", id);
            json.writeValue("name", name);
            json.writeValue("created", created);
            json.writeValue("modified", modified);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        if (json != null) {
            id = json.readValue("id", String.class, jsonValue);
            name = json.readValue("name", String.class, jsonValue);
            created = json.readValue("created", Long.class, jsonValue);
            modified = json.readValue("modified", Long.class, jsonValue);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCreated(long dateTime) {
        this.created = dateTime;
    }

    public long getCreated() {
        return created;
    }

    public void setModified(long dateTime) {
        this.modified = dateTime;
    }

    public long getModified() {
        return modified;
    }

    protected void copyParent(YokelObject o) {
        if (o != null) {
            o.setId(this.getId());
            o.setName(this.getName());
            o.setCreated(this.getCreated());
            o.setModified(this.getModified());
        }
    }
}
