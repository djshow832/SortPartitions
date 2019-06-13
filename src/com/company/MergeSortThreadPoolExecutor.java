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
 * Thread1: (3) + (1) => (1, 3)
 * Thread2: (5) + (2) => (2, 5)
 * Thread1: (4) + (1, 3) => (1, 3, 4)
 * Thread1: (2, 5) + (1, 3, 4) => (1, 2, 3, 4, 5)
 */
public class MergeSortThreadPoolExecutor extends ThreadPoolExecutor {

    // Queue stores sorted lists.
    private List<List> valueLists;
    // The total value count in all lists.
    private int        totalValueCount;

    public MergeSortThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                       List<List> valueLists){
        super(corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(valueLists.size() / 2));

        initValueLists(valueLists);
    }

    /**
     * Initialize members.
     *
     * @param inputLists original 2d array.
     */
    private void initValueLists(List<List> inputLists) {
        totalValueCount = inputLists.stream().map(List::size).reduce(0, Integer::sum);
        valueLists = new LinkedList<>(inputLists);
    }

    /**
     * Consume lists and produce jobs.
     */
    public void start() {
        List<Runnable> jobs = new ArrayList<>(valueLists.size() / 2);
        Runnable job;
        while ((job = peekNewJob()) != null) {
            jobs.add(job);
        }
        // Remove all nodes from valueLists and then start running. Don't do it
        // parallel.
        jobs.forEach(this::execute);
    }

    /**
     * Get the sorted result after it's done.
     *
     * @return the sorted list.
     */
    public List getSortResult() {
        return valueLists.get(0);
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
                List outputBlockList = ((MergeSortJob) r).getOutputList();
                valueLists.add(outputBlockList);

                // If it's the very last one, shutdown.
                if (outputBlockList.size() == totalValueCount) {
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
        if (valueLists.size() > 1) {
            return new MergeSortJob(valueLists.remove(0), valueLists.remove(0));
        }
        return null;
    }
}
