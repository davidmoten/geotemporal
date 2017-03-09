package org.davidmoten.gt.btree;

// helper B-tree node data type
public final class NodeMemory<Key, Value> implements Node<Key, Value> {

    // must be even and greater than or equal to 4
    private static final int MAX_CHILDREN = 4;

    private int m; // number of children

    @SuppressWarnings("unchecked")
    private final Entry<Key, Value>[] children = new Entry[MAX_CHILDREN];

    // create a node with k children
    public NodeMemory(int k) {
        m = k;
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#key(int)
     */
    @Override
    public Key key(int j) {
        return children[j].key();
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#value(int)
     */
    @Override
    public Value value(int j) {
        return (Value) children[j].value();
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#next(int)
     */
    @Override
    public Node<Key, Value> next(int j) {
        return children[j].next();
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#setEntry(int, org.davidmoten.gt.btree.Entry)
     */
    @Override
    public void setEntry(int i, Entry<Key, Value> entry) {
        children[i] = entry;
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#insert(int, org.davidmoten.gt.btree.Entry)
     */
    @Override
    public void insert(int j, Entry<Key, Value> t) {
        for (int i = m; i > j; i--) {
            children[i] = children[i - 1];
        }
        children[j] = t;
        m++;
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#entry(int)
     */
    @Override
    public Entry<Key, Value> entry(int i) {
        return children[i];
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#numEntries()
     */
    @Override
    public int numEntries() {
        return m;
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#isFull()
     */
    @Override
    public boolean isFull() {
        return m == MAX_CHILDREN;
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#split()
     */
    @Override
    public Node<Key, Value> split() {
        m = MAX_CHILDREN / 2;
        Node<Key, Value> t = new NodeMemory<Key, Value>(m);
        for (int j = 0; j < m; j++) {
            t.setEntry(j, entry(m + j));
        }
        return t;
    }

    /* (non-Javadoc)
     * @see org.davidmoten.gt.btree.Node#makeParentWith(org.davidmoten.gt.btree.Node)
     */
    @Override
    public Node<Key, Value> makeParentWith(Node<Key, Value> u) {
        Node<Key, Value> t = new NodeMemory<Key, Value>(2);
        t.setEntry(0, new Entry<Key, Value>(this.key(0), null, this));
        t.setEntry(1, new Entry<Key, Value>(u.key(0), null, u));
        return t;
    }
}
