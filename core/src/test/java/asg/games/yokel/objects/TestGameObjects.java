package asg.games.yokel.objects;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import asg.games.yokel.client.objects.AbstractYokelObject;
import asg.games.yokel.client.objects.Copyable;
import asg.games.yokel.client.objects.YokelBlock;
import asg.games.yokel.client.objects.YokelBlockMove;
import asg.games.yokel.client.objects.YokelBoardPair;
import asg.games.yokel.client.objects.YokelBrokenBlock;
import asg.games.yokel.client.objects.YokelClock;
import asg.games.yokel.client.objects.YokelGameBoard;
import asg.games.yokel.client.objects.YokelKeyMap;
import asg.games.yokel.client.objects.YokelObject;
import asg.games.yokel.client.objects.YokelPiece;
import asg.games.yokel.client.objects.YokelPlayer;
import asg.games.yokel.client.objects.YokelRoom;
import asg.games.yokel.client.objects.YokelSeat;
import asg.games.yokel.client.objects.YokelTable;
import asg.games.yokel.client.utils.RandomUtil;
import asg.games.yokel.client.utils.YokelUtilities;

public class TestGameObjects {
    AtomicInteger atomicId = new AtomicInteger(0);

    @Test()
    public void testRandomArray() {
        RandomUtil.TestRandomBlockArray testNextBlocks = new RandomUtil.TestRandomBlockArray(2048, -1, 6);
        RandomUtil.RandomNumberArray nextBlocks = new RandomUtil.RandomNumberArray(2048, 1, 6);

        System.out.println("testArray=" + Arrays.toString(randomToArray(testNextBlocks)));
        System.out.println("realArray=" + Arrays.toString(randomToArray(nextBlocks)));
    }

    private int[] randomToArray(RandomUtil.RandomNumberArray blocksArray) {
        int[] testReturn = new int[2048];
        for (int i = 0; i < 2048; i++) {
            testReturn[i] = blocksArray.getRandomNumberAt(i);
        }
        return testReturn;
    }

    @Test(dataProvider = "yokel_objects")
    public void testAbstractEqualsAndHashNullIds(AbstractYokelObject y1, AbstractYokelObject y2, AbstractYokelObject x1) {
        Assert.assertEquals(y1, y2);
        Assert.assertNotEquals(y1, x1);
        Assert.assertNotEquals(y2, x1);

        //HashSet uses hashes to determine uniqueness.
        Set<YokelObject> set = new HashSet<>();
        set.add(y1);
        set.add(y2);
        set.add(x1);
        Assert.assertEquals(set.size(), 2);
    }

    @Test(dataProvider = "yokel_objects_ids")
    public void testAbstractEqualsAndHash(AbstractYokelObject y1, AbstractYokelObject y2, AbstractYokelObject x1) {
        Assert.assertNotEquals(y1, y2);
        Assert.assertNotEquals(y1, x1);
        Assert.assertNotEquals(y2, x1);
        Assert.assertNotEquals(Objects.hash(y1.getId()), x1.hashCode());
        Assert.assertNotEquals(Objects.hash(y2.getId()), x1.hashCode());
        Assert.assertNotEquals(Objects.hash(y1.getId()), y2.hashCode());
        System.out.println("y1: " + y1);
        System.out.println("y1: " + y1.getClass());
        YokelObject copy = YokelUtilities.getObjectFromJsonString(y1.getClass(), y1.getJsonString());
        Assert.assertEquals(copy, y1);
        Assert.assertEquals(copy.getId(), y1.getId());
        Assert.assertEquals(copy.hashCode(), y1.hashCode());
    }

    @Test(dataProvider = "yokel_objects_ids")
    public void testAbstractYokeObjectDeepCopy(AbstractYokelObject y1, AbstractYokelObject y2, AbstractYokelObject x1) {
        //test Deep Copy
        if (y1 instanceof Copyable<?>) {
            Object copy1 = ((Copyable<?>) y1).deepCopy();
            Assert.assertEquals(copy1, y1);
        }

        if (y2 instanceof Copyable<?>) {
            Object copy1 = ((Copyable<?>) y2).deepCopy();
            Assert.assertEquals(copy1, y2);
        }

        if (x1 instanceof Copyable<?>) {
            Object copy1 = ((Copyable<?>) x1).deepCopy();
            Assert.assertEquals(copy1, x1);
        }
    }

    @Test(dataProvider = "yokel_objects_ids")
    public void testJSONConversion(AbstractYokelObject y1, AbstractYokelObject y2, AbstractYokelObject x1) {
        //Test Json
        String jsonStringBlock1 = y1.getJsonString();
        String jsonStringYblock2 = y2.getJsonString();
        String jsonStringXblock1 = x1.getJsonString();
        System.out.println(y1);
        System.out.println(jsonStringBlock1);
        System.out.println(y2);
        System.out.println(jsonStringYblock2);
        System.out.println(x1);
        System.out.println(jsonStringXblock1);

        YokelObject readBlock1 = YokelUtilities.getObjectFromJsonString(y1.getClass(), jsonStringBlock1);
        YokelObject readYBlock1 = YokelUtilities.getObjectFromJsonString(y2.getClass(), jsonStringYblock2);
        YokelObject readYBlock2 = YokelUtilities.getObjectFromJsonString(x1.getClass(), jsonStringXblock1);
        Assert.assertEquals(jsonStringBlock1, y1.getJsonString());
        Assert.assertEquals(jsonStringYblock2, y2.getJsonString());
        Assert.assertEquals(jsonStringXblock1, x1.getJsonString());
        Assert.assertEquals(readBlock1, y1);
        Assert.assertEquals(readYBlock1, y2);
        Assert.assertEquals(readYBlock2, x1);
    }

    @Test
    public void testYokelBlock() {
        YokelBlock block1 = new YokelBlock(1, 1);
        YokelBlock yblock1 = new YokelBlock(1, 1, YokelBlock.Y_BLOCK);
        setIdAndName(block1, yblock1);

        //getBlockType() {
        Assert.assertEquals(yblock1.getBlockType(), YokelBlock.Y_BLOCK);
        //public void setBlockType(int blockType) {
        yblock1.setBlockType(YokelBlock.OFFENSIVE_BASH_BLOCK_MEGA);
        Assert.assertEquals(yblock1.getBlockType(), YokelBlock.OFFENSIVE_BASH_BLOCK_MEGA);

        //public void reset() {
        block1.dispose();
        Assert.assertEquals(block1.getBlockType(), YokelBlock.CLEAR_BLOCK);

        //public void setPowerIntensity(int intensity) {
        //public int getPowerIntensity() {
        block1.setPowerIntensity(1);
        Assert.assertEquals(block1.getPowerIntensity(), 3);
        block1.setPowerIntensity(10);
        Assert.assertEquals(block1.getPowerIntensity(), 7);

        Assert.assertTrue(block1.hasPower());
        block1.setPowerIntensity(0);
        Assert.assertFalse(block1.hasPower());

        //Test Json
        block1.setPowerIntensity(4);
        String json = YokelUtilities.getJsonString(YokelBlock.class, block1);
        YokelBlock readYokelBlock = YokelUtilities.getObjectFromJsonString(YokelBlock.class, json);
        Assert.assertEquals(json, block1.getJsonString());
        System.out.println("Expected: " + block1.getJsonString());
        System.out.println("Actual: " + readYokelBlock.getJsonString());
        Assert.assertEquals(block1, readYokelBlock);

        //Test Json complete
        //getBlockType() {
        Assert.assertEquals(readYokelBlock.getBlockType(), YokelBlock.CLEAR_BLOCK);
        Assert.assertEquals(readYokelBlock.getPowerIntensity(), 4);
        Assert.assertTrue(block1.hasPower());
    }

    @Test
    public void testYokelClock() {
        YokelClock clock = new YokelClock();
        setIdAndName(clock);

        //Test Json
        String jsonStringClock1 = clock.getJsonString();
        System.out.println("clock: " + jsonStringClock1);

        //Test methods
        YokelClock readClock1 = YokelUtilities.getObjectFromJsonString(YokelClock.class, jsonStringClock1);
        Assert.assertEquals(jsonStringClock1, clock.getJsonString());
        Assert.assertEquals(readClock1, clock);

        Assert.assertFalse(clock.isRunning());
        Assert.assertEquals(clock.getSeconds(), -1);
        Assert.assertEquals(clock.getMinutes(), -1);

        clock.start();
        long millisSnapshot = TimeUtils.millis();
        Assert.assertEquals(clock.getStart(), millisSnapshot);
        Assert.assertTrue(clock.isRunning());
        Assert.assertEquals(clock.getSeconds(), 0);
        Assert.assertEquals(clock.getMinutes(), 0);

        //Test Json
        String json = YokelUtilities.getJsonString(YokelClock.class, clock);
        YokelClock readYokelClock = YokelUtilities.getObjectFromJsonString(YokelClock.class, json);
        Assert.assertEquals(json, clock.getJsonString());
        System.out.println("Expected: " + clock.getJsonString());
        System.out.println("Actual: " + readYokelClock.getJsonString());
        Assert.assertEquals(clock, readYokelClock);
    }

    @Test
    public void testYokelBoardPair() {
        YokelGameBoard board1 = new YokelGameBoard();
        YokelGameBoard board2 = new YokelGameBoard(14);
        YokelBoardPair pair = new YokelBoardPair(board1, board2);
        setIdAndName(board1, board2, pair);

        //Test methods
        String jsonStringBoard1 = board1.getJsonString();
        String jsonStringBoard2 = board2.getJsonString();
        String jsonStringPair = pair.getJsonString();
        System.out.println("board1: " + jsonStringBoard1);
        System.out.println("board2: " + jsonStringBoard2);
        System.out.println("pair: " + jsonStringPair);

        //Json Test
        YokelGameBoard readStringBoard1 = YokelUtilities.getObjectFromJsonString(YokelGameBoard.class, jsonStringBoard1);
        YokelGameBoard readStringBoard2 = YokelUtilities.getObjectFromJsonString(YokelGameBoard.class, jsonStringBoard2);
        YokelBoardPair readStringPair = YokelUtilities.getObjectFromJsonString(YokelBoardPair.class, jsonStringPair);
        Assert.assertEquals(jsonStringBoard1, board1.getJsonString());
        Assert.assertEquals(jsonStringBoard2, board2.getJsonString());
        Assert.assertEquals(jsonStringPair, pair.getJsonString());
        Assert.assertEquals(readStringBoard1, board1);
        Assert.assertEquals(readStringBoard2, board2);
        Assert.assertEquals(readStringPair, pair);

        Assert.assertEquals(pair.getLeftBoard(), board1);
        Assert.assertEquals(pair.getRightBoard(), board2);

        Assert.assertEquals(readStringPair.getLeftBoard(), board1);
        Assert.assertEquals(readStringPair.getRightBoard(), board2);
    }

    @Test
    public void testYokelBlockMove() {
        //TODO: UPDATE TEST
        YokelBlockMove blockMove = new YokelBlockMove(5, 5, 1, 2, 4);
        setIdAndName(blockMove);
        System.out.println(" blockMove: " + blockMove);

        String json = YokelUtilities.getJsonString(YokelBlockMove.class, blockMove);
        YokelBlockMove readBlockMove = YokelUtilities.getObjectFromJsonString(YokelBlockMove.class, json);
        Assert.assertEquals(json, blockMove.getJsonString());
        Assert.assertEquals(blockMove, readBlockMove);

        Assert.assertEquals(blockMove.col, 1);
        Assert.assertEquals(blockMove.y, 2);
        Assert.assertEquals(blockMove.targetRow, 4);
        Assert.assertEquals(json, blockMove.getJsonString());
        Assert.assertEquals(blockMove, YokelUtilities.getObjectFromJsonString(YokelBlockMove.class, json));
    }

    @Test
    public void testYokelBrokenBlock() {
        int row = 0;
        int col = 1;
        int block = 3;
        YokelBrokenBlock brokenBlock = new YokelBrokenBlock(block, row, col);
        Assert.assertEquals(brokenBlock.getRow(), row);
        Assert.assertEquals(brokenBlock.getBlock(), block);
        Assert.assertEquals(brokenBlock.getCol(), col);

        String json = YokelUtilities.getJsonString(YokelBrokenBlock.class, brokenBlock);

        YokelBrokenBlock readBrokenBlock = YokelUtilities.getObjectFromJsonString(YokelBrokenBlock.class, json);
        Assert.assertEquals(json, readBrokenBlock.getJsonString());
        Assert.assertEquals(brokenBlock, readBrokenBlock);

        Assert.assertEquals(readBrokenBlock.getRow(), row);
        Assert.assertEquals(readBrokenBlock.getBlock(), block);
        Assert.assertEquals(readBrokenBlock.getCol(), col);
    }

    @Test
    public void testYokelPiece() {
        int[] expected = new int[3];
        int expectedIndex = 1;
        expected[0] = 4;
        expected[2] = 2;

        YokelPiece yokelPiece = new YokelPiece(1, 2, 0, 4);
        setIdAndName(yokelPiece);
        System.out.println("yokelPiece: " + yokelPiece);

        yokelPiece.setPosition(2, 3);
        Assert.assertEquals(yokelPiece.row, 2);
        Assert.assertEquals(yokelPiece.column, 3);

        Assert.assertThrows(RuntimeException.class, () -> yokelPiece.setPosition(-30, 4));
        Assert.assertThrows(RuntimeException.class, () -> yokelPiece.setPosition(6, -4));

        Assert.assertEquals(yokelPiece.getValueAt(1), expected[1]);
        Assert.assertEquals(yokelPiece.getValueAt(2), expected[2]);
        Assert.assertEquals(yokelPiece.getValueAt(0), expected[0]);

        Assert.assertEquals(yokelPiece.getIndex(), expectedIndex);
        Assert.assertEquals(yokelPiece.getBlock1(), expected[2]);
        Assert.assertEquals(yokelPiece.getBlock2(), expected[1]);
        Assert.assertEquals(yokelPiece.getBlock3(), expected[0]);
        Assert.assertEquals(Arrays.toString(yokelPiece.getCells()), Arrays.toString(expected));

        //Cycle Down
        yokelPiece.cycleDown();
        Assert.assertEquals(yokelPiece.getBlock1(), expected[0]);
        Assert.assertEquals(yokelPiece.getBlock2(), expected[2]);
        Assert.assertEquals(yokelPiece.getBlock3(), expected[1]);

        //Cycle Down
        yokelPiece.cycleUp();
        Assert.assertEquals(yokelPiece.getBlock1(), expected[2]);
        Assert.assertEquals(yokelPiece.getBlock2(), expected[1]);
        Assert.assertEquals(yokelPiece.getBlock3(), expected[0]);

        //Test Json
        String json = YokelUtilities.getJsonString(YokelPiece.class, yokelPiece);
        YokelPiece readBlockMove = YokelUtilities.getObjectFromJsonString(YokelPiece.class, json);
        Assert.assertEquals(json, yokelPiece.getJsonString());
        System.out.println("Expected: " + yokelPiece.getJsonString());
        System.out.println("Actual: " + readBlockMove.getJsonString());
        Assert.assertEquals(yokelPiece, readBlockMove);
    }

    @Test
    public void testYokelPlayer() {
        YokelPlayer yokelPlayer = new YokelPlayer("TestUser1", 500, 4);
        YokelPlayer yokelPlayer2 = new YokelPlayer("TestUser2", 2500);
        YokelPlayer yokelPlayer3 = new YokelPlayer("TestUser3");
        yokelPlayer3.setIcon(12);
        yokelPlayer3.increaseRating(10);
        yokelPlayer3.decreaseRating(5);

        Assert.assertEquals(yokelPlayer.getName(), "TestUser1");
        Assert.assertEquals(yokelPlayer2.getName(), "TestUser2");
        Assert.assertEquals(yokelPlayer3.getName(), "TestUser3");

        setIdAndName(yokelPlayer, yokelPlayer2, yokelPlayer3);
        System.out.println("yokelPlayer: " + yokelPlayer);
        System.out.println("yokelPlayer2: " + yokelPlayer2);
        System.out.println("yokelPlayer3: " + yokelPlayer3);

        Assert.assertEquals(yokelPlayer.getIcon(), 4);
        Assert.assertEquals(yokelPlayer.getRating(), 500);

        Assert.assertEquals(yokelPlayer2.getIcon(), 1);
        Assert.assertEquals(yokelPlayer2.getRating(), 2500);

        Assert.assertEquals(yokelPlayer3.getIcon(), 12);
        Assert.assertEquals(yokelPlayer3.getRating(), 1505);

        //Test Json
        String json = YokelUtilities.getJsonString(YokelPlayer.class, yokelPlayer3);
        YokelPlayer readYokelPlayer3 = YokelUtilities.getObjectFromJsonString(YokelPlayer.class, json);
        Assert.assertEquals(json, yokelPlayer3.getJsonString());
        Assert.assertEquals(yokelPlayer3, readYokelPlayer3);

        YokelPlayer copy = new YokelPlayer(YokelPlayer.class, json);
        System.out.println("Expected: " + readYokelPlayer3.getJsonString());
        System.out.println("Actual: " + copy.getJsonString());
        Assert.assertEquals(copy, readYokelPlayer3);
    }

    @Test
    public void testYokelSeat() {
        Assert.assertThrows(RuntimeException.class, () -> new YokelSeat("12", 8));
        Assert.assertThrows(RuntimeException.class, () -> new YokelSeat("12", -1));
        YokelPlayer yokelPlayer = new YokelPlayer("TestUser1", 500, 4);
        YokelPlayer yokelPlayer2 = new YokelPlayer("TestUser2", 2500);
        YokelPlayer yokelPlayer3 = new YokelPlayer("TestUser3");
        setIdAndName(yokelPlayer, yokelPlayer2, yokelPlayer3);

        YokelSeat yokelSeat = new YokelSeat("sim:table:1#1", 0);
        Assert.assertEquals(yokelSeat.getSeatNumber(), 0);
        Assert.assertEquals(yokelSeat.getTableId(), "sim:table:1#1");

        Assert.assertNull(yokelSeat.getSeatedPlayer());
        Assert.assertFalse(yokelSeat.isOccupied());

        Assert.assertTrue(yokelSeat.sitDown(yokelPlayer));
        Assert.assertFalse(yokelSeat.sitDown(yokelPlayer));
        Assert.assertEquals(yokelSeat.getSeatedPlayer(), yokelPlayer);
        Assert.assertTrue(yokelSeat.isOccupied());
        YokelPlayer standUp = yokelSeat.standUp();
        Assert.assertFalse(yokelSeat.isOccupied());
        Assert.assertEquals(standUp, yokelPlayer);

        yokelSeat.sitDown(yokelPlayer2);
        Assert.assertEquals(yokelSeat.getSeatedPlayer(), yokelPlayer2);
        Assert.assertTrue(yokelSeat.isOccupied());
        yokelSeat.dispose();
        Assert.assertFalse(yokelSeat.isOccupied());

        //Test Json
        String json = YokelUtilities.getJsonString(YokelSeat.class, yokelSeat);
        YokelSeat readYokelSeat = YokelUtilities.getObjectFromJsonString(YokelSeat.class, json);
        Assert.assertEquals(json, yokelSeat.getJsonString());
        System.out.println("Expected: " + readYokelSeat.getJsonString());
        System.out.println("Actual: " + yokelSeat.getJsonString());
        Assert.assertEquals(yokelSeat, readYokelSeat);
    }

    @Test
    public void testYokelRoom() {
        YokelRoom yokelRoom1 = new YokelRoom("Eiffel Tower", YokelRoom.ADVANCED_LOUNGE);
        YokelPlayer yokelPlayer = new YokelPlayer("TestUser1", 500, 4);
        YokelPlayer yokelPlayer2 = new YokelPlayer("TestUser2", 2500);
        YokelPlayer yokelPlayer3 = new YokelPlayer("TestUser3");
        setIdAndName(yokelRoom1, yokelPlayer, yokelPlayer2, yokelPlayer3);
        yokelRoom1.setName("Eiffel Tower");

        Assert.assertEquals(yokelRoom1.getName(), "Eiffel Tower");
        Assert.assertEquals(yokelRoom1.getLoungeName(), YokelRoom.ADVANCED_LOUNGE);

        System.out.println("yokelRoom1: " + yokelRoom1);
        Array<YokelPlayer> expectedPlayers = GdxArrays.newArray();
        ObjectMap<Integer, YokelTable> expectedTables = GdxMaps.newObjectMap();

        Assert.assertEquals(yokelRoom1.getAllTableIndexes(), YokelUtilities.getMapKeys(expectedTables));
        Assert.assertEquals(yokelRoom1.getAllTables(), YokelUtilities.getMapValues(expectedTables));
        Assert.assertEquals(yokelRoom1.getAllPlayers(), expectedPlayers);

        //Test players watch list
        yokelRoom1.joinRoom(yokelPlayer);
        yokelRoom1.joinRoom(yokelPlayer);
        yokelRoom1.joinRoom(yokelPlayer2);
        yokelRoom1.joinRoom(yokelPlayer3);
        yokelRoom1.leaveRoom(yokelPlayer3);
        yokelRoom1.leaveRoom(yokelPlayer3);
        expectedPlayers.add(yokelPlayer);
        expectedPlayers.add(yokelPlayer2);
        Assert.assertEquals(yokelRoom1.getAllPlayers(), expectedPlayers);

        YokelRoom room = new YokelRoom("NewRoom");
        room.dispose();

        //Test table list
        yokelRoom1.addTable();

        OrderedMap<String, Object> arguments = GdxMaps.newOrderedMap();
        arguments.put(YokelTable.ARG_TYPE, YokelTable.ENUM_VALUE_PRIVATE);
        arguments.put(YokelTable.ARG_RATED, true);
        yokelRoom1.addTable(arguments);

        arguments.put(YokelTable.ARG_TYPE, YokelTable.ENUM_VALUE_PROTECTED);
        arguments.put(YokelTable.ARG_RATED, false);
        yokelRoom1.addTable(arguments);

        yokelRoom1.addTable();
        Assert.assertEquals(yokelRoom1.getAllTables().size, 4);
        yokelRoom1.removeTable(2);
        Assert.assertEquals(yokelRoom1.getAllTables().size, 3);

        Array<YokelTable> tables = yokelRoom1.getAllTables();
        for (YokelTable table : tables) {
            table.setId(YokelUtilities.IDGenerator.getID());
        }

        Assert.assertNotNull(yokelRoom1.getTable(4));
        Assert.assertNull(yokelRoom1.getTable(2));
        yokelRoom1.addTable();
        Assert.assertNotNull(yokelRoom1.getTable(2));

        //Test Json
        String json = YokelUtilities.getJsonString(YokelRoom.class, yokelRoom1);
        YokelRoom readYokelRoom = YokelUtilities.getObjectFromJsonString(YokelRoom.class, json);
        Assert.assertEquals(json, yokelRoom1.getJsonString());
        System.out.println("Expected: " + yokelRoom1.getJsonString());
        System.out.println("Actual  : " + readYokelRoom.getJsonString());
        Assert.assertEquals(readYokelRoom, yokelRoom1);
    }

    @Test
    public void testYokelKeyMap() {
        YokelKeyMap blockKeyMap = new YokelKeyMap();
        System.out.println("blockKeyMap: " + blockKeyMap);

        Assert.assertEquals(blockKeyMap.getRightKey(), Input.Keys.RIGHT);
        blockKeyMap.setRightKey(Input.Keys.LEFT);
        Assert.assertEquals(blockKeyMap.getRightKey(), Input.Keys.LEFT);

        Assert.assertEquals(blockKeyMap.getLeftKey(), Input.Keys.LEFT);
        blockKeyMap.setLeftKey(Input.Keys.UP);
        Assert.assertEquals(blockKeyMap.getLeftKey(), Input.Keys.UP);

        Assert.assertEquals(blockKeyMap.getCycleDownKey(), Input.Keys.UP);
        blockKeyMap.setCycleDownKey(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getCycleDownKey(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getDownKey(), Input.Keys.DOWN);
        blockKeyMap.setDownKey(Input.Keys.LEFT);
        Assert.assertEquals(blockKeyMap.getDownKey(), Input.Keys.LEFT);

        Assert.assertEquals(blockKeyMap.getCycleUpKey(), Input.Keys.P);
        blockKeyMap.setCycleUpKey(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getCycleUpKey(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget1(), Input.Keys.NUM_1);
        blockKeyMap.setTarget1(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget1(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget2(), Input.Keys.NUM_2);
        blockKeyMap.setTarget2(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget2(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget3(), Input.Keys.NUM_3);
        blockKeyMap.setTarget3(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget3(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget4(), Input.Keys.NUM_4);
        blockKeyMap.setTarget4(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget4(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget5(), Input.Keys.NUM_5);
        blockKeyMap.setTarget5(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget5(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget6(), Input.Keys.NUM_6);
        blockKeyMap.setTarget6(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget6(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget7(), Input.Keys.NUM_7);
        blockKeyMap.setTarget7(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget7(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getTarget8(), Input.Keys.NUM_8);
        blockKeyMap.setTarget8(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getTarget8(), Input.Keys.DOWN);

        Assert.assertEquals(blockKeyMap.getRandomAttackKey(), Input.Keys.SPACE);
        blockKeyMap.setRandomAttackKey(Input.Keys.DOWN);
        Assert.assertEquals(blockKeyMap.getRandomAttackKey(), Input.Keys.DOWN);
    }

    @Test
    public void testYokelTable() {
        YokelTable yokelTable = new YokelTable("sim:room1", 1);
        YokelTable yokelTable2 = new YokelTable("sim:room2", 2);

        YokelPlayer yokelPlayer = new YokelPlayer("TestUser1", 500, 4);
        YokelPlayer yokelPlayer2 = new YokelPlayer("TestUser2", 2500);

        OrderedMap<String, Object> arguments = GdxMaps.newOrderedMap();
        arguments.put(YokelTable.ARG_RATED, true);
        arguments.put(YokelTable.ARG_TYPE, YokelTable.ENUM_VALUE_PRIVATE);
        YokelTable yokelTable3 = new YokelTable("sim:room3", 1, arguments);
        setIdAndName(yokelTable, yokelTable2, yokelTable3, yokelPlayer, yokelPlayer2);
        yokelTable.setName("sim:room1");
        yokelTable.setTableName(1);
        yokelTable2.setName("sim:room2");
        yokelTable2.setTableName(2);
        yokelTable3.setName("sim:room3");
        yokelTable3.setTableName(1);

        yokelTable2.setAccessType(YokelTable.ACCESS_TYPE.PROTECTED);
        Assert.assertEquals(yokelTable.getAccessType(), YokelTable.ACCESS_TYPE.PUBLIC);
        Assert.assertEquals(yokelTable2.getAccessType().getValue(), YokelTable.ACCESS_TYPE.PROTECTED.toString());
        Assert.assertEquals(yokelTable3.getAccessType(), YokelTable.ACCESS_TYPE.PRIVATE);

        Assert.assertEquals(yokelTable.getTableNumber(), 1);
        Assert.assertEquals(yokelTable2.getTableNumber(), 2);
        Assert.assertEquals(yokelTable3.getTableNumber(), 1);

        Assert.assertFalse(yokelTable.isRated());
        Assert.assertFalse(yokelTable2.isRated());
        Assert.assertTrue(yokelTable3.isRated());

        yokelTable.addWatcher(yokelPlayer);
        yokelTable.addWatcher(yokelPlayer2);
        Assert.assertEquals(yokelTable.getWatchers().size, 2);
        yokelTable.removeWatcher(yokelPlayer2);
        Assert.assertEquals(yokelTable.getWatchers().size, 1);

        Assert.assertFalse(yokelTable.isGroupReady(-3));
        Assert.assertFalse(yokelTable.isGroupReady(23));

        for (int i = 0; i < 9; i++) {
            Assert.assertFalse(yokelTable.isGroupReady(i));
        }

        //Test Json
        String json = YokelUtilities.getJsonString(YokelTable.class, yokelTable);
        YokelTable readYokelTable = YokelUtilities.getObjectFromJsonString(YokelTable.class, json);
        Assert.assertEquals(json, yokelTable.getJsonString());
        System.out.println("Expected: " + yokelTable.getJsonString());
        System.out.println("Actual  : " + readYokelTable.getJsonString());
        Assert.assertEquals(readYokelTable, yokelTable);
    }

    @DataProvider(name = "yokel_objects")
    public Object[][] provideYokelObjects() {
        YokelBlock yokelBlock1 = new YokelBlock();
        YokelBlock yokelBlock2 = new YokelBlock();
        YokelBlock yokelBlock3 = new YokelBlock();
        yokelBlock3.setModified(TimeUtils.millis() + 1);

        YokelClock yokelClock1 = new YokelClock();
        YokelClock yokelClock2 = new YokelClock();
        YokelClock yokelClock3 = new YokelClock();
        yokelClock3.setModified(TimeUtils.millis() + 1);

        YokelPlayer player1 = new YokelPlayer();
        YokelPlayer player2 = new YokelPlayer();
        YokelPlayer player3 = new YokelPlayer();
        player3.setModified(TimeUtils.millis() + 1);

        YokelPiece piece1 = new YokelPiece();
        YokelPiece piece2 = new YokelPiece();
        YokelPiece piece3 = new YokelPiece();
        piece3.setModified(TimeUtils.millis() + 1);

        YokelBlockMove yokelBlockMove1 = new YokelBlockMove();
        YokelBlockMove yokelBlockMove2 = new YokelBlockMove();
        YokelBlockMove yokelBlockMove3 = new YokelBlockMove();
        yokelBlockMove3.setModified(TimeUtils.millis() + 1);

        YokelBoardPair yokelBoardPair1 = new YokelBoardPair();
        YokelBoardPair yokelBoardPair2 = new YokelBoardPair();
        YokelBoardPair yokelBoardPair3 = new YokelBoardPair();
        yokelBoardPair3.setModified(TimeUtils.millis() + 1);

        YokelRoom yokelRoom1 = new YokelRoom();
        YokelRoom yokelRoom2 = new YokelRoom();
        YokelRoom yokelRoom3 = new YokelRoom();
        yokelRoom3.setModified(TimeUtils.millis() + 1);

        YokelSeat yokelSeat1 = new YokelSeat();
        YokelSeat yokelSeat2 = new YokelSeat();
        YokelSeat yokelSeat3 = new YokelSeat();
        yokelSeat3.setModified(TimeUtils.millis() + 1);

        YokelTable yokelTablet1 = new YokelTable();
        YokelTable yokelTablet2 = new YokelTable();
        YokelTable yokelTablet3 = new YokelTable();
        yokelTablet3.setModified(TimeUtils.millis() + 1);

        YokelBrokenBlock YokelBrokenBlock1 = new YokelBrokenBlock(1, 2, 3);
        YokelBrokenBlock YokelBrokenBlock2 = new YokelBrokenBlock(1, 2, 3);
        YokelBrokenBlock YokelBrokenBlock3 = new YokelBrokenBlock(1, 2, 3);
        YokelBrokenBlock3.setModified(TimeUtils.millis() + 1);

        return new Object[][]{
                {yokelBlock1, yokelBlock2, yokelBlock3},
                {yokelBlockMove1, yokelBlockMove2, yokelBlockMove3},
                {yokelBoardPair1, yokelBoardPair2, yokelBoardPair3},
                {YokelBrokenBlock1, YokelBrokenBlock2, YokelBrokenBlock3},
                {yokelClock1, yokelClock2, yokelClock3},
                {piece1, piece2, piece3},
                {player1, player2, player3},
                {yokelRoom1, yokelRoom2, yokelRoom3},
                {yokelSeat1, yokelSeat2, yokelSeat3},
                {yokelTablet1, yokelTablet2, yokelTablet3}
        };
    }

    @DataProvider(name = "yokel_objects_ids")
    public Object[][] provideYokelObjectsWithIds() {
        Object[][] objectsWithIds = provideYokelObjects();
        // let's loop through array to populate id and name
        for (Object[] objectsWithId : objectsWithIds) {
            for (Object object : objectsWithId) {
                if (object instanceof YokelObject) {
                    setIdAndName((YokelObject) object);
                }
            }
        }
        return objectsWithIds;
    }

    private void setIdAndName(YokelObject... yokelObjects) {
        for (YokelObject yokelObject : YokelUtilities.safeIterable(yokelObjects)) {
            if (yokelObject != null) {
                int id = atomicId.getAndIncrement();
                yokelObject.setId(YokelUtilities.IDGenerator.getID());
                yokelObject.setName(id + "-" + yokelObject.getClass().getSimpleName());
            }
        }
    }
}