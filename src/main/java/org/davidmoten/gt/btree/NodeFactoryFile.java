package org.davidmoten.gt.btree;

import java.io.File;
import java.io.Serializable;

public class NodeFactoryFile<Key extends Serializable, Value extends Serializable>
        implements NodeFactory<Key, Value> {

    int fileNo = 0;

    @Override
    public Node<Key, Value> create(int size) {
        fileNo++;
        return new NodeFile<>(new File("target/" + fileNo + ".db"), 0);
    }

}
