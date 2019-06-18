package com.company;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 1. Split the work into jobs and put them into work queue.
 * 2. Use worker threads to consume jobs.
 * 3. Every two jobs produce one new job. Put the new job back to the queue.
 *
 * One possible work path:
 * Data: (3), (1), (5), (2), (4), Threads: 2
 * Thread1: (3) + (1) => [(1), (3)]
 * Thread2: (5) + (2) => [(2), (5)]
 * Thread1: (4) + [(1), (3)] => [(1), (3), (4)]
 * Thread1: [(2), (5)] + [(1), (3), (4)] => [(1), (2), (3), (4), (5)]
 */
public class MergeSortThreadPoolExecutor extends ThreadPoolExecutor {

    // Queue stores sorted lists.
    private List<List<Partition>> partitionLists;
    // The total value count in all lists.
    private int        totalValueCount;

    public MergeSortThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                       List<List<Partition>> partitionLists){
        super(corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(partitionLists.size() / 2));

        initPartitionLists(partitionLists);
    }

    /**
     * Initialize members.
     *
     * @param inputLists original 2d array.
     */
    private void initPartitionLists(List<List<Partition>> inputLists) {
        totalValueCount = inputLists.stream().map(List::size).reduce(0, Integer::sum);
        partitionLists = new LinkedList<>(inputLists);
    }

    /**
     * Consume lists and produce jobs.
     */
    public void start() {
        List<Runnable> jobs = new ArrayList<>(partitionLists.size() / 2);
        Runnable job;
        while ((job = peekNewJob()) != null) {
            jobs.add(job);
        }
        // Remove all nodes from partitionLists and then start running.
        // Don't do it simultaneously.
        jobs.forEach(this::execute);
    }

    /**
     * Get the sorted result after it's done.
     *
     * @return the sorted list.
     */
    public List getSortResult() {
        List result = new ArrayList();
        for (List<Partition> partitionList : partitionLists) {
            for (Partition partition : partitionList) {
                result.addAll(partition.getValueList());
            }
        }
        return result;
    }

    /**
     * Judge if it's all finished according to the merged result.
     *
     * @return true if finished or false otherwise.
     */
    private boolean isFinished() {
        return partitionLists.size() == 1 && partitionLists.get(0).size() == totalValueCount;
    }

    /**
     * Append the new list to the queue after each worker finishes.
     * If there's at least 2 lists in the queue, produce a new job, and consume it.
     *
     * @param r the finished job
     * @param t exceptions
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        // Add lock to this code to make it atomic, preventing from two
        // threads checking queue size simultaneously and both stop.
        synchronized (this) {
            if (t == null && r instanceof MergeSortJob) {
                // Get the new sorted list, put it back to the queue.
                List<Partition> outputList = ((MergeSortJob) r).getOutputList();
                partitionLists.add(outputList);

                // If it's the very last one, shutdown.
                if (isFinished()) {
                    shutdown();
                }

                // Produce a new job and consume it.
                Runnable job = peekNewJob();
                if (job != null) {
                    execute(job);
                }
            }
        }
    }

    /**
     * Combine two list into one job. Always peek lists from the head.
     *
     * @return A job if there's one, or null if none.
     */
    private Runnable peekNewJob() {
        if (partitionLists.size() > 1) {
            return new MergeSortJob(partitionLists.remove(0), partitionLists.remove(0));
        }
        return null;
    }
}
