package org.davidmoten.gt.btree.immutable;

public final class Entry<K, T> {

    private final K key;
    private final T value;

    public Entry(K key, T value) {
        this.key = key;
        this.value = value;
    }

    public K key() {
        return this.key;
    }

    public T value() {
        return this.value;
    }

}
