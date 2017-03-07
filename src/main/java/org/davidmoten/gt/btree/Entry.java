package org.davidmoten.gt.btree;

// internal nodes: only use key and next
// external nodes: only use key and value
final class Entry<Key, Value> {

    private Key key;
    final Value val;
    private Node<Key, Value> next; // helper field to iterate over array entries

    Entry(Key key, Value val, Node<Key, Value> next) {
        this.key = key;
        this.val = val;
        this.next = next;
    }

    void setKey(Key key) {
        this.key = key;
    }

    void setNext(Node<Key, Value> node) {
        this.next = node;
    }

    Key key() {
        return key;
    }

    Node<Key, Value> next() {
        return next;
    }
}