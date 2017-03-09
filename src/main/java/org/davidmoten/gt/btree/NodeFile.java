package org.davidmoten.gt.btree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

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
                fis.skip(position + 8);
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
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.skip(position);
            byte[] a = new byte[8];
            fis.read(a);
            ByteBuffer b = ByteBuffer.wrap(a);
            b.position(0);
            int nextFileNo = b.getInt();
            int nextPosition = b.getInt();
            File nextFile = new File("target/" + nextFileNo + ".db");
            return new NodeFile<Key, Value>(nextFile, nextPosition);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
