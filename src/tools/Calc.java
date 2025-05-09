package tools;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class Calc {
    public static Point calcCenter(Path2D hex) {
        double left = hex.getCurrentPoint().getX();
        double right = left;
        double x;
        double y = 0;
        for (PathIterator i = hex.getPathIterator(null); !i.isDone(); i.next()) {
            Point2D p = hex.getCurrentPoint();

            if (p.getX() < left) {
                left = p.getX();
                y = p.getY();
            }
            if (p.getX() > right)
                right = p.getX();
        }

        x = left + (left - right)/2;

        return new Point((int)x,(int)y);

    }
}
