package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Entrance for sorting partitions. Use multi-threads to do sorting.
 */
public class MergeSortK {

    /* Thread pool configurations */
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 2;
    private static final int KEEP_ALIVE_TIME = 5000;

    /**
     * Sort partitions, each of which is originally sorted by blocks.
     *
     * @param partitions partitions to be sorted
     * @return sorted value list
     */
    public static List sortPartitions(List<Partition> partitions) {
        if (partitions.size() == 0) {
            return new ArrayList();
        }
        if (partitions.size() == 1) {
            return partitions.get(0).getValueList();
        }

        // Put each partition into a single list
        List<List<Partition>> partitionLists = partitions.stream().map(Arrays::asList).collect(Collectors.toList());

        // Create a thread pool
        MergeSortThreadPoolExecutor threadPoolExecutor = new MergeSortThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, partitionLists);
        threadPoolExecutor.prestartAllCoreThreads();

        // Start sorting
        threadPoolExecutor.start();
        try {
            threadPoolExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("interrupted");
            return new ArrayList();
        }

        // Return sorted result
        return threadPoolExecutor.getSortResult();
    }
}
