package com.company;

import com.sun.tools.javac.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TestCase {

    private static int[][][][] testcases = new int[][][][] {
        {
        },
        {
            {}
        },
        {
            { { } }
        },
        {
            { { }, { } }
        },
        {
            { { 1 } }
        },
        {
            { { 1, 2, 3 }, { 4, 5, 6 } }
        },
        {
            { { 1 }, { 2 } }, { { 3 }, { 3 } }
        },
        {
            { { 2, 3 } },
            { { 1, 4 } }
        },
        {
            { { 1 }, { 2, 3 }, { 3, 4 } },
            { { 1 }, { 2, 3 }, { 3, 4 } }
        },
        {
            { { 1 }, { 2, 3 } },
            { { 3, 4 } },
            { { 1 }, { 2, 3 } }
        },
        {
            { { 5 }, { 6 } },
            { { 3 }, { 4 } },
            { { 1 }, { 2 } },
        },
        {
            { { 5 }, { 6 } },
            { { 3 }, { 4 } },
            { { 1 }, { 2 } },
            { { 7 }, { 8 } },
        },
        {
            { { 5 }, { 6 } },
            { { 3 }, { 4 } },
            { { 1 }, { 2 } },
            { { 7 }, { 8 } },
            { { 9 }, { 10 } },
        },
    };

    /**
     * Convert array to partition list.
     */
    private static List<Partition> convertArrayToPartitionList(int[][][] arrayList) {
        return Arrays.stream(arrayList).map(list -> new Partition(convertArrayToBlockList(list))).collect(
            Collectors.toList());
    }

    /**
     * Convert array to block list.
     */
    private static List<Block> convertArrayToBlockList(int[][] arrayList) {
        return Arrays.stream(arrayList)
            .map(array -> new Block(Arrays.stream(array).boxed().collect(Collectors.toList())))
            .collect(Collectors.toList());
    }

    /**
     * 1. Are the sizes of input and output equal?
     * 2. Is the output really sorted?
     *
     * @param testcase original data
     * @param result output data
     */
    private static void checkResult(int[][][] testcase, List result) {
        int totalNum = 0;
        for (int[][] chunkData : testcase) {
            for (int[] blockData : chunkData) {
                totalNum += blockData.length;
            }
        }

        Assert.check(totalNum == result.size());

        for (int i = 0; i < result.size() - 1; i++) {
            Assert.check((Integer) result.get(i) <= (Integer) result.get(i + 1));
        }
    }

    /**
     * Convert arrays to partitions and then test.
     * @param testcase arrays
     */
    private static void test(int[][][] testcase) {
        List<Partition> partitionList = convertArrayToPartitionList(testcase);
        List result = MergeSortK.sortPartitions(partitionList);
        checkResult(testcase, result);
    }

    /**
     * Test corner cases
     */
    public static void cornerCasesTest() {
        for (int[][][] testcase : testcases) {
            test(testcase);
        }
    }

    /**
     * Randomly produce some values for test. For convenience, each partition
     * only contains one block.
     */
    public static void randomTest() {
        final int TEST_TIMES = 100;
        final int MAX_PARTITION_COUNT = 1000;
        final int MAX_BLOCK_SIZE = 1000;

        Random random = new Random();
        for (int testTimes = 0; testTimes < TEST_TIMES; testTimes++) {
            int partitionCount = random.nextInt(MAX_PARTITION_COUNT);
            int[][][] partitions = new int[partitionCount][][];
            for (int partitionIndex = 0; partitionIndex < partitionCount; partitionIndex++) {
                int[][] blocks = new int[1][];
                for (int blockIndex = 0; blockIndex < 1; blockIndex++) {
                    int blockSize = random.nextInt(MAX_BLOCK_SIZE);
                    int[] block = new int[blockSize];
                    for (int valueIndex = 0; valueIndex < blockSize; valueIndex++) {
                        block[valueIndex] = random.nextInt(Integer.MAX_VALUE);
                    }
                    Arrays.sort(block);
                    blocks[blockIndex] = block;
                }
                partitions[partitionIndex] = blocks;
            }

            test(partitions);
        }
    }
}
