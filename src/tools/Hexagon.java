package tools;
import java.awt.Point;
import java.awt.Polygon;

public class Hexagon extends Polygon{
    public static Point calcCenter(Polygon p) {
        int left = p.xpoints[0];
        int right = p.xpoints[0];
        int x;
        int y = 0;

        for (int i = 0; i < p.npoints; i++) {
            if (p.xpoints[i] < left) {
                left = p.xpoints[i];
                y = p.ypoints[i];
            }
            if (p.xpoints[i] > right)
                right = p.xpoints[i];
        }

        x = left + (left - right)/2;

        return new Point(x,y);

    }
}
