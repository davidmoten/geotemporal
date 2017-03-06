package org.davidmoten.gt.btree.immutable;

import java.util.List;

public final class NonLeaf<K, T> implements Node<K, T> {

    private final List<Node<K, T>> children;

    public NonLeaf(List<Node<K, T>> children) {
        this.children = children;
    }

    public Node<K, T> child(int i) {
        return children.get(i);
    }

    @Override
    public int count() {
        return children.size();
    }

}
