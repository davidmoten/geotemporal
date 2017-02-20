package org.davidmoten.gt;

import java.math.BigInteger;

public final class Hilbert {

    private Hilbert() {
        // prevent instantiation
    }

    /**
     * Convert the Hilbert index into an N-dimensional point expressed as a
     * vector of uints.
     *
     * Note: In Skilling's paper, this function is named TransposetoAxes.
     * 
     * @param transposedIndex
     *            The Hilbert index stored in transposed form.
     * @param bits
     *            Number of bits per coordinate.
     * @return Point in N-space.
     */
    public static long[] hilbertAxes(final long[] transposedIndex, final int bits) {
        final long[] result = transposedIndex.clone();
        final int dims = result.length;
        grayDecode(result, dims);
        undoExcessWork(result, dims, bits);
        return result;
    }

    private static void grayDecode(final long[] result, final int dims) {
        final long swap = result[dims - 1] >>> 1;
        // Corrected error in Skilling's paper on the following line. The
        // appendix had i >= 0 leading to negative array index.
        for (int i = dims - 1; i > 0; i--)
            result[i] ^= result[i - 1];
        result[0] ^= swap;
    }

    private static void undoExcessWork(final long[] result, final int dims, final int bits) {
        for (long bit = 2, n = 1; n != bits; bit <<= 1, ++n) {
            final long mask = bit - 1;
            for (int i = dims - 1; i >= 0; i--)
                if ((result[i] & bit) != 0)
                    result[0] ^= mask; // invert
                else
                    swapBits(result, mask, i);
        }
    }

    /**
     * Given the axes (coordinates) of a point in N-Dimensional space, find the
     * distance to that point along the Hilbert curve. That distance will be
     * transposed; broken into pieces and distributed into an array.
     *
     * The number of dimensions is the length of the hilbertAxes array.
     *
     * Note: In Skilling's paper, this function is called AxestoTranspose.
     * 
     * @param hilbertAxes
     *            Point in N-space.
     * @param bits
     *            Depth of the Hilbert curve. If bits is one, this is the
     *            top-level Hilbert curve.
     * @return The Hilbert distance (or index) as a transposed Hilbert index.
     */
    public static long[] HilbertIndexTransposed(final long[] hilbertAxes, final int bits) {
        final long[] result = hilbertAxes.clone();
        final int dims = hilbertAxes.length;
        final long maxBit = 1L << (bits - 1);
        inverseUndo(result, dims, maxBit);
        grayEncode(result, dims, maxBit);
        return result;
    }

    public static BigInteger toBigInteger(long[] transposedIndex) {
        // TODO
        return null;

    }

    private static void inverseUndo(final long[] result, final int dims, final long maxBit) {
        for (long bit = maxBit; bit != 0; bit >>>= 1) {
            final long mask = bit - 1;
            for (int i = 0; i < dims; i++)
                if ((result[i] & bit) != 0)
                    result[0] ^= mask; // invert
                else
                    swapBits(result, mask, i);
        } // exchange
    }

    private static void grayEncode(final long[] result, final int dims, final long maxBit) {
        for (int i = 1; i < dims; i++)
            result[i] ^= result[i - 1];
        long mask = 0;
        for (long bit = maxBit; bit != 0; bit >>>= 1)
            if ((result[dims - 1] & bit) != 0)
                mask ^= bit - 1;
        for (int i = 0; i < dims; i++)
            result[i] ^= mask;
    }

    private static void swapBits(final long[] array, final long mask, final int index) {
        final long swap = (array[0] ^ array[index]) & mask;
        array[0] ^= swap;
        array[index] ^= swap;
    }

}
