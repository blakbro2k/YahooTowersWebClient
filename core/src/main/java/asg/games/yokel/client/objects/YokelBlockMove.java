package asg.games.yokel.client.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Objects;

public class YokelBlockMove extends AbstractYokelObject {
    public int block;
    public int cellId;
    public int col;
    public int y;
    public int targetRow;

    //Empty Constructor required for Json.Serializable
    public YokelBlockMove() {
    }

    public YokelBlockMove(int cellID, int block, int x, int y, int targetRow) {
        setBlock(block);
        setCellID(cellID);
        this.col = x;
        this.y = y;
        this.targetRow = targetRow;
    }

    public void setCellID(int cellId) {
        this.cellId = cellId;
    }

    public int getCellID() {
        return cellId;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getBlock() {
        return block;
    }

    @Override
    public void write(Json json) {
        if (json != null) {
            super.write(json);
            json.writeValue("block", block);
            json.writeValue("cellId", cellId);
            json.writeValue("x", col);
            json.writeValue("y", y);
            json.writeValue("targetRow", targetRow);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        if (json != null) {
            super.read(json, jsonValue);
            block = json.readValue("block", Integer.class, jsonValue);
            cellId = json.readValue("cellId", Integer.class, jsonValue);
            col = json.readValue("x", Integer.class, jsonValue);
            y = json.readValue("y", Integer.class, jsonValue);
            targetRow = json.readValue("targetRow", Integer.class, jsonValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        YokelBlockMove blockMove = (YokelBlockMove) o;
        return col == blockMove.col && y == blockMove.y && targetRow == blockMove.targetRow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), col, y, targetRow);
    }

    @Override
    public String toString() {
        return super.toString() + "{" + block + "@(x: " + col + ", y: " + y + ") to targetRow: " + targetRow + "}";
    }

    public int getRow() {
        return targetRow;
    }

    public int getCol() {
        return col;
    }
}
