package asg.games.yokel.client.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Blakbro2k on 12/29/2017.
 */

public class RandomUtil {
    private static final int ATTACK_SECTION = 6;
    private static final int SECTION_GROUP_NUM = 6;
    private static final int DEFENSE_SECTION = 12;

    /**
     * @see <a href="http://stackoverflow.com/a/1973018">Stack Overflow</a>
     */
    private static class RandomEnum<E extends Enum> {

        private static final Random RND = new Random();
        private final E[] values;

        public RandomEnum(Class<E> token) {
            values = token.getEnumConstants();
        }

        /**
         * @return a random value for the given enums
         */
        public E random() {
            return values[RND.nextInt(values.length)];
        }

        /**
         * @return a random value for the given enums and a min and max range
         */
        public E random(int min) {
            return values[min + RND.nextInt(SECTION_GROUP_NUM)];
        }
    }

    public static final class RandomNumber {
        private long seed;
        //private boolean c = false;
        //private static final long f = (1L << 48) - 1L;

        public RandomNumber() {
            this(System.currentTimeMillis());
        }

        public RandomNumber(long l) {
            generateSeedByKey(l);
        }

        public synchronized void generateSeedByKey(long l) {
            seed = (l ^ 0x5deece66dL) & (1L << 48) - 1L;
            //c = false;
        }

        protected synchronized int moveSeed(int i) {
            long l = seed * 25214903917L + 11L & (1L << 48) - 1L;
            seed = l;
            return (int) (l >>> 48 - i);
        }

        public int generateCappedNumber() {
            return moveSeed(32);
        }

        public int next(int i) {
            return (generateCappedNumber() & 0x7fffffff) % i;
        }

        public void printOut(DataOutputStream dataoutputstream) throws IOException {
            dataoutputstream.writeLong(seed);
        }

        public void readIn(DataInputStream datainputstream) throws IOException {
            seed = datainputstream.readLong();
        }
    }

    public static class RandomNumberArray {
        int[] randomNumbers;

        public RandomNumberArray(int byteLength, long seed, int maxValue) {
            randomNumbers = new int[byteLength];
            RandomNumber numberGenerator = new RandomNumber(seed);
            for (int i = 0; i < byteLength; i++)
                randomNumbers[i] = numberGenerator.next(maxValue);
        }

        public int getRandomNumberAt(int index) {
            if (index < 0)
                System.out.println("Assertion failure: invalid random index " + index);
            return randomNumbers[index % randomNumbers.length];
        }
    }

    public static class TestRandomBlockArray extends RandomUtil.RandomNumberArray {
        int[] testRandomNumbers;

        public TestRandomBlockArray(int byteLength, long seed, int maxValue) {
            super(byteLength, seed, maxValue);
            testRandomNumbers = new int[byteLength];
            int count = 0;
            int triplet = 0;

            for (int i = 0; i < byteLength; i++) {
                testRandomNumbers[i] = count;
                ++triplet;
                if (triplet > 2) {
                    triplet = 0;
                    ++count;
                    if (count > maxValue - 1) {
                        count = 0;
                    }
                }
            }
        }

        @Override
        public int getRandomNumberAt(int index) {
            if (index < 0)
                System.out.println("Assertion failure: invalid random index " + index);
            return testRandomNumbers[index % testRandomNumbers.length];
        }
    }
}
