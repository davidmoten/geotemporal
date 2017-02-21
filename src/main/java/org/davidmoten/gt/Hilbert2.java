package org.davidmoten.gt;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

/// <summary>
/// Convert between Hilbert index and N-dimensional points.
/// 
/// The Hilbert index is expressed as an array of transposed bits. 
/// 
/// Example: 5 bits for each of n=3 coordinates.
/// 15-bit Hilbert integer = A B C D E F G H I J K L M N O is stored
/// as its Transpose                        ^
/// X[0] = A D G J M                    X[2]|  7
/// X[1] = B E H K N        <------->       | /X[1]
/// X[2] = C F I L O                   axes |/
///        high low                         0------> X[0]
///        
/// NOTE: This algorithm is derived from work done by John Skilling and published in "Programming the Hilbert curve".
/// (c) 2004 American Institute of Physics.
///
public class Hilbert2 {
    /// </summary>
    /// <summary>
    /// Convert the Hilbert index into an N-dimensional point expressed as a
    /// vector of uints.
    ///
    /// Note: In Skilling's paper, this function is named TransposetoAxes.
    /// </summary>
    /// <param name="transposedIndex">The Hilbert index stored in transposed
    /// form.</param>
    /// <param name="bits">Number of bits per coordinate.</param>
    /// <returns>Coordinate vector.</returns>
    public static long[] point(long[] transposedIndex, int bits) {

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

    /// <summary>
    /// Given the axes (coordinates) of a point in N-Dimensional space, find the
    /// distance to that point along the Hilbert curve.
    /// That distance will be transposed; broken into pieces and distributed
    /// into an array.
    ///
    /// The number of dimensions is the length of the hilbertAxes array.
    ///
    /// Note: In Skilling's paper, this function is called AxestoTranspose.
    /// </summary>
    /// <param name="hilbertAxes">Point in N-space.</param>
    /// <param name="bits">Depth of the Hilbert curve. If bits is one, this is
    /// the top-level Hilbert curve.</param>
    /// <returns>The Hilbert distance (or index) as a transposed Hilbert
    /// index.</returns>
    public static long[] transposedIndex(long[] point, int bits) {
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

    private static BigInteger index(long[] transposedIndex, int bits) {
        BitSet b = new BitSet(transposedIndex.length * bits);
        int bIndex = 0;
        long mask = 1;
        for (int i = 0; i < bits; i++) {
            for (int j = transposedIndex.length - 1; j >= 0; j--) {
                b.set(bIndex, (transposedIndex[j] & mask) != 0);
                bIndex++;
            }
            mask <<= 1;
        }
        if (b.isEmpty())
            return BigInteger.ZERO;
        else
            return new BigInteger(1, b.toByteArray());
    }

    public static void main(String[] args) {
        int bits = 5;
        int n = 2 << (bits - 1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long[] q = { i, j };
                System.out.print(index(transposedIndex(q, bits), bits));
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}
