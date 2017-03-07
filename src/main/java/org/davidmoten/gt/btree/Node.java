package org.davidmoten.gt.btree;

// helper B-tree node data type
final class Node<Key, Value> {

    // must be even and greater than or equal to 4
    private static final int MAX_CHILDREN = 4;

    private int m; // number of children
    @SuppressWarnings("unchecked")
    private Entry<Key, Value>[] children = new Entry[MAX_CHILDREN];

    // create a node with k children
    Node(int k) {
        m = k;
    }

    Key key(int j) {
        return children[j].key();
    }

    Value value(int j) {
        return (Value) children[j].val;
    }

    Node<Key, Value> next(int j) {
        return children[j].next();
    }

    void setEntry(int i, Entry<Key, Value> entry) {
        children[i] = entry;
    }

    void insert(int j, Entry<Key, Value> t) {
        for (int i = m; i > j; i--) {
            children[i] = children[i - 1];
        }
        children[j] = t;
        m++;
    }

    Entry<Key, Value> entry(int i) {
        return children[i];
    }

    int numEntries() {
        return m;
    }

    boolean isFull() {
        return m == MAX_CHILDREN;
    }

    Node<Key, Value> split() {
        m = MAX_CHILDREN / 2;
        Node<Key, Value> t = new Node<Key, Value>(m);
        for (int j = 0; j < m; j++) {
            t.setEntry(j, entry(m + j));
        }
        return t;
    }
}
