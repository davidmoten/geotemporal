package org.davidmoten.gt.btree;

// helper B-tree node data type
final class Node<Key> {
    int m; // number of children
    @SuppressWarnings("unchecked")
    Entry<Key>[] children = new Entry[BTree.MAX_CHILDREN];

    // create a node with k children
    Node(int k) {
        m = k;
    }
}
