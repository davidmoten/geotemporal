package org.davidmoten.gt.btree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Comparator;

import org.junit.Test;

public class BTreeTest {

    @Test
    public void testGet() {
        assertEquals("three", createTree().get(3));
    }

    @Test
    public void testGetNotFound() {
        assertNull(createTree().get(0));
    }

    private static BTree<Integer, String> createTree() {
        BTree<Integer, String> t = new BTree<Integer, String>(Comparator.naturalOrder());
        t.put(1, "one");
        t.put(2, "two");
        t.put(3, "three");
        t.put(4, "four");
        t.put(5, "five");
        t.put(6, "six");
        t.put(7, "seven");
        t.put(8, "eight");
        t.put(9, "nine");
        t.put(10, "ten");
        return t;
    }

}
