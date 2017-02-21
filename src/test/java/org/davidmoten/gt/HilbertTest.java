package org.davidmoten.gt;

import static org.junit.Assert.assertEquals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.junit.Test;

public class HilbertTest {

    @Test
    public void test() throws IOException {
        // TODO works for bits <= 4
        int bits = 5;
        Map<BigInteger, long[]> map = new TreeMap<>();
        final long N = 1L << bits;
        for (long i = 0; i < N; i++) {
            for (long j = 0; j < N; j++) {
                long[] point = { i, j };
                BigInteger n = Hilbert.toIndex(point, bits, false);
                map.put(n, point);
            }
        }
        for (BigInteger n : map.keySet()) {
            System.out.println(n + "\t" + Arrays.toString(map.get(n)));
        }

        int width = 600;
        int height = width;
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setPaint(Color.black);
        g.setStroke(new BasicStroke(2f));
        int margin = 10;
        int x = margin;
        int y = margin;
        for (BigInteger n : map.keySet()) {
            long[] point = map.get(n);
            int x2 = (int) Math.round((double) point[0] / (N - 1) * (width - 2 * margin) + margin);
            int y2 = (int) Math.round((double) point[1] / (N - 1) * (height - 2 * margin) + margin);
            g.drawLine(x, y, x2, y2);
            x = x2;
            y = y2;
        }
        ImageIO.write(b, "PNG", new File("target/image.png"));
    }

    @Test
    public void testWeird() {
        assertEquals(6, Hilbert.toIndex(new long[] { 0, 16 }, 5, true).intValue());
    }


}
