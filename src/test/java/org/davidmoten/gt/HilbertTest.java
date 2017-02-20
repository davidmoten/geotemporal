package org.davidmoten.gt;

import java.util.Arrays;

import org.junit.Test;

public class HilbertTest {

    @Test
    public void test() {
        long[] axes = { 0, 1 };
        long[] ti = Hilbert.transposedIndex(axes, 2);
        System.out.println(Arrays.toString(ti));
        System.out.println(Hilbert.untranspose(ti, 2, false));
    }

}
