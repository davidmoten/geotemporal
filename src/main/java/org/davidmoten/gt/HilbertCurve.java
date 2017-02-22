package org.davidmoten.gt;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Converts between Hilbert index (transposed and {@code BigInteger}) and
 * N-dimensional points.
 * 
 * The Hilbert index is expressed as an array of transposed bits.
 * 
 * <pre>
  Example: 5 bits for each of n=3 coordinates.
     15-bit Hilbert integer = A B C D E F G H I J K L M N O is stored
     as its Transpose                        ^
     X[0] = A D G J M                    X[2]|  7
     X[1] = B E H K N        <------->       | /X[1]
     X[2] = C F I L O                   axes |/
            high low                         0------> X[0]
 * </pre>
 * 
 * <p>
 * Note: This algorithm is derived from work done by John Skilling and published
 * in "Programming the Hilbert curve". (c) 2004 American Institute of Physics.
 */
public final class HilbertCurve {

    private final int bits;

    private HilbertCurve(int bits) {
        this.bits = bits;
    }

    /**
     * Returns an instance for performing transformations for a Hilbert curve
     * with the given number of bits.
     * 
     * @param bits
     *            depth of the Hilbert curve. If bits is one, this is the
     *            top-level Hilbert curve
     * @return object to do transformations with the Hilbert Curve
     */
    public static HilbertCurve createWithBits(int bits) {
        return new HilbertCurve(bits);
    }

    /**
     * Converts the Hilbert transposed index into an N-dimensional point
     * expressed as a vector of {@code long}.
     * 
     * In Skilling's paper this function is named {@code TransposeToAxes}
     * 
     * @param transposedIndex
     * @return the coordinates of the point represented by the transposed index
     *         on the Hilbert curve
     */
    public long[] point(long... transposedIndex) {
        return point(bits, transposedIndex);
    }

    static long[] point(int bits, long... transposedIndex) {

        final long N = 2L << (bits - 1);
        long[] x = Arrays.copyOf(transposedIndex, transposedIndex.length);
        int n = x.length; // n: Number of dimensions
        long p, q, t;
        int i;
        // Gray decode by H ^ (H/2)
        t = x[n - 1] >> 1;
        // Corrected error in Skilling's paper on the following line. The
        // appendix had i >= 0 leading to negative array index.
        for (i = n - 1; i > 0; i--)
            x[i] ^= x[i - 1];
        x[0] ^= t;
        // Undo excess work
        for (q = 2; q != N; q <<= 1) {
            p = q - 1;
            for (i = n - 1; i >= 0; i--)
                if ((x[i] & q) != 0L)
                    x[0] ^= p; // invert
                else {
                    t = (x[0] ^ x[i]) & p;
                    x[0] ^= t;
                    x[i] ^= t;
                }
        } // exchange
        return x;
    }

    /**
     * <p>
     * Given the axes (coordinates) of a point in N-Dimensional space, find the
     * distance to that point along the Hilbert curve. That distance will be
     * transposed; broken into pieces and distributed into an array.
     *
     * <p>
     * The number of dimensions is the length of the hilbertAxes array.
     *
     * <p>
     * Note: In Skilling's paper, this function is called AxestoTranspose.
     * 
     * @param point
     *            Point in N-space
     * @return The Hilbert distance (or index) as a transposed Hilbert index
     */
    public long[] transposedIndex(long... point) {
        return transposedIndex(bits, point);
    }

    static long[] transposedIndex(int bits, long... point) {
        final long M = 1L << (bits - 1);
        long[] x = Arrays.copyOf(point, point.length);
        int n = point.length; // n: Number of dimensions
        long p, q, t;
        int i;
        // Inverse undo
        for (q = M; q > 1; q >>= 1) {
            p = q - 1;
            for (i = 0; i < n; i++)
                if ((x[i] & q) != 0)
                    x[0] ^= p; // invert
                else {
                    t = (x[0] ^ x[i]) & p;
                    x[0] ^= t;
                    x[i] ^= t;
                }
        } // exchange
          // Gray encode
        for (i = 1; i < n; i++)
            x[i] ^= x[i - 1];
        t = 0;
        for (q = M; q > 1; q >>= 1)
            if ((x[n - 1] & q) != 0)
                t ^= q - 1;
        for (i = 0; i < n; i++)
            x[i] ^= t;

        return x;
    }

    public BigInteger index(long... point) {
        return index(bits, point);
    }

    static BigInteger index(int bits, long... point) {
        return toBigInteger(bits, transposedIndex(bits, point));
    }

    static BigInteger toBigInteger(int bits, long... transposedIndex) {
        int length = transposedIndex.length * bits;
        BitSet b = new BitSet(length);
        int bIndex = length - 1;
        long mask = 1L << (bits - 1);
        for (int i = 0; i < bits; i++) {
            for (int j = 0; j < transposedIndex.length; j++) {
                if ((transposedIndex[j] & mask) != 0) {
                    b.set(bIndex);
                }
                bIndex--;
            }
            mask >>= 1;
        }
        if (b.isEmpty())
            return BigInteger.ZERO;
        else {
            byte[] bytes = b.toByteArray();
            // make Big Endian
            reverse(bytes);
            return new BigInteger(1, bytes);
        }
    }

    // visible for testing
    static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

}