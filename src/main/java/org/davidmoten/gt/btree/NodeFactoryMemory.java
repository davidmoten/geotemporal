package org.davidmoten.gt.btree;

public class NodeFactoryMemory<Key,Value> implements NodeFactory<Key,Value> {

    @Override
    public Node<Key, Value> create(int size) {
        return new NodeMemory<>(size);
    }

}
