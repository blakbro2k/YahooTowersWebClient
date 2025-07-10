package asg.games.yokel.objects;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import asg.games.yipee.libgdx.game.YipeeBlockEvalGDX;
import asg.games.yipee.libgdx.objects.YipeeBlockGDX;
import asg.games.yipee.libgdx.objects.YipeeObjectGDX;
import asg.games.yokel.client.utils.YokelUtilities;

public class TestBlockEval {
    AtomicInteger atomicId = new AtomicInteger(0);

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    public void printBlockLabel(int block) {
        System.out.println(YipeeBlockEvalGDX.getPowerLabel(block) + " : " + YipeeBlockEvalGDX.getPowerUseDescriptionLabel(block));
    }

    @Test
    public void descriptionTest() throws Exception {
        System.out.println(YipeeBlockEvalGDX.getPowerUseDescriptionLabel(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR)) + "]Y_BLOCK-(OFFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR));
        System.out.println(YipeeBlockEvalGDX.getPowerUseDescriptionLabel(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR)) + "]Y_BLOCK-(OFFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR));
        System.out.println(YipeeBlockEvalGDX.getPowerUseDescriptionLabel(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA)) + "]Y_BLOCK-(OFFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA));
        System.out.println(YipeeBlockEvalGDX.getPowerUseDescriptionLabel(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR)) + "]Y_BLOCK-(DEFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR));
        System.out.println(YipeeBlockEvalGDX.getPowerUseDescriptionLabel(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR)) + "]Y_BLOCK-(DEFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR));
        System.out.println(YipeeBlockEvalGDX.getPowerUseDescriptionLabel(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA)) + "]Y_BLOCK-(DEFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA));

        System.out.println("Offensive Y minor=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR));

        System.out.println("O_BLOCK-(OFFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR));
        System.out.println("O_BLOCK-(OFFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR));
        System.out.println("O_BLOCK-(OFFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA));
        System.out.println("O_BLOCK-(DEFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR));
        System.out.println("O_BLOCK-(DEFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR));
        System.out.println("O_BLOCK-(DEFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA));

        System.out.println("K_BLOCK-(OFFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR));
        System.out.println("K_BLOCK-(OFFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR));
        System.out.println("K_BLOCK-(OFFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA));
        System.out.println("K_BLOCK-(DEFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR));
        System.out.println("K_BLOCK-(DEFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR));
        System.out.println("K_BLOCK-(DEFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA));

        System.out.println("E_BLOCK-(OFFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR));
        System.out.println("E_BLOCK-(OFFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR));
        System.out.println("E_BLOCK-(OFFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA));
        System.out.println("E_BLOCK-(DEFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR));
        System.out.println("E_BLOCK-(DEFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR));
        System.out.println("E_BLOCK-(DEFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA));

        System.out.println("L_BLOCK-(OFFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR));
        System.out.println("L_BLOCK-(OFFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR));
        System.out.println("L_BLOCK-(OFFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA));
        System.out.println("L_BLOCK-(DEFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR));
        System.out.println("L_BLOCK-(DEFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR));
        System.out.println("L_BLOCK-(DEFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA));

        System.out.println("EX_BLOCK-(OFFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR));
        System.out.println("EX_BLOCK-(OFFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR));
        System.out.println("EX_BLOCK-(OFFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA));
        System.out.println("EX_BLOCK-(DEFENSIVE_MINOR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR));
        System.out.println("EX_BLOCK-(DEFENSIVE_REGULAR)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR));
        System.out.println("EX_BLOCK-(DEFENSIVE_MEGA)=" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA));
    }

    private void testBlockPowers(int block, int power) {
        System.out.println("block: " + block);
        System.out.println("power: " + power);
        System.out.println("blockk: " + block);

        final int originalBlock = block;
        int powerLevel;
        String powerText;

        switch (power) {
            case YipeeBlockGDX.OFFENSIVE_MINOR:
                powerText = "OFFENSIVE_MINOR: ";
                powerLevel = 1;
                break;
            case YipeeBlockGDX.OFFENSIVE_REGULAR:
                powerText = "OFFENSIVE_REGULAR: ";
                powerLevel = 2;
                break;
            case YipeeBlockGDX.OFFENSIVE_MEGA:
                powerText = "OFFENSIVE_MEGA: ";
                powerLevel = 3;
                break;
            case YipeeBlockGDX.DEFENSIVE_MINOR:
                powerText = "DEFENSIVE_MINOR: ";
                powerLevel = 1;
                break;
            case YipeeBlockGDX.DEFENSIVE_REGULAR:
                powerText = "DEFENSIVE_REGULAR: ";
                powerLevel = 2;
                break;
            case YipeeBlockGDX.DEFENSIVE_MEGA:
            default:
                powerText = "DEFENSIVE_MEGA: ";
                powerLevel = 3;
                break;
        }

        block = YipeeBlockEvalGDX.setPowerFlag(block, power);
        System.out.println(powerText + block);
        System.out.println("CELL: " + YipeeBlockEvalGDX.getCellFlag(block));
        Assert.assertEquals(originalBlock, YipeeBlockEvalGDX.getCellFlag(block));

        //test add power flag
        block = YipeeBlockEvalGDX.addPowerBlockFlag(block);
        System.out.println(powerText + block);
        System.out.println("CELL: " + YipeeBlockEvalGDX.getCellFlag(block));
        Assert.assertEquals(originalBlock, YipeeBlockEvalGDX.getCellFlag(block));
        printBlockLabel(block);

        Assert.assertTrue(YipeeBlockEvalGDX.hasPowerBlockFlag(block));
        Assert.assertEquals(powerLevel, YipeeBlockEvalGDX.getPowerLevel(block));
        Assert.assertEquals(originalBlock, YipeeBlockEvalGDX.getCellFlag(block));
    }

    @Test
    public void YipeeBlockGDX_Y_Test() throws Exception {
        System.out.println("Start YipeeBlockGDX_Y_Test()");
        int y_block = YipeeBlockGDX.Y_BLOCK;
        int block = y_block;
        Assert.assertEquals(YipeeBlockGDX.Y_BLOCK, block);

        System.out.println("Y Block: " + YipeeBlockGDX.Y_BLOCK);
        printBlockLabel(block);

        //OFFENSIVE_MINOR = 3;
        block = YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);
        printBlockLabel(block);
        block = YipeeBlockEvalGDX.addPowerBlockFlag(block);
        printBlockLabel(block);

        //OFFENSIVE_MINOR = 3;
        testBlockPowers(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);

        //OFFENSIVE_REGULAR = 5;
        testBlockPowers(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR);

        //OFFENSIVE_MEGA = 7;
        testBlockPowers(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA);

        //DEFENSIVE_MINOR = 2;
        testBlockPowers(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR);

        //DEFENSIVE_REGULAR = 4;
        testBlockPowers(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR);

        //DEFENSIVE_MEGA = 6;
        testBlockPowers(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA);

        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(y_block));
        y_block = YipeeBlockEvalGDX.addBrokenFlag(y_block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasBrokenFlag(y_block));
        y_block = YipeeBlockEvalGDX.removeBrokenFlag(y_block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(y_block));

        System.out.println("End YipeeBlockGDX_Y_Test()");
    }

    @Test
    public void YipeeBlockGDX_O_Test() throws Exception {
        System.out.println("Start YipeeBlockGDX_O_Test()");
        int o_block = YipeeBlockGDX.A_BLOCK;
        int block = o_block;
        Assert.assertEquals(YipeeBlockGDX.A_BLOCK, block);

        System.out.println("O Block: " + YipeeBlockGDX.A_BLOCK);
        printBlockLabel(block);

        //OFFENSIVE_MINOR = 3;
        testBlockPowers(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);

        //OFFENSIVE_REGULAR = 5;
        testBlockPowers(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR);

        //OFFENSIVE_MEGA = 7;
        testBlockPowers(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA);

        //DEFENSIVE_MINOR = 2;
        testBlockPowers(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR);

        //DEFENSIVE_REGULAR = 4;
        testBlockPowers(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR);

        //DEFENSIVE_MEGA = 6;
        testBlockPowers(YipeeBlockGDX.A_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA);

        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(o_block));
        o_block = YipeeBlockEvalGDX.addBrokenFlag(o_block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasBrokenFlag(o_block));
        o_block = YipeeBlockEvalGDX.removeBrokenFlag(o_block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(o_block));

        System.out.println("End YipeeBlockGDX_O_Test()");
    }

    @Test
    public void YipeeBlockGDX_K_Test() throws Exception {
        System.out.println("Start YipeeBlockGDX_K_Test()");
        int k_block = YipeeBlockGDX.H_BLOCK;
        int block = k_block;
        Assert.assertEquals(YipeeBlockGDX.H_BLOCK, block);

        System.out.println("K Block: " + YipeeBlockGDX.H_BLOCK);
        //OFFENSIVE_MINOR = 3;
        block = YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);
        System.out.println("OFFENSIVE_MINOR: " + block);
        printBlockLabel(block);

        //test add power flag
        block = YipeeBlockEvalGDX.addPowerBlockFlag(block);
        System.out.println("OFFENSIVE_MINOR: " + block);
        System.out.println("CELL: " + YipeeBlockEvalGDX.getCellFlag(block));
        Assert.assertEquals(YipeeBlockGDX.H_BLOCK, YipeeBlockEvalGDX.getCellFlag(block));
        printBlockLabel(block);

        Assert.assertTrue(YipeeBlockEvalGDX.hasPowerBlockFlag(block));
        Assert.assertEquals(1, YipeeBlockEvalGDX.getPowerLevel(block));
        Assert.assertEquals(YipeeBlockGDX.H_BLOCK, YipeeBlockEvalGDX.getCellFlag(block));

        //OFFENSIVE_MINOR = 3;
        testBlockPowers(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);

        //OFFENSIVE_REGULAR = 5;
        testBlockPowers(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR);

        //OFFENSIVE_MEGA = 7;
        testBlockPowers(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA);

        //DEFENSIVE_MINOR = 2;
        testBlockPowers(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR);

        //DEFENSIVE_REGULAR = 4;
        testBlockPowers(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR);

        //DEFENSIVE_MEGA = 6;
        testBlockPowers(YipeeBlockGDX.H_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA);
        ;

        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(k_block));
        k_block = YipeeBlockEvalGDX.addBrokenFlag(k_block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasBrokenFlag(k_block));
        k_block = YipeeBlockEvalGDX.removeBrokenFlag(k_block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(k_block));
        System.out.println("End YipeeBlockGDX_K_Test()");
    }

    @Test
    public void YipeeBlockGDX_E_Test() throws Exception {
        System.out.println("Start YipeeBlockGDX_E_Test()");
        int e_block = YipeeBlockGDX.Op_BLOCK;
        int block = e_block;
        Assert.assertEquals(YipeeBlockGDX.Op_BLOCK, block);

        System.out.println("E Block: " + YipeeBlockGDX.Op_BLOCK);

        //OFFENSIVE_MINOR = 3;
        testBlockPowers(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);

        //OFFENSIVE_REGULAR = 5;
        testBlockPowers(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR);

        //OFFENSIVE_MEGA = 7;
        testBlockPowers(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA);

        //DEFENSIVE_MINOR = 2;
        testBlockPowers(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR);

        //DEFENSIVE_REGULAR = 4;
        testBlockPowers(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR);

        //DEFENSIVE_MEGA = 6;
        testBlockPowers(YipeeBlockGDX.Op_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA);

        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(e_block));
        e_block = YipeeBlockEvalGDX.addBrokenFlag(e_block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasBrokenFlag(e_block));
        e_block = YipeeBlockEvalGDX.removeBrokenFlag(e_block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(e_block));
        System.out.println("End YipeeBlockGDX_E_Test()");
    }

    @Test
    public void YipeeBlockGDX_L_Test() throws Exception {
        System.out.println("Start YipeeBlockGDX_L_Test()");
        int l_block = YipeeBlockGDX.Oy_BLOCK;
        int block = l_block;
        Assert.assertEquals(YipeeBlockGDX.Oy_BLOCK, block);

        System.out.println("L Block: " + YipeeBlockGDX.Oy_BLOCK);
        //OFFENSIVE_MINOR = 3;
        testBlockPowers(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);

        //OFFENSIVE_REGULAR = 5;
        testBlockPowers(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR);

        //OFFENSIVE_MEGA = 7;
        testBlockPowers(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA);

        //DEFENSIVE_MINOR = 2;
        testBlockPowers(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR);

        //DEFENSIVE_REGULAR = 4;
        testBlockPowers(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR);

        //DEFENSIVE_MEGA = 6;
        testBlockPowers(YipeeBlockGDX.Oy_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA);

        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(l_block));
        l_block = YipeeBlockEvalGDX.addBrokenFlag(l_block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasBrokenFlag(l_block));
        l_block = YipeeBlockEvalGDX.removeBrokenFlag(l_block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(l_block));
        System.out.println("End YipeeBlockGDX_L_Test()");
    }

    @Test
    public void YipeeBlockGDX_EX_Test() {
        System.out.println("Start YipeeBlockGDX_EX_Test()");
        int ex_block = YipeeBlockGDX.EX_BLOCK;
        int block = ex_block;
        Assert.assertEquals(YipeeBlockGDX.EX_BLOCK, block);

        System.out.println("EX Block: " + YipeeBlockGDX.EX_BLOCK);
        //OFFENSIVE_MINOR = 3;
        testBlockPowers(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.OFFENSIVE_MINOR);

        //OFFENSIVE_REGULAR = 5;
        testBlockPowers(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.OFFENSIVE_REGULAR);

        //OFFENSIVE_MEGA = 7;
        testBlockPowers(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA);

        //DEFENSIVE_MINOR = 2;
        testBlockPowers(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR);

        //DEFENSIVE_REGULAR = 4;
        testBlockPowers(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.DEFENSIVE_REGULAR);

        //DEFENSIVE_MEGA = 6;
        testBlockPowers(YipeeBlockGDX.EX_BLOCK, YipeeBlockGDX.DEFENSIVE_MEGA);

        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(ex_block));
        ex_block = YipeeBlockEvalGDX.addBrokenFlag(ex_block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasBrokenFlag(ex_block));
        ex_block = YipeeBlockEvalGDX.removeBrokenFlag(ex_block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasBrokenFlag(ex_block));
        System.out.println("End YipeeBlockGDX_EX_Test()");
    }

    @Test(dataProvider = "yokel_blocks_with_ids")
    public void testGetCellFlag(YipeeBlockGDX block, int blockType) throws Exception {
        System.out.println("Start testGetCellFlag()=" + block);
        int cellBlock = blockType;
        int cellFlag = YipeeBlockEvalGDX.setValueFlag(cellBlock, blockType);
        System.out.println("cellBlock=" + cellBlock);
        System.out.println("cellFlag=" + cellFlag);
        Assert.assertEquals(cellFlag, YipeeBlockEvalGDX.setValueFlag(cellBlock, blockType));
        Assert.assertEquals(cellBlock, YipeeBlockEvalGDX.getCellFlag(cellFlag));
        System.out.println("End testGetCellFlag()");
    }

    /*
        @Test
        public void testSetValueFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testHasAddedByYahooFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testAddArtificialFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testHasBrokenFlags() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testAddBrokenFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testRemoveBrokenFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }
    */
    @Test
    public void testHasPartnerBreakFlags() {
        System.out.println("Start testHasPartnerBreakFlags()");
        int block;
        int actual;

        //Normal Blocks
        block = YipeeBlockGDX.Y_BLOCK;
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        //block = YipeeBlockEvalGDX.addPowerBlockFlag(block, YipeeBlockEvalGDX.OFFENSIVE_MINOR);

        block = YipeeBlockGDX.A_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.H_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.Op_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.Oy_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.EX_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        //Offensive Blocks
        block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockGDX.Y_BLOCK);
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.A_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.H_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.Op_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.Oy_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));

        block = YipeeBlockGDX.EX_BLOCK;
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.addPartnerBreakFlag(block);
        Assert.assertTrue(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        actual = YipeeBlockEvalGDX.removePartnerBreakFlag(block);
        Assert.assertFalse(YipeeBlockEvalGDX.hasPartnerBreakFlag(actual));
        System.out.println("End testHasPartnerBreakFlags()");
    }

    /*
        @Test
        public void testAddPartnerBreakFlag() throws Exception {
            System.out.println("Start testAddPartnerBreakFlag()");
            System.out.println("End testAddPartnerBreakFlag()");
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testRemovePartnerBreakFlag() throws Exception {
            System.out.println("Start testRemovePartnerBreakFlag()");
            System.out.println("End testRemovePartnerBreakFlag()");
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testGetID() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testGetIDFlag() throws Exception {
            System.out.println("Start testPartnerBreakFlag()");
            System.out.println("End testPartnerBreakFlag()");
            throw new Exception("Test not implemented.");
        }
    */
    @Test
    public void testSetIDFlag() throws Exception {
        System.out.println("Start testSetIDFlag()");
        int v0 = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.OFFENSIVE_MEGA));
        System.out.println("powwah-V0-" + v0);
        int v1 = YipeeBlockEvalGDX.setIDFlag(v0, 1);
        System.out.println("-V1-" + v1);
        int v2 = YipeeBlockEvalGDX.getID(v1);
        System.out.println("-V2-" + v2);
        int v3 = YipeeBlockEvalGDX.setValueFlag(v2, v2);
        System.out.println("-V3-" + v3);
        System.out.println("ttm-" + ((v1 & ~0x7f000) | 1 >> 12));

        int v10 = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, YipeeBlockGDX.DEFENSIVE_MINOR));
        System.out.println("powwah-V0-" + v10);
        int v11 = YipeeBlockEvalGDX.setIDFlag(v10, 2);
        System.out.println("-V1-" + v11);
        int v12 = YipeeBlockEvalGDX.getID(v11);
        System.out.println("-V2-" + v12);
        int v13 = YipeeBlockEvalGDX.setValueFlag(v11, v12);
        System.out.println("-V3-" + v13);
        System.out.println("End testSetIDFlag()");
    }

    /*
        @Test
        public void testGetPowerFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }
    */
    @Test
    public void testSetPowerFlag() throws Exception {
        System.out.println("power 8" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, 8));
        System.out.println("power 8" + YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, 1));
        int block = YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockEvalGDX.setPowerFlag(YipeeBlockGDX.Y_BLOCK, 4));
        Assert.assertEquals(YipeeBlockEvalGDX.getCellFlag(block), YipeeBlockGDX.Y_BLOCK);
        System.out.println("power 8" + YipeeBlockEvalGDX.isOffensive(block));
        System.out.println("power level = " + YipeeBlockEvalGDX.getPowerLevel(block));

    }

    /*
        @Test
        public void testHasSpecialFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testAddSpecialFlag() throws Exception {
            throw new Exception("Test not implemented.");
        }
    */
    @Test
    public void testHasPowerBlockFlag() throws Exception {
        int block = YipeeBlockGDX.Y_BLOCK;
        int actual = YipeeBlockEvalGDX.addPowerBlockFlag(block);

        Assert.assertTrue(YipeeBlockEvalGDX.hasPowerBlockFlag(actual));
        Assert.assertFalse(YipeeBlockEvalGDX.hasPowerBlockFlag(block));
    }

    @Test
    public void testAddPowerBlockFlag() throws Exception {
        System.out.println("Start testAddPowerBlockFlag()");

        int block;
        int actual;

        //Normal Blocks
        block = YipeeBlockGDX.Y_BLOCK;
        actual = YipeeBlockEvalGDX.addPowerBlockFlag(block);

        System.out.println("BLOCK: " + block);
        System.out.println("POWER_BLOCK: " + actual);
        System.out.println("HAS Block Flag: " + YipeeBlockEvalGDX.hasPowerBlockFlag(actual));
        System.out.println("BLOCK: " + YipeeBlockGDX.A_BLOCK);
        System.out.println("POWER_BLOCK: " + YipeeBlockEvalGDX.addPowerBlockFlag(YipeeBlockGDX.A_BLOCK));

        System.out.println("End testAddPowerBlockFlag()");

        Assert.assertTrue(YipeeBlockEvalGDX.hasPowerBlockFlag(actual));
        Assert.assertFalse(YipeeBlockEvalGDX.hasPowerBlockFlag(block));
    }

    /*
        @Test
        public void testIsOffensive() throws Exception {
            throw new Exception("Test not implemented.");
        }

        @Test
        public void testGetPowerLevel() throws Exception {
            throw new Exception("Test not implemented.");
        }
    */
    @DataProvider(name = "yokel_objects")
    public Object[][] provideYipeeObjectGDXs() {
        YipeeBlockGDX yokelBlockY = new YipeeBlockGDX(0, 0, 0);
        YipeeBlockGDX yokelBlockA = new YipeeBlockGDX(0, 0, 1);
        YipeeBlockGDX yokelBlockH = new YipeeBlockGDX(0, 0, 2);
        YipeeBlockGDX yokelBlockOp = new YipeeBlockGDX(0, 0, 3);
        YipeeBlockGDX yokelBlockOy = new YipeeBlockGDX(0, 0, 4);
        YipeeBlockGDX yokelBlockBsh = new YipeeBlockGDX(0, 0, 5);
        YipeeBlockGDX yokelBlockClear = new YipeeBlockGDX(0, 0, 6);
        YipeeBlockGDX yokelBlockStone = new YipeeBlockGDX(0, 0, 7);
        YipeeBlockGDX yokelBlockMedusa = new YipeeBlockGDX(0, 0, 9);

        return new Object[][]{
                {yokelBlockY, yokelBlockY.getBlockType()},
                {yokelBlockA, yokelBlockA.getBlockType()},
                {yokelBlockH, yokelBlockH.getBlockType()},
                {yokelBlockOp, yokelBlockOp.getBlockType()},
                {yokelBlockOy, yokelBlockOy.getBlockType()},
                {yokelBlockBsh, yokelBlockBsh.getBlockType()},
                {yokelBlockClear, yokelBlockClear.getBlockType()},
                {yokelBlockStone, yokelBlockStone.getBlockType()},
                {yokelBlockMedusa, yokelBlockMedusa.getBlockType()}
        };
    }

    /*
        @Test
        public void testGetNormalLabel() throws Exception {
            System.out.println("Start testGetNormalLabel()");
            System.out.println("End testGetNormalLabel()");
            throw new Exception("Test not implemented.");
        }
    */
    @DataProvider(name = "yokel_blocks_with_ids")
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

    private void setIdAndName(YipeeObjectGDX... YipeeObjectGDXs) {
        for (YipeeObjectGDX yipeeObjectGDX : YokelUtilities.safeIterable(YipeeObjectGDXs)) {
            if (yipeeObjectGDX != null) {
                int id = atomicId.getAndIncrement();
                yipeeObjectGDX.setId(YokelUtilities.IDGenerator.getID());
                yipeeObjectGDX.setName(id + "-" + yipeeObjectGDX.getClass().getSimpleName());
            }
        }
    }
}