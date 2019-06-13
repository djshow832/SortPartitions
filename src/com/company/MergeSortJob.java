package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Job for sorting.
 */
public class MergeSortJob implements Runnable {

    private List inputList1, inputList2;
    private List outputList;

    public MergeSortJob(List inputList1, List inputList2){
        this.inputList1 = inputList1;
        this.inputList2 = inputList2;
    }

    public List getOutputList() {
        return outputList;
    }

    @Override
    public void run() {
        mergeSort();
    }

    /**
     * Do merge-sort from two lists. inputList1 + inputList2 -> outputList.
     */
    private void mergeSort() {
        outputList = new ArrayList<>();
        for (int index1 = 0, index2 = 0; index1 < inputList1.size() || index2 < inputList2.size();) {
            if (index1 >= inputList1.size()) {
                outputList.add(inputList2.get(index2++));
            } else if (index2 >= inputList2.size()) {
                outputList.add(inputList1.get(index1++));
            } else {
                int result = ((Comparable) inputList1.get(index1)).compareTo(inputList2.get(index2));
                if (result <= 0) {
                    outputList.add(inputList1.get(index1++));
                } else {
                    outputList.add(inputList2.get(index2++));
                }
            }
        }
    }
}
