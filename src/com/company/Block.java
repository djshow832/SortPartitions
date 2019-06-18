package com.company;

import java.util.List;

/**
 * Block containing sorted array.
 */
public class Block<T extends Comparable> extends ComparableContainer<T> {

    private List<T> values;

    public Block(List<T> values){
        this.values = values;
    }

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    @Override
    public T getFirst() {
        if (values.isEmpty()) {
            return null;
        } else {
            return values.get(0);
        }
    }

    @Override
    public T getLast() {
        if (values.isEmpty()) {
            return null;
        } else {
            return values.get(values.size() - 1);
        }
    }
}
