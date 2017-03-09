package org.davidmoten.gt.btree;

import java.util.Comparator;

final class Context<Key, Value> {

    private final Comparator<Key> comparator;
    private final int maxChildren;
    private final NodeFactory<Key, Value> nodeFactory;

    private Context(Comparator<Key> comparator, int maxChildren, NodeFactory<Key,Value> nodeFactory) {
        this.comparator = comparator;
        this.maxChildren = maxChildren;
        this.nodeFactory = nodeFactory;
    }
    
    public static <Key,Value> Context<Key,Value> create(Comparator<Key> comparator, int maxChildren, NodeFactory<Key,Value> nodeFactory) {
        return new Context<Key,Value>(comparator, maxChildren, nodeFactory);
    }

    public Comparator<Key> comparator() {
        return comparator;
    }

    public int maxChildren() {
        return maxChildren;
    }
    
    public NodeFactory<Key,Value> nodeFactory(){
        return nodeFactory;
    }

}
