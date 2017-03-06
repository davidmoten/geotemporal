package org.davidmoten.gt.btree.immutable;

public class BTree<K, T> {

    private final Node<K, T> root;

    public BTree(Node<K, T> root) {
        this.root = root;
    }

    public BTree<K, T> add(Entry<K, T> entry) {
        return this;
    }

}
