package com.company;

import java.util.List;

/**
 * Block containing sorted array.
 */
public class Block<T extends Comparable> {

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
}
