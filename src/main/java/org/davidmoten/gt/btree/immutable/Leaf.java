package org.davidmoten.gt.btree.immutable;

import java.util.ArrayList;
import java.util.List;

public final class Leaf<K, T> implements Node<K, T> {

    private final Context<K> context;
    private final List<Entry<K, T>> entries;

    public Leaf(Context<K> context, List<Entry<K, T>> entries) {
        this.context = context;
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
        if (entries.size() < context.maxChildren) {
            List<Entry<K, T>> list = new ArrayList<>(entries.size() + 1);
            list.addAll(entries);
            list.add(entry);
            return new Leaf<K, T>(context, list);
        } else {
            return null;
        }
    }

}
