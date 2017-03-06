package org.davidmoten.gt.btree.immutable;

import java.util.List;

public final class Leaf<K, T> implements Node<K, T> {

    private final List<Entry<K, T>> entries;

    public Leaf(List<Entry<K, T>> entries) {
        this.entries = entries;
    }

    public Entry<K, T> entry(int i) {
        return entries.get(i);
    }

    @Override
    public int count() {
        return entries.size();
    }

}
