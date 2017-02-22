package org.davidmoten.gt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class HilbertCurveRenderer {
    
    public static void main(String[] args) throws IOException {
        int bits = 5;
        HilbertCurve c = HilbertCurve.bits(bits).dimensions(2);
        int n = 1 << bits;
        for (int i = 0; i < n * n; i++) {
            long[] point = { 0, 0 };
//            d2xy(n, i, point);
            System.out.println(i + "\t" + Arrays.toString(point));
        }
        int width = 800;
        int height = width;
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setPaint(Color.black);
        g.setStroke(new BasicStroke(0.5f));
        int margin = 10;
        int x = margin;
        int y = margin;
        for (long i = 0; i < n * n; i++) {
            long[] point = new long[2];
//            d2xy(n, i, point);
            int x2 = (int) Math.round((double) point[0] / (n - 1) * (width - 2 * margin) + margin);
            int y2 = (int) Math.round((double) point[1] / (n - 1) * (height - 2 * margin) + margin);
            g.drawLine(x, y, x2, y2);
            x = x2;
            y = y2;
        }
        ImageIO.write(b, "PNG", new File("target/image.png"));
    }
}
