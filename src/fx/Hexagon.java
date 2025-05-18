package fx;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class Hexagon {

    private Path2D shape;
    private Point2D center;
    private final Point gridCoords;

    public Hexagon (Point2D center, int hexSize, Point gridCoords) {

        this.center = center;
        this.shape  = new Path2D.Double();

        for (int i = 0; i < 6; i++) {

            double angle = 2 * Math.PI / 6 * i;

            double x = center.getX() + hexSize * Math.cos(angle);
            double y = center.getY() + hexSize * Math.sin(angle);

            if (i == 0) {
                shape.moveTo(x, y);
            }    else   {
                shape.lineTo(x, y);
            }
        }
        shape.closePath();
        this.gridCoords = gridCoords;
    }

    public boolean contains(Point2D p) {
        return shape.contains(p);
    }
    public Path2D getShape() {
        return shape;
    }
    public Point2D getCenter() {
        return center;
    }
    public Point getGridPoint() {
        return gridCoords;
    }
    public void setShape(Path2D shape) {
        this.shape = shape;
    }
    public void setCenter(Point2D center) {
        this.center = center;
    }
}
