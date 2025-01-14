package asg.games.yokel.client.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class YokelBrokenBlock extends AbstractYokelObject {
   private int block, row, col;

   //Empty Constructor required for Json.Serializable
   public YokelBrokenBlock() {
   }

   public YokelBrokenBlock(int block, int row, int col) {
      setBlock(block);
      setRow(row);
      setCol(col);
   }

   public int getBlock() {
      return block;
   }

   public void setBlock(int block) {
      this.block = block;
   }

   public int getRow() {
      return row;
   }

   public void setRow(int row) {
      this.row = row;
   }

   public int getCol() {
      return col;
   }

   public void setCol(int col) {
      this.col = col;
   }

   @Override
   public void write(Json json) {
      if (json != null) {
         super.write(json);
         json.writeValue("block", block);
         json.writeValue("row", row);
         json.writeValue("col", col);
      }
   }

   @Override
   public void read(Json json, JsonValue jsonValue) {
      if (json != null) {
         super.read(json, jsonValue);
         block = json.readValue("block", Integer.class, jsonValue);
         row = json.readValue("row", Integer.class, jsonValue);
         col = json.readValue("col", Integer.class, jsonValue);
      }
   }

   @Override
   public String toString() {
      return super.toString() + "{Block: [" + block + "]" + YokelBlock.printReaderFriendlyBlock(block) + "@, row: " + row + ", col: " + col + "}";
   }
}
