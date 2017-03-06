package org.davidmoten.gt.btree;

import java.util.Comparator;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

public final class BTree<Key, Value> {

    // must be even and greater than or equal to 4
    static final int MAX_CHILDREN = 4;

    private Node<Key> root;
    private final Comparator<Key> comparator;
    private int height;
    private int size;

    public BTree(Comparator<Key> comparator) {
        this.comparator = comparator;
        root = new Node<Key>(0);
    }

    public Value get(Key key) {
        Preconditions.checkNotNull(key, "key cannot be null");
        return search(root, key, height);
    }

    @SuppressWarnings("unchecked")
    private Value search(Node<Key> x, Key key, int ht) {
        Entry<Key>[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                if (eq(key, children[j].key))
                    return (Value) children[j].val;
            }
        }

        // internal node
        else {
            for (int j = 0; j < x.m; j++) {
                if (j + 1 == x.m || less(key, children[j + 1].key))
                    return search(children[j].next, key, ht - 1);
            }
        }
        return null;
    }

    public void put(Key key, Value val) {
        Preconditions.checkNotNull(key, "key cannot be null");
        Node<Key> u = insert(root, key, val, height);
        size++;
        if (u == null)
            return;

        // need to split root
        Node<Key> t = new Node<Key>(2);
        t.children[0] = new Entry<Key>(root.children[0].key, null, root);
        t.children[1] = new Entry<Key>(u.children[0].key, null, u);
        root = t;
        height++;
    }

    private Node<Key> insert(Node<Key> h, Key key, Value val, int height) {
        int j;
        Entry<Key> t = new Entry<Key>(key, val, null);

        // external node
        if (height == 0) {
            for (j = 0; j < h.m; j++) {
                if (less(key, h.children[j].key))
                    break;
            }
        }

        // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j + 1 == h.m) || less(key, h.children[j + 1].key)) {
                    Node<Key> u = insert(h.children[j++].next, key, val, height - 1);
                    if (u == null)
                        return null;
                    t.key = u.children[0].key;
                    t.next = u;
                    break;
                }
            }
        }

        for (int i = h.m; i > j; i--)
            h.children[i] = h.children[i - 1];
        h.children[j] = t;
        h.m++;
        if (h.m < MAX_CHILDREN)
            return null;
        else
            return split(h);
    }

    private Node<Key> split(Node<Key> h) {
        Node<Key> t = new Node<Key>(MAX_CHILDREN / 2);
        h.m = MAX_CHILDREN / 2;
        for (int j = 0; j < MAX_CHILDREN / 2; j++)
            t.children[j] = h.children[MAX_CHILDREN / 2 + j];
        return t;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    @VisibleForTesting
    int height() {
        return height;
    }

    private boolean less(Key k1, Key k2) {
        return comparator.compare(k1, k2) < 0;
    }

    private boolean eq(Key k1, Key k2) {
        return comparator.compare(k1, k2) == 0;
    }

    private String toString(Node<Key> h, int ht, String indent) {
        StringBuilder s = new StringBuilder();
        Entry<Key>[] children = h.children;

        if (ht == 0) {
            for (int j = 0; j < h.m; j++) {
                s.append(indent + children[j].key + " " + children[j].val + "\n");
            }
        } else {
            for (int j = 0; j < h.m; j++) {
                if (j > 0)
                    s.append(indent + "(" + children[j].key + ")\n");
                s.append(toString(children[j].next, ht - 1, indent + "     "));
            }
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return toString(root, height, "") + "\n";
    }

}
