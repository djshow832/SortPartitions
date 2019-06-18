package com.company;

import com.sun.tools.javac.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Job for sorting.
 */
public class MergeSortJob implements Runnable {

    private List<Partition> inputList1, inputList2;
    private List<Partition> outputList;

    public MergeSortJob(List<Partition> inputList1, List<Partition> inputList2){
        this.inputList1 = inputList1;
        this.inputList2 = inputList2;
    }

    public List<Partition> getOutputList() {
        return outputList;
    }

    @Override
    public void run() {
        outputList = mergeSortContainer(inputList1, inputList2);
    }

    /**
     * Do merge-sort from two lists. inputList1 + inputList2 -> outputList.
     */
    private static List mergeSortValues(List<? extends Comparable> inputList1, List<? extends Comparable> inputList2) {
        List outputList = new ArrayList<>();
        for (int index1 = 0, index2 = 0; index1 < inputList1.size() || index2 < inputList2.size();) {
            if (index1 >= inputList1.size()) {
                outputList.add(inputList2.get(index2++));
            } else if (index2 >= inputList2.size()) {
                outputList.add(inputList1.get(index1++));
            } else {
                int result = inputList1.get(index1).compareTo(inputList2.get(index2));
                if (result < 0) {
                    outputList.add(inputList1.get(index1++));
                } else if (result > 0) {
                    outputList.add(inputList2.get(index2++));
                } else {
                    outputList.add(inputList1.get(index1++));
                    outputList.add(inputList2.get(index2++));
                }
            }
        }
        return outputList;
    }

    /**
     * Merge sort block list or partition list.
     * If block1 < block2 or block2 < block1, output the less one.
     * Otherwise, build a queue to store conflicting blocks, until
     * the next block in the input list is bigger than all of the
     * stored list in the queue. Then merge conflicting lists.
     *
     * @param inputList1 block list or partition list
     * @param inputList2 block list or partition list
     * @return output block list or partition list
     */
    private static List mergeSortContainer(List<? extends ComparableContainer> inputList1, List<? extends ComparableContainer> inputList2) {
        List outputList = new ArrayList<>();
        List tempList1 = new ArrayList(), tempList2 = new ArrayList();
        Comparable lastBiggest = null;
        for (int index1 = 0, index2 = 0; index1 < inputList1.size() || index2 < inputList2.size(); ) {
            // If the queue is empty
            if (lastBiggest == null) {
                if (index1 >= inputList1.size()) {
                    outputList.add(inputList2.get(index2++));
                } else if (index2 >= inputList2.size()) {
                    outputList.add(inputList1.get(index1++));
                } else {
                    int result = inputList1.get(index1).compareTo(inputList2.get(index2));
                    if (result < 0) {
                        outputList.add(inputList1.get(index1++));
                    } else if (result > 0) {
                        outputList.add(inputList2.get(index2++));
                    } else {
                        Comparable biggest1 = inputList1.get(index1).getLast();
                        Comparable biggest2 = inputList2.get(index2).getLast();

                        tempList1.add(inputList1.get(index1++));
                        tempList2.add(inputList2.get(index2++));

                        if (biggest1 == null) {
                            continue;
                        }

                        lastBiggest = biggest1.compareTo(biggest2) <= 0 ? biggest2 : biggest1;
                    }
                }
            } else {
                // Should the queue be ended
                boolean tempEnd = true;
                if (index1 < inputList1.size()) {
                    Comparable least1 = inputList1.get(index1).getFirst();
                    if (least1 == null) {
                        index1++;
                        tempEnd = false;
                    } else if (least1.compareTo(lastBiggest) < 0) {
                        tempList1.add(inputList1.get(index1));
                        Comparable biggest1 = inputList1.get(index1++).getLast();
                        lastBiggest = lastBiggest.compareTo(biggest1) <= 0 ? biggest1 : lastBiggest;
                        tempEnd = false;
                    }
                }
                if (index2 < inputList2.size()) {
                    Comparable least2 = inputList2.get(index2).getFirst();
                    if (least2 == null) {
                        index2++;
                        tempEnd = false;
                    } else if (least2.compareTo(lastBiggest) < 0) {
                        tempList2.add(inputList2.get(index2));
                        Comparable biggest2 = inputList2.get(index2++).getLast();
                        lastBiggest = lastBiggest.compareTo(biggest2) <= 0 ? biggest2 : lastBiggest;
                        tempEnd = false;
                    }
                }

                // merge conflicting lists
                if (tempEnd) {
                    outputList.addAll(mergeIntersectingContainers(tempList1, tempList2));
                    tempList1.clear();
                    tempList2.clear();
                    lastBiggest = null;
                }
            }
        }

        if (lastBiggest != null) {
            outputList.addAll(mergeIntersectingContainers(tempList1, tempList2));
        }

        return outputList;
    }

    /**
     * Merge two Containers. Partition merging is transformed to Block merging,
     * and Block merging is transformed to Value merging.
     */
    private static List<Comparable> mergeIntersectingContainers(List inputList1, List inputList2) {
        if (inputList1.get(0) instanceof Partition) {
            List<Block> blockList1 = new ArrayList<>(), blockList2 = new ArrayList<>();
            inputList1.forEach(partition -> blockList1.addAll(((Partition) partition).getBlockList()));
            inputList2.forEach(partition -> blockList2.addAll(((Partition) partition).getBlockList()));
            List<Comparable> outputBlockList = mergeSortContainer(blockList1, blockList2);
            return splitIntoContainers(outputBlockList, inputList1.size() + inputList2.size());
        } else {
            List<Comparable> valueList1 = new ArrayList<>(), valueList2 = new ArrayList<>();
            inputList1.forEach(block -> valueList1.addAll(((Block) block).getValues()));
            inputList2.forEach(block -> valueList2.addAll(((Block) block).getValues()));
            List<Comparable> outputValueList = mergeSortValues(valueList1, valueList2);
            return splitIntoContainers(outputValueList, inputList1.size() + inputList2.size());
        }
    }

    /**
     * Split Blocks into Partitions, or split Values into Blocks.
     *
     * @param elements value list or block list
     * @param listNum count of result list
     * @return block list or partition list
     */
    private static List<Comparable> splitIntoContainers(List<Comparable> elements, int listNum) {
        List<List<Comparable>> outputList = new ArrayList<>();

        int totalSize = elements.size();
        int eachSize = 0;
        if (totalSize > 0) {
            eachSize = listNum / totalSize;
        }

        // group elements
        for (int i = 0; ; i += eachSize) {
            if (outputList.size() == listNum - 1) {
                outputList.add(elements.subList(i, totalSize));
                break;
            } else {
                outputList.add(elements.subList(i, i + eachSize));
            }
        }

        if (elements.get(0) instanceof Block) {
            // block list -> partition list
            return outputList.stream().map(list -> new Partition(list)).collect(Collectors.toList());
        } else {
            // value list -> block list
            return outputList.stream().map(list -> new Block(list)).collect(Collectors.toList());
        }
    }
}
