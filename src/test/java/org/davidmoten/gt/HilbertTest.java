package org.davidmoten.gt;

import java.util.Arrays;

import org.junit.Test;

public class HilbertTest {

    @Test
    public void test() {
        int bits = 3;
        for (long i=0;i< Math.pow(2, bits);i++) {
            for (long j=0;j< Math.pow(2, bits);j++) {
                long[] point= {i,j};
                long[] ti = Hilbert.transposedIndex(point, bits);
                System.out.println(Arrays.toString(ti));
                System.out.println(Hilbert.untranspose(ti, bits, false));
            }   
        }
        
        for (long i=0;i<Math.pow(2, bits);i++) {
            System.out.println(i + "\t" + Arrays.toString(Hilbert.transposedIndex(point, bits)));
        }
    }

}
