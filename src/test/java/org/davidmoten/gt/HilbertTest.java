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
        int bits = 2;
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

    public static void main(String[] args) throws IOException {
        int bits = 6;
        int n = 1 << bits;
        for (int i = 0; i < n*n; i++) {
            int[] point = { 0, 0 };
            d2xy(n, i, point);
            System.out.println(i + "\t" + Arrays.toString(point));
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
        for (int i=0;i<n*n;i++) {
            int[] point = new int[2];
            d2xy(n, i, point);
            int x2 = (int) Math.round((double) point[0] / (n - 1) * (width - 2 * margin) + margin);
            int y2 = (int) Math.round((double) point[1] / (n - 1) * (height - 2 * margin) + margin);
            g.drawLine(x, y, x2, y2);
            x = x2;
            y = y2;
        }
        ImageIO.write(b, "PNG", new File("target/image.png"));
    }
    
    private static int xy2d(int n, int[] point) {
        int rx, ry, s, d = 0;
        for (s = n / 2; s > 0; s /= 2) {
            rx = (point[0] & s) > 0 ? 1 : 0;
            ry = (point[1] & s) > 0 ? 1 : 0;
            d += s * s * ((3 * rx) ^ ry);
            rot(s, point, rx, ry);
        }
        return d;
    }

    // convert d to (x,y)
    private static int[] d2xy(int n, int d, int[] point) {
        int rx, ry, s, t = d;
        point[0] = 0;
        point[1] = 0;
        for (s = 1; s < n; s *= 2) {
            rx = 1 & (t / 2);
            ry = 1 & (t ^ rx);
            rot(s, point, rx, ry);
            point[0] += s * rx;
            point[1] += s * ry;
            t /= 4;
        }
        return point;
    }

    // rotate/flip a quadrant appropriately
    private static void rot(int n, int[] point, int rx, int ry) {
        if (ry == 0) {
            if (rx == 1) {
                point[0] = n - 1 - point[0];
                point[1] = n - 1 - point[1];
            }

            // Swap x and y
            int t = point[0];
            point[0] = point[1];
            point[1] = t;
        }
    }
}
