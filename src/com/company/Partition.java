package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Partition containing sorted blocks.
 */
public class Partition<T extends Comparable> extends ComparableContainer<T> {

    private List<Block<T>> blockList;

    public Partition(List<Block<T>> blockList){
        this.blockList = blockList;
    }

    public List<Block<T>> getBlockList() {
        return blockList;
    }

    public List<T> getValueList() {
        List<T> valueList = new ArrayList<>();
        blockList.forEach(block -> valueList.addAll(block.getValues()));
        return valueList;
    }

    @Override
    public T getFirst() {
        for (Block<T> block : blockList) {
            T first = block.getFirst();
            if (first != null) {
                return first;
            }
        }
        return null;
    }

    @Override
    public T getLast() {
        for (int i = blockList.size() - 1; i >= 0; i--) {
            Block<T> block = blockList.get(i);
            T last = block.getLast();
            if (last != null) {
                return last;
            }
        }
        return null;
    }
}
