package org.davidmoten.gt.btree;

import java.util.Comparator;

final class Context<T> {

    private final Comparator<T> comparator;
    private final int maxChildren;

    Context(Comparator<T> comparator, int maxChildren) {
        this.comparator = comparator;
        this.maxChildren = maxChildren;
    }

    Comparator<T> comparator() {
        return comparator;
    }

    int maxChildren() {
        return maxChildren;
    }

}
