package org.davidmoten.gt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.BitSet;

import org.junit.Test;

public class HilbertTest {

    @Test
    public void test() {
        int bits = 5;
        long[] ti = Hilbert.transposedIndex(bits, 0, 16);
        for (long x : ti) {
            System.out.println(Long.toUnsignedString(x, 2));
        }
        System.out.println(Hilbert.index(bits, 0, 16));

    }

    @Test
    public void testIndex1() {
        assertEquals(7, Hilbert.index(2, 1, 2).intValue());
    }

    @Test
    public void testIndex2() {
        assertEquals(256, Hilbert.index(5, 0, 16).intValue());
    }

    @Test
    public void testToBigInteger() {
        long[] ti = { 0, 16 };
        assertEquals(256, Hilbert.toBigInteger(5, ti).intValue());
    }

    @Test
    public void testBitSet() {
        BitSet b = new BitSet(10);
        b.set(8);
        System.out.println(b);
        byte[] a = b.toByteArray();
        Hilbert.reverse(a);
        System.out.println(new BigInteger(1, a));
    }

    @Test
    public void testReverseOddNumberOfElements() {
        byte[] bytes = { 1, 2, 3, 4, 5 };
        Hilbert.reverse(bytes);
        assertArrayEquals(new byte[] { 5, 4, 3, 2, 1 }, bytes);
    }

    @Test
    public void testReverseEventNumberOfElements() {
        byte[] bytes = { 1, 2, 3, 4, 5, 6 };
        Hilbert.reverse(bytes);
        assertArrayEquals(new byte[] { 6, 5, 4, 3, 2, 1 }, bytes);
    }

    @Test
    public void test5() {
        int bits = 5;
        int n = 2 << (bits - 1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(Hilbert.index(bits, i, j));
                System.out.print("\t");
            }
            System.out.println();
        }
    }

}
