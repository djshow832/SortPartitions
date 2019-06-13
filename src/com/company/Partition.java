package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Partition containing sorted blocks.
 */
public class Partition<T extends Comparable> {

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
}
