package org.davidmoten.gt.btree.immutable;

import java.util.Comparator;

import com.github.davidmoten.guavamini.Lists;

public class BTree<K, T> {

    private final Node<K, T> root;
    private final Comparator<K> comparator;

    public BTree(Node<K, T> root, Comparator<K> comparator) {
        this.root = root;
        this.comparator = comparator;
    }

    public BTree(Comparator<K> comparator) {
        this(null, comparator);
    }

    @SuppressWarnings("unchecked")
    public BTree<K, T> add(Entry<K, T> entry) {
        final Node<K, T> root2;
        if (root == null) {
            root2 = new Leaf<K, T>(Lists.newArrayList(entry));
        } else {
            root2 = root.add(entry);
        }
        return new BTree<K, T>(root2, comparator);
    }

    public BTree<K, T> add(K key, T value) {
        return add(new Entry<K, T>(key, value));
    }

}
