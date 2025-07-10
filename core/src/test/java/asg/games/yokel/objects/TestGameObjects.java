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

import asg.games.yipee.libgdx.game.YipeeGameBoardGDX;
import asg.games.yipee.libgdx.objects.AbstractYipeeObjectGDX;
import asg.games.yipee.libgdx.objects.Copyable;
import asg.games.yipee.libgdx.objects.YipeeBlockGDX;
import asg.games.yipee.libgdx.objects.YipeeBlockMoveGDX;
import asg.games.yipee.libgdx.objects.YipeeBoardPairGDX;
import asg.games.yipee.libgdx.objects.YipeeBrokenBlockGDX;
import asg.games.yipee.libgdx.objects.YipeeClockGDX;
import asg.games.yipee.libgdx.objects.YipeeKeyMapGDX;
import asg.games.yipee.libgdx.objects.YipeeObjectGDX;
import asg.games.yipee.libgdx.objects.YipeePieceGDX;
import asg.games.yipee.libgdx.objects.YipeePlayerGDX;
import asg.games.yipee.libgdx.objects.YipeeRoomGDX;
import asg.games.yipee.libgdx.objects.YipeeSeatGDX;
import asg.games.yipee.libgdx.objects.YipeeTableGDX;
import asg.games.yipee.libgdx.tools.LibGDXUtil;
import asg.games.yipee.tools.NetUtil;
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
    public void testAbstractEqualsAndHashNullIds(AbstractYipeeObjectGDX y1, AbstractYipeeObjectGDX y2, AbstractYipeeObjectGDX x1) {
        Assert.assertEquals(y1, y2);
        Assert.assertNotEquals(y1, x1);
        Assert.assertNotEquals(y2, x1);

        //HashSet uses hashes to determine uniqueness.
        Set<YipeeObjectGDX> set = new HashSet<>();
        set.add(y1);
        set.add(y2);
        set.add(x1);
        Assert.assertEquals(set.size(), 2);
    }

    @Test(dataProvider = "yokel_objects_ids")
    public void testAbstractEqualsAndHash(AbstractYipeeObjectGDX y1, AbstractYipeeObjectGDX y2, AbstractYipeeObjectGDX x1) {
        Assert.assertNotEquals(y1, y2);
        Assert.assertNotEquals(y1, x1);
        Assert.assertNotEquals(y2, x1);
        Assert.assertNotEquals(Objects.hash(y1.getId()), x1.hashCode());
        Assert.assertNotEquals(Objects.hash(y2.getId()), x1.hashCode());
        Assert.assertNotEquals(Objects.hash(y1.getId()), y2.hashCode());
        System.out.println("y1: " + y1);
        System.out.println("y1: " + y1.getClass());
        YipeeObjectGDX copy = YokelUtilities.getObjectFromJsonString(y1.getClass(), NetUtil.toJsonClient(y1));
        Assert.assertEquals(copy, y1);
        Assert.assertEquals(copy.getId(), y1.getId());
        Assert.assertEquals(copy.hashCode(), y1.hashCode());
    }

    @Test(dataProvider = "yokel_objects_ids")
    public void testAbstractYokeObjectDeepCopy(AbstractYipeeObjectGDX y1, AbstractYipeeObjectGDX y2, AbstractYipeeObjectGDX x1) {
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
    public void testJSONConversion(AbstractYipeeObjectGDX y1, AbstractYipeeObjectGDX y2, AbstractYipeeObjectGDX x1) {
        //Test Json
        String jsonStringBlock1 = NetUtil.toJsonClient(y1);
        String jsonStringYblock2 = NetUtil.toJsonClient(y2);
        String jsonStringXblock1 = NetUtil.toJsonClient(x1);
        System.out.println(y1);
        System.out.println(jsonStringBlock1);
        System.out.println(y2);
        System.out.println(jsonStringYblock2);
        System.out.println(x1);
        System.out.println(jsonStringXblock1);

        YipeeObjectGDX readBlock1 = YokelUtilities.getObjectFromJsonString(y1.getClass(), jsonStringBlock1);
        YipeeObjectGDX readYBlock1 = YokelUtilities.getObjectFromJsonString(y2.getClass(), jsonStringYblock2);
        YipeeObjectGDX readYBlock2 = YokelUtilities.getObjectFromJsonString(x1.getClass(), jsonStringXblock1);
        Assert.assertEquals(jsonStringBlock1, NetUtil.toJsonClient(y1));
        Assert.assertEquals(jsonStringYblock2, NetUtil.toJsonClient(y2));
        Assert.assertEquals(jsonStringXblock1, NetUtil.toJsonClient(x1));
        Assert.assertEquals(readBlock1, y1);
        Assert.assertEquals(readYBlock1, y2);
        Assert.assertEquals(readYBlock2, x1);
    }

    @Test
    public void testYipeeBlockGDX() {
        YipeeBlockGDX block1 = new YipeeBlockGDX(1, 1);
        YipeeBlockGDX yblock1 = new YipeeBlockGDX(1, 1, YipeeBlockGDX.Y_BLOCK);
        setIdAndName(block1, yblock1);

        //getBlockType() {
        Assert.assertEquals(yblock1.getBlockType(), YipeeBlockGDX.Y_BLOCK);
        //public void setBlockType(int blockType) {
        yblock1.setBlockType(YipeeBlockGDX.OFFENSIVE_BASH_BLOCK_MEGA);
        Assert.assertEquals(yblock1.getBlockType(), YipeeBlockGDX.OFFENSIVE_BASH_BLOCK_MEGA);

        //public void reset() {
        block1.dispose();
        Assert.assertEquals(block1.getBlockType(), YipeeBlockGDX.CLEAR_BLOCK);

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
        String json = YokelUtilities.getJsonString(YipeeBlockGDX.class, block1);
        YipeeBlockGDX readYipeeBlockGDX = YokelUtilities.getObjectFromJsonString(YipeeBlockGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(block1));
        System.out.println("Expected: " + NetUtil.toJsonClient(block1));
        System.out.println("Actual: " + NetUtil.toJsonClient(readYipeeBlockGDX));
        Assert.assertEquals(block1, readYipeeBlockGDX);

        //Test Json complete
        //getBlockType() {
        Assert.assertEquals(readYipeeBlockGDX.getBlockType(), YipeeBlockGDX.CLEAR_BLOCK);
        Assert.assertEquals(readYipeeBlockGDX.getPowerIntensity(), 4);
        Assert.assertTrue(block1.hasPower());
    }

    @Test
    public void testYipeeClockGDX() {
        YipeeClockGDX clock = new YipeeClockGDX();
        setIdAndName(clock);

        //Test Json
        String jsonStringClock1 = NetUtil.toJsonClient(clock);
        System.out.println("clock: " + jsonStringClock1);

        //Test methods
        YipeeClockGDX readClock1 = YokelUtilities.getObjectFromJsonString(YipeeClockGDX.class, jsonStringClock1);
        Assert.assertEquals(jsonStringClock1, NetUtil.toJsonClient(clock));
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
        String json = YokelUtilities.getJsonString(YipeeClockGDX.class, clock);
        YipeeClockGDX readYipeeClockGDX = YokelUtilities.getObjectFromJsonString(YipeeClockGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(clock));
        System.out.println("Expected: " + NetUtil.toJsonClient(clock));
        System.out.println("Actual: " + NetUtil.toJsonClient(readYipeeClockGDX));
        Assert.assertEquals(clock, readYipeeClockGDX);
    }

    @Test
    public void testYipeeBoardPairGDX() {
        YipeeGameBoardGDX board1 = new YipeeGameBoardGDX();
        YipeeGameBoardGDX board2 = new YipeeGameBoardGDX(14);
        YipeeBoardPairGDX pair = new YipeeBoardPairGDX(board1.exportGameState(), board2.exportGameState());
        setIdAndName(board1.exportGameState(), board2.exportGameState(), pair);

        //Test methods
        String jsonStringBoard1 = NetUtil.toJsonClient(board1);
        String jsonStringBoard2 = NetUtil.toJsonClient(board2);
        String jsonStringPair = NetUtil.toJsonClient(pair);
        System.out.println("board1: " + jsonStringBoard1);
        System.out.println("board2: " + jsonStringBoard2);
        System.out.println("pair: " + jsonStringPair);

        //Json Test
        YipeeGameBoardGDX readStringBoard1 = YokelUtilities.getObjectFromJsonString(YipeeGameBoardGDX.class, jsonStringBoard1);
        YipeeGameBoardGDX readStringBoard2 = YokelUtilities.getObjectFromJsonString(YipeeGameBoardGDX.class, jsonStringBoard2);
        YipeeBoardPairGDX readStringPair = YokelUtilities.getObjectFromJsonString(YipeeBoardPairGDX.class, jsonStringPair);
        Assert.assertEquals(jsonStringBoard1, NetUtil.toJsonClient(board1));
        Assert.assertEquals(jsonStringBoard2, NetUtil.toJsonClient(board2));
        Assert.assertEquals(jsonStringPair, NetUtil.toJsonClient(pair));
        Assert.assertEquals(readStringBoard1, board1);
        Assert.assertEquals(readStringBoard2, board2);
        Assert.assertEquals(readStringPair, pair);

        Assert.assertEquals(pair.getLeftBoard(), board1.exportGameState());
        Assert.assertEquals(pair.getRightBoard(), board2.exportGameState());

        Assert.assertEquals(readStringPair.getLeftBoard(), board1.exportGameState());
        Assert.assertEquals(readStringPair.getRightBoard(), board2.exportGameState());
    }

    @Test
    public void testYipeeBlockMoveGDX() {
        //TODO: UPDATE TEST
        YipeeBlockMoveGDX blockMove = new YipeeBlockMoveGDX(5, 5, 1, 2, 4);
        setIdAndName(blockMove);
        System.out.println(" blockMove: " + blockMove);

        String json = YokelUtilities.getJsonString(YipeeBlockMoveGDX.class, blockMove);
        YipeeBlockMoveGDX readBlockMove = YokelUtilities.getObjectFromJsonString(YipeeBlockMoveGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(blockMove));
        Assert.assertEquals(blockMove, readBlockMove);

        Assert.assertEquals(blockMove.getCol(), 1);
        Assert.assertEquals(blockMove.getRow(), 2);
        Assert.assertEquals(blockMove.getTargetRow(), 4);
        Assert.assertEquals(json, NetUtil.toJsonClient(blockMove));
        Assert.assertEquals(blockMove, YokelUtilities.getObjectFromJsonString(YipeeBlockMoveGDX.class, json));
    }

    @Test
    public void testYipeeBrokenBlockGDX() {
        int row = 0;
        int col = 1;
        int block = 3;
        YipeeBrokenBlockGDX brokenBlock = new YipeeBrokenBlockGDX(block, row, col);
        Assert.assertEquals(brokenBlock.getRow(), row);
        Assert.assertEquals(brokenBlock.getBlock(), block);
        Assert.assertEquals(brokenBlock.getCol(), col);

        String json = YokelUtilities.getJsonString(YipeeBrokenBlockGDX.class, brokenBlock);

        YipeeBrokenBlockGDX readBrokenBlock = YokelUtilities.getObjectFromJsonString(YipeeBrokenBlockGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(readBrokenBlock));
        Assert.assertEquals(brokenBlock, readBrokenBlock);

        Assert.assertEquals(readBrokenBlock.getRow(), row);
        Assert.assertEquals(readBrokenBlock.getBlock(), block);
        Assert.assertEquals(readBrokenBlock.getCol(), col);
    }

    @Test
    public void testYipeePieceGDX() {
        int[] expected = new int[3];
        int expectedIndex = 1;
        expected[0] = 4;
        expected[2] = 2;

        YipeePieceGDX yokelPiece = new YipeePieceGDX(1, 2, 0, 4);
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
        String json = YokelUtilities.getJsonString(YipeePieceGDX.class, yokelPiece);
        YipeePieceGDX readBlockMove = YokelUtilities.getObjectFromJsonString(YipeePieceGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(yokelPiece));
        System.out.println("Expected: " + NetUtil.toJsonClient(yokelPiece));
        System.out.println("Actual: " + NetUtil.toJsonClient(readBlockMove));
        Assert.assertEquals(yokelPiece, readBlockMove);
    }

    @Test
    public void testYipeePlayerGDX() {
        YipeePlayerGDX yokelPlayer = new YipeePlayerGDX("TestUser1", 500, 4);
        YipeePlayerGDX yokelPlayer2 = new YipeePlayerGDX("TestUser2", 2500);
        YipeePlayerGDX yokelPlayer3 = new YipeePlayerGDX("TestUser3");
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
        String json = YokelUtilities.getJsonString(YipeePlayerGDX.class, yokelPlayer3);
        YipeePlayerGDX readYipeePlayerGDX3 = YokelUtilities.getObjectFromJsonString(YipeePlayerGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(yokelPlayer3));
        Assert.assertEquals(yokelPlayer3, readYipeePlayerGDX3);

        YipeePlayerGDX copy = new YipeePlayerGDX("playerOne");
        System.out.println("Expected: " + NetUtil.toJsonClient(readYipeePlayerGDX3));
        System.out.println("Actual: " + NetUtil.toJsonClient(copy));
        Assert.assertEquals(copy, readYipeePlayerGDX3);
    }

    @Test
    public void testYipeeSeatGDX() {
        Assert.assertThrows(RuntimeException.class, () -> new YipeeSeatGDX("12", 8));
        Assert.assertThrows(RuntimeException.class, () -> new YipeeSeatGDX("12", -1));
        YipeePlayerGDX yokelPlayer = new YipeePlayerGDX("TestUser1", 500, 4);
        YipeePlayerGDX yokelPlayer2 = new YipeePlayerGDX("TestUser2", 2500);
        YipeePlayerGDX yokelPlayer3 = new YipeePlayerGDX("TestUser3");
        setIdAndName(yokelPlayer, yokelPlayer2, yokelPlayer3);

        YipeeSeatGDX yokelSeat = new YipeeSeatGDX("sim:table:1#1", 0);
        Assert.assertEquals(yokelSeat.getSeatNumber(), 0);
        Assert.assertEquals(yokelSeat.getParentTableId(), "sim:table:1#1");

        Assert.assertNull(yokelSeat.getSeatedPlayer());
        Assert.assertFalse(yokelSeat.isOccupied());

        Assert.assertTrue(yokelSeat.sitDown(yokelPlayer));
        Assert.assertFalse(yokelSeat.sitDown(yokelPlayer));
        Assert.assertEquals(yokelSeat.getSeatedPlayer(), yokelPlayer);
        Assert.assertTrue(yokelSeat.isOccupied());
        YipeePlayerGDX standUp = yokelSeat.standUp();
        Assert.assertFalse(yokelSeat.isOccupied());
        Assert.assertEquals(standUp, yokelPlayer);

        yokelSeat.sitDown(yokelPlayer2);
        Assert.assertEquals(yokelSeat.getSeatedPlayer(), yokelPlayer2);
        Assert.assertTrue(yokelSeat.isOccupied());
        yokelSeat.dispose();
        Assert.assertFalse(yokelSeat.isOccupied());

        //Test Json
        String json = YokelUtilities.getJsonString(YipeeSeatGDX.class, yokelSeat);
        YipeeSeatGDX readYipeeSeatGDX = YokelUtilities.getObjectFromJsonString(YipeeSeatGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(yokelSeat));
        System.out.println("Expected: " + NetUtil.toJsonClient(readYipeeSeatGDX));
        System.out.println("Actual: " + NetUtil.toJsonClient(yokelSeat));
        Assert.assertEquals(yokelSeat, readYipeeSeatGDX);
    }

    @Test
    public void testYipeeRoomGDX() {
        YipeeRoomGDX yokelRoom1 = new YipeeRoomGDX("Eiffel Tower", YipeeRoomGDX.ADVANCED_LOUNGE);
        YipeePlayerGDX yokelPlayer = new YipeePlayerGDX("TestUser1", 500, 4);
        YipeePlayerGDX yokelPlayer2 = new YipeePlayerGDX("TestUser2", 2500);
        YipeePlayerGDX yokelPlayer3 = new YipeePlayerGDX("TestUser3");
        setIdAndName(yokelRoom1, yokelPlayer, yokelPlayer2, yokelPlayer3);
        yokelRoom1.setName("Eiffel Tower");

        Assert.assertEquals(yokelRoom1.getName(), "Eiffel Tower");
        Assert.assertEquals(yokelRoom1.getLoungeName(), YipeeRoomGDX.ADVANCED_LOUNGE);

        System.out.println("yokelRoom1: " + yokelRoom1);
        Array<YipeePlayerGDX> expectedPlayers = GdxArrays.newArray();
        ObjectMap<Integer, YipeeTableGDX> expectedTables = GdxMaps.newObjectMap();

        Assert.assertEquals(yokelRoom1.getTableIndexes(), YokelUtilities.getMapKeys(expectedTables));
        Assert.assertEquals(yokelRoom1.getTables(), YokelUtilities.getMapValues(expectedTables));
        Assert.assertEquals(yokelRoom1.getPlayers(), expectedPlayers);

        //Test players watch list
        yokelRoom1.joinRoom(yokelPlayer);
        yokelRoom1.joinRoom(yokelPlayer);
        yokelRoom1.joinRoom(yokelPlayer2);
        yokelRoom1.joinRoom(yokelPlayer3);
        yokelRoom1.leaveRoom(yokelPlayer3);
        yokelRoom1.leaveRoom(yokelPlayer3);
        expectedPlayers.add(yokelPlayer);
        expectedPlayers.add(yokelPlayer2);
        Assert.assertEquals(yokelRoom1.getPlayers(), expectedPlayers);

        YipeeRoomGDX room = new YipeeRoomGDX("NewRoom");
        room.dispose();

        //Test table list
        yokelRoom1.addTable();

        OrderedMap<String, Object> arguments = GdxMaps.newOrderedMap();
        arguments.put(YipeeTableGDX.ARG_TYPE, YipeeTableGDX.ENUM_VALUE_PRIVATE);
        arguments.put(YipeeTableGDX.ARG_RATED, true);
        yokelRoom1.addTable(arguments);

        arguments.put(YipeeTableGDX.ARG_TYPE, YipeeTableGDX.ENUM_VALUE_PROTECTED);
        arguments.put(YipeeTableGDX.ARG_RATED, false);
        yokelRoom1.addTable(arguments);

        yokelRoom1.addTable();
        Assert.assertEquals(LibGDXUtil.sizeOf(yokelRoom1.getTables()), 4);
        yokelRoom1.removeTableAt(2);
        Assert.assertEquals(LibGDXUtil.sizeOf(yokelRoom1.getTables()), 3);

        Iterable<YipeeTableGDX> tables = yokelRoom1.getTables();
        for (YipeeTableGDX table : tables) {
            table.setId(YokelUtilities.IDGenerator.getID());
        }

        Assert.assertNotNull(yokelRoom1.getTableAt(4));
        Assert.assertNull(yokelRoom1.getTableAt(2));
        yokelRoom1.addTable();
        Assert.assertNotNull(yokelRoom1.getTableAt(2));

        //Test Json
        String json = YokelUtilities.getJsonString(YipeeRoomGDX.class, yokelRoom1);
        YipeeRoomGDX readYipeeRoomGDX = YokelUtilities.getObjectFromJsonString(YipeeRoomGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(yokelRoom1));
        System.out.println("Expected: " + NetUtil.toJsonClient(yokelRoom1));
        System.out.println("Actual  : " + NetUtil.toJsonClient(readYipeeRoomGDX));
        Assert.assertEquals(readYipeeRoomGDX, yokelRoom1);
    }

    @Test
    public void testYipeeKeyMapGDX() {
        YipeeKeyMapGDX blockKeyMap = new YipeeKeyMapGDX();
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
    public void testYipeeTableGDX() {
        OrderedMap<String, Object> arguments = GdxMaps.newOrderedMap();
        arguments.put(YipeeTableGDX.ARG_RATED, true);
        arguments.put(YipeeTableGDX.ARG_TYPE, YipeeTableGDX.ENUM_VALUE_PRIVATE);

        YipeeTableGDX yokelTable = new YipeeTableGDX(1, arguments);
        YipeeTableGDX yokelTable2 = new YipeeTableGDX(2, arguments);

        YipeePlayerGDX yokelPlayer = new YipeePlayerGDX("TestUser1", 500, 4);
        YipeePlayerGDX yokelPlayer2 = new YipeePlayerGDX("TestUser2", 2500);


        YipeeTableGDX yokelTable3 = new YipeeTableGDX(3, arguments);
        setIdAndName(yokelTable, yokelTable2, yokelTable3, yokelPlayer, yokelPlayer2);
        yokelTable.setName("sim:room1");
        yokelTable.setTableName(1);
        yokelTable2.setName("sim:room2");
        yokelTable2.setTableName(2);
        yokelTable3.setName("sim:room3");
        yokelTable3.setTableName(1);

        yokelTable2.setAccessType(YipeeTableGDX.ACCESS_TYPE.PROTECTED);
        Assert.assertEquals(yokelTable.getAccessType(), YipeeTableGDX.ACCESS_TYPE.PUBLIC);
        Assert.assertEquals(yokelTable2.getAccessType().getValue(), YipeeTableGDX.ACCESS_TYPE.PROTECTED.toString());
        Assert.assertEquals(yokelTable3.getAccessType(), YipeeTableGDX.ACCESS_TYPE.PRIVATE);

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
        String json = YokelUtilities.getJsonString(YipeeTableGDX.class, yokelTable);
        YipeeTableGDX readYipeeTableGDX = YokelUtilities.getObjectFromJsonString(YipeeTableGDX.class, json);
        Assert.assertEquals(json, NetUtil.toJsonClient(yokelTable));
        System.out.println("Expected: " + NetUtil.toJsonClient(yokelTable));
        System.out.println("Actual  : " + NetUtil.toJsonClient(readYipeeTableGDX));
        Assert.assertEquals(readYipeeTableGDX, yokelTable);
    }

    @DataProvider(name = "yokel_objects")
    public Object[][] provideYipeeObjectGDXs() {
        YipeeBlockGDX yokelBlock1 = new YipeeBlockGDX();
        YipeeBlockGDX yokelBlock2 = new YipeeBlockGDX();
        YipeeBlockGDX yokelBlock3 = new YipeeBlockGDX();
        yokelBlock3.setModified(TimeUtils.millis() + 1);

        YipeeClockGDX yokelClock1 = new YipeeClockGDX();
        YipeeClockGDX yokelClock2 = new YipeeClockGDX();
        YipeeClockGDX yokelClock3 = new YipeeClockGDX();
        yokelClock3.setModified(TimeUtils.millis() + 1);

        YipeePlayerGDX player1 = new YipeePlayerGDX();
        YipeePlayerGDX player2 = new YipeePlayerGDX();
        YipeePlayerGDX player3 = new YipeePlayerGDX();
        player3.setModified(TimeUtils.millis() + 1);

        YipeePieceGDX piece1 = new YipeePieceGDX();
        YipeePieceGDX piece2 = new YipeePieceGDX();
        YipeePieceGDX piece3 = new YipeePieceGDX();
        piece3.setModified(TimeUtils.millis() + 1);

        YipeeBlockMoveGDX yokelBlockMove1 = new YipeeBlockMoveGDX();
        YipeeBlockMoveGDX yokelBlockMove2 = new YipeeBlockMoveGDX();
        YipeeBlockMoveGDX yokelBlockMove3 = new YipeeBlockMoveGDX();
        yokelBlockMove3.setModified(TimeUtils.millis() + 1);

        YipeeBoardPairGDX yokelBoardPair1 = new YipeeBoardPairGDX();
        YipeeBoardPairGDX yokelBoardPair2 = new YipeeBoardPairGDX();
        YipeeBoardPairGDX yokelBoardPair3 = new YipeeBoardPairGDX();
        yokelBoardPair3.setModified(TimeUtils.millis() + 1);

        YipeeRoomGDX yokelRoom1 = new YipeeRoomGDX();
        YipeeRoomGDX yokelRoom2 = new YipeeRoomGDX();
        YipeeRoomGDX yokelRoom3 = new YipeeRoomGDX();
        yokelRoom3.setModified(TimeUtils.millis() + 1);

        YipeeSeatGDX yokelSeat1 = new YipeeSeatGDX();
        YipeeSeatGDX yokelSeat2 = new YipeeSeatGDX();
        YipeeSeatGDX yokelSeat3 = new YipeeSeatGDX();
        yokelSeat3.setModified(TimeUtils.millis() + 1);

        YipeeTableGDX yokelTablet1 = new YipeeTableGDX();
        YipeeTableGDX yokelTablet2 = new YipeeTableGDX();
        YipeeTableGDX yokelTablet3 = new YipeeTableGDX();
        yokelTablet3.setModified(TimeUtils.millis() + 1);

        YipeeBrokenBlockGDX YipeeBrokenBlockGDX1 = new YipeeBrokenBlockGDX(1, 2, 3);
        YipeeBrokenBlockGDX YipeeBrokenBlockGDX2 = new YipeeBrokenBlockGDX(1, 2, 3);
        YipeeBrokenBlockGDX YipeeBrokenBlockGDX3 = new YipeeBrokenBlockGDX(1, 2, 3);
        YipeeBrokenBlockGDX3.setModified(TimeUtils.millis() + 1);

        return new Object[][]{
                {yokelBlock1, yokelBlock2, yokelBlock3},
                {yokelBlockMove1, yokelBlockMove2, yokelBlockMove3},
                {yokelBoardPair1, yokelBoardPair2, yokelBoardPair3},
                {YipeeBrokenBlockGDX1, YipeeBrokenBlockGDX2, YipeeBrokenBlockGDX3},
                {yokelClock1, yokelClock2, yokelClock3},
                {piece1, piece2, piece3},
                {player1, player2, player3},
                {yokelRoom1, yokelRoom2, yokelRoom3},
                {yokelSeat1, yokelSeat2, yokelSeat3},
                {yokelTablet1, yokelTablet2, yokelTablet3}
        };
    }

    @DataProvider(name = "yokel_objects_ids")
    public Object[][] provideYipeeObjectGDXsWithIds() {
        Object[][] objectsWithIds = provideYipeeObjectGDXs();
        // let's loop through array to populate id and name
        for (Object[] objectsWithId : objectsWithIds) {
            for (Object object : objectsWithId) {
                if (object instanceof YipeeObjectGDX) {
                    setIdAndName((YipeeObjectGDX) object);
                }
            }
        }
        return objectsWithIds;
    }

    private void setIdAndName(YipeeObjectGDX... yokelObjects) {
        for (YipeeObjectGDX yokelObject : YokelUtilities.safeIterable(yokelObjects)) {
            if (yokelObject != null) {
                int id = atomicId.getAndIncrement();
                yokelObject.setId(YokelUtilities.IDGenerator.getID());
                yokelObject.setName(id + "-" + yokelObject.getClass().getSimpleName());
            }
        }
    }
}