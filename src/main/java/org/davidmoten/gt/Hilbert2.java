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

        long[] X = Arrays.copyOf(transposedIndex, transposedIndex.length);
        int n = X.length; // n: Number of dimensions
        long N = 2L << (bits - 1), P, Q, t;
        int i;
        // Gray decode by H ^ (H/2)
        t = X[n - 1] >> 1;
        // Corrected error in Skilling's paper on the following line. The
        // appendix had i >= 0 leading to negative array index.
        for (i = n - 1; i > 0; i--)
            X[i] ^= X[i - 1];
        X[0] ^= t;
        // Undo excess work
        for (Q = 2; Q != N; Q <<= 1) {
            P = Q - 1;
            for (i = n - 1; i >= 0; i--)
                if ((X[i] & Q) != 0L)
                    X[0] ^= P; // invert
                else {
                    t = (X[0] ^ X[i]) & P;
                    X[0] ^= t;
                    X[i] ^= t;
                }
        } // exchange
        return X;
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
        long[] X = Arrays.copyOf(point, point.length);
        int n = point.length; // n: Number of dimensions
        long M = 1L << (bits - 1), P, Q, t;
        int i;
        // Inverse undo
        for (Q = M; Q > 1; Q >>= 1) {
            P = Q - 1;
            for (i = 0; i < n; i++)
                if ((X[i] & Q) != 0)
                    X[0] ^= P; // invert
                else {
                    t = (X[0] ^ X[i]) & P;
                    X[0] ^= t;
                    X[i] ^= t;
                }
        } // exchange
          // Gray encode
        for (i = 1; i < n; i++)
            X[i] ^= X[i - 1];
        t = 0;
        for (Q = M; Q > 1; Q >>= 1)
            if ((X[n - 1] & Q) != 0)
                t ^= Q - 1;
        for (i = 0; i < n; i++)
            X[i] ^= t;

        return X;
    }

    private static BigInteger index(long[] transposedIndex, int bits) {
        BitSet b = new BitSet(transposedIndex.length * bits);
        int bi = 0;
        int mask = 1;
        for (int i = 0; i < bits; i++) {
            for (int j = transposedIndex.length - 1; j >= 0; j--) {
                b.set(bi, (transposedIndex[j] & mask) != 0);
                bi++;
            }
            mask <<= 1;
        }
        if (b.isEmpty())
            return BigInteger.ZERO;
        else
            return new BigInteger(b.toByteArray());
    }

    public static void main(String[] args) {
        long[] p = { 1, 1 };
        int bits = 2;
        long[] ti = transposedIndex(p, bits);
        System.out.println(Arrays.toString(ti));
        System.out.println(index(ti, bits));
    }
}
