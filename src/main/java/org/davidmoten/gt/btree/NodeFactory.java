package org.davidmoten.gt.btree;

public interface NodeFactory<Key,Value> {

    Node<Key, Value> create();
}
