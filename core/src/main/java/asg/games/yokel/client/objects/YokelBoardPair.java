package asg.games.yokel.client.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Objects;

public class YokelBoardPair extends AbstractYokelObject {
    YokelGameBoard leftBoard;
    YokelGameBoard rightBoard;

    //Empty Contractor required for Json.Serializable
    public YokelBoardPair() {
    }

    public YokelBoardPair(YokelGameBoard left, YokelGameBoard right) {
        setLeftBoard(left);
        setRightBoard(right);
    }

    public void setLeftBoard(YokelGameBoard leftBoard) {
        this.leftBoard = leftBoard;
    }

    public void setRightBoard(YokelGameBoard rightBoard) {
        this.rightBoard = rightBoard;
    }

    public YokelGameBoard getLeftBoard() {
        return leftBoard;
    }

    public YokelGameBoard getRightBoard() {
        return rightBoard;
    }

    @Override
    public void write(Json json) {
        if (json != null) {
            super.write(json);
            if (leftBoard != null) {
                json.writeValue("leftBoard", leftBoard, leftBoard.getClass());
            }
            if (rightBoard != null) {
                json.writeValue("rightBoard", rightBoard, rightBoard.getClass());
            }
        }
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        if (json != null) {
            super.read(json, jsonValue);
            leftBoard = json.readValue("leftBoard", YokelGameBoard.class, jsonValue);
            rightBoard = json.readValue("rightBoard", YokelGameBoard.class, jsonValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        YokelBoardPair that = (YokelBoardPair) o;
        return Objects.equals(getLeftBoard(), that.getLeftBoard()) && Objects.equals(getRightBoard(), that.getRightBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLeftBoard(), getRightBoard());
    }
}
