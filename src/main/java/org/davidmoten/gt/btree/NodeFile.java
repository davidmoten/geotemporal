package org.davidmoten.gt.btree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class NodeFile<Key extends Serializable, Value extends Serializable>
        implements Node<Key, Value> {

    private final File file;
    private final int position;

    public NodeFile(File file, int position) {
        this.file = file;
        this.position = position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Key key(int j) {
        return (Key) readObject(j);
    }

    private Object readObject(int j) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object o = null;
                for (int i = 0; i <= j; i++) {
                    o = ois.readObject();
                }
                return o;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Value value(int j) {
       return (Value) readObject(j);
    }

    @Override
    public Node<Key, Value> next(int j) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEntry(int i, Entry<Key, Value> entry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void insert(int j, Entry<Key, Value> t) {
        // TODO Auto-generated method stub

    }

    @Override
    public Entry<Key, Value> entry(int i) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int numEntries() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isFull() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Node<Key, Value> split() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Node<Key, Value> makeParentWith(Node<Key, Value> u) {
        // TODO Auto-generated method stub
        return null;
    }

}
