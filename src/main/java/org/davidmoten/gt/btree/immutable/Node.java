package org.davidmoten.gt.btree.immutable;

public interface Node<K, T> {

    int count();

    Node<K, T> add(Entry<K, T> entry);

}
