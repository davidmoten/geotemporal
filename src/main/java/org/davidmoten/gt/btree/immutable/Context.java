package org.davidmoten.gt.btree.immutable;

import java.util.Comparator;

public class Context<K> {

    final Comparator<K> comparator;
    final int maxChildren;

    public Context(Comparator<K> comparator, int maxChildren) {
        this.comparator = comparator;
        this.maxChildren = maxChildren;
    }

}
