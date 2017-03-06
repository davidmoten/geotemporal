package org.davidmoten.gt.btree.immutable;

import com.github.davidmoten.guavamini.Lists;

public class BTree<K, T> {

    private final Node<K, T> root;
    private final Context<K> context;

    public BTree(Node<K, T> root, Context<K> context) {
        this.root = root;
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public BTree<K, T> add(Entry<K, T> entry) {
        final Node<K, T> root2;
        if (root == null) {
            root2 = new Leaf<K, T>(context, Lists.newArrayList(entry));
        } else {
            root2 = root.insert(entry);
        }
        return new BTree<K, T>(root2, context);
    }

    public BTree<K, T> add(K key, T value) {
        return add(new Entry<K, T>(key, value));
    }

    private boolean less(K a, K b) {
        return context.comparator.compare(a, b) < 0;
    }

    private boolean eq(K a, K b) {
        return context.comparator.compare(a, b) == 0;
    }

}
