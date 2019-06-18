package com.company;

public abstract class ComparableContainer<T extends Comparable> implements Comparable<ComparableContainer<T>> {
    public abstract T getFirst();

    public abstract T getLast();

    /**
     * If this.least > other.biggest, this > other;
     * If this.biggest < other.least, this < other;
     * Otherwise, this == other.
     *
     * @param container the other container
     * @return -1 if this < other, 1 if this > other, 0 if this == other
     */
    public int compareTo(ComparableContainer<T> container) {
        T myFirst = getFirst(), otherFirst = container.getFirst();
        T myLast = getLast(), otherLast = container.getLast();

        if (myFirst == null && otherFirst != null) {
            return -1;
        } else if (otherFirst == null && myFirst != null) {
            return 1;
        } else if (myFirst == null) {
            return 0;
        }

        if (myFirst.compareTo(otherLast) >= 0) {
            return 1;
        } else if (myLast.compareTo(otherFirst) <= 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
