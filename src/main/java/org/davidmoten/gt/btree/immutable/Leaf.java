package org.davidmoten.gt.btree.immutable;

import java.util.ArrayList;
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

    @Override
    public Leaf<K, T> add(Entry<K, T> entry) {
        List<Entry<K, T>> list = new ArrayList<>(entries.size() + 1);
        list.addAll(entries);
        list.add(entry);
        return new Leaf<K, T>(list);

    }

}
