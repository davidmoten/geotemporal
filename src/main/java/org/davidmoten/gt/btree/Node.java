package org.davidmoten.gt.btree;

public interface Node<Key, Value> {

    Key key(int j);

    Value value(int j);

    Node<Key, Value> next(int j);

    void setEntry(int i, Entry<Key, Value> entry);

    void insert(int j, Entry<Key, Value> t);

    Entry<Key, Value> entry(int i);

    int numEntries();

    boolean isFull();

    Node<Key, Value> split();

    Node<Key, Value> makeParentWith(Node<Key, Value> u);

}