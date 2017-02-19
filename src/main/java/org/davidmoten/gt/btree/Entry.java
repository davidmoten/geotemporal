package org.davidmoten.gt.btree;

// internal nodes: only use key and next
// external nodes: only use key and value
class Entry<Key> {
    Key key;
    final Object val;
    Node<Key> next; // helper field to iterate over array entries

    Entry(Key key, Object val, Node<Key> next) {
        this.key = key;
        this.val = val;
        this.next = next;
    }
}