package org.davidmoten.gt.btree;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public final class BTree<Key, Value> {

    private final Context<Key, Value> context;

    // mutable
    private Node<Key, Value> root;
    private int height;
    private int size;

    public BTree(Context<Key, Value> context) {
        this.context = context;
        root = context.nodeFactory().create(0);
    }

    public Value get(Key key) {
        Preconditions.checkNotNull(key, "key cannot be null");
        return search(root, key, height);
    }

    private Value search(Node<Key, Value> x, Key key, int ht) {
        // external node
        if (ht == 0) {
            for (int j = 0; j < x.numEntries(); j++) {
                if (eq(key, x.key(j))) {
                    return x.value(j);
                }
            }
        }

        // internal node
        else {
            for (int j = 0; j < x.numEntries(); j++) {
                if (j + 1 == x.numEntries() || less(key, x.key(j + 1)))
                    return search(x.next(j), key, ht - 1);
            }
        }
        return null;
    }

    public Observable<Value> range(Key lowerInclusive, Key upperExclusive) {
        return range(root, lowerInclusive, upperExclusive, height);
    }

    private Observable<Value> range(Node<Key, Value> x, Key lowerInclusive, Key upperExclusive,
            int height) {
        return Observable.create(new ObservableOnSubscribe<Value>() {

            @Override
            public void subscribe(ObservableEmitter<Value> emitter) throws Exception {
                range(x, lowerInclusive, upperExclusive, height, emitter);
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private boolean range(Node<Key, Value> x, Key lower, Key upper, int ht,
            ObservableEmitter<Value> emitter) {

        if (ht == 0) {
            // external node
            for (int j = 0; j < x.numEntries(); j++) {
                if (emitter.isDisposed()) {
                    return false;
                }
                if (geq(x.key(j), lower) && less(x.key(j), upper)) {
                    emitter.onNext(x.value(j));
                }
            }
        } else {
            // internal node
            for (int j = 0; j < x.numEntries(); j++) {
                if (j + 1 == x.numEntries() || (less(lower, x.key(j + 1)))) {
                    if (emitter.isDisposed()) {
                        return false;
                    }
                    if (!range(x.next(j), lower, upper, ht - 1, emitter)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void put(Key key, Value val) {
        Preconditions.checkNotNull(key, "key cannot be null");
        Node<Key, Value> u = insert(root, key, val, height);
        size++;
        if (u == null)
            return;

        // need to give root a new parent
        root = root.makeParentWith(u);
        height++;
    }

    private Node<Key, Value> insert(Node<Key, Value> h, Key key, Value val, int height) {
        int j;
        Entry<Key, Value> t = new Entry<Key, Value>(key, val, null);

        // external node
        if (height == 0) {
            for (j = 0; j < h.numEntries(); j++) {
                if (less(key, h.key(j)))
                    break;
            }
        }

        // internal node
        else {
            for (j = 0; j < h.numEntries(); j++) {
                if ((j + 1 == h.numEntries()) || less(key, h.key(j + 1))) {
                    Node<Key, Value> u = insert(h.next(j++), key, val, height - 1);
                    if (u == null)
                        return null;
                    t.setKey(u.key(0));
                    t.setNext(u);
                    break;
                }
            }
        }

        h.insert(j, t);

        if (h.isFull())
            return h.split();
        else
            return null;
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

    private boolean less(Key a, Key b) {
        return context.comparator().compare(a, b) < 0;
    }

    private boolean eq(Key a, Key b) {
        return context.comparator().compare(a, b) == 0;
    }

    private boolean geq(Key a, Key b) {
        return context.comparator().compare(a, b) >= 0;
    };

    private String toString(Node<Key, Value> h, int ht, String indent) {
        StringBuilder s = new StringBuilder();

        if (ht == 0) {
            for (int j = 0; j < h.numEntries(); j++) {
                s.append(indent + h.key(j) + " " + h.value(j) + "\n");
            }
        } else {
            for (int j = 0; j < h.numEntries(); j++) {
                if (j > 0) {
                    s.append(indent + "(" + h.key(j) + ")\n");
                }
                s.append(toString(h.next(j), ht - 1, indent + "     "));
            }
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return toString(root, height, "") + "\n";
    }

}
