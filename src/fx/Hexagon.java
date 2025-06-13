package fx;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Represents a hexagonal tile in a grid system with both pixel and grid coordinates.
 * Handles geometric properties and contains-check functionality for hexagon shapes.
 */
public class Hexagon {

    // Geometric shape of the hexagon
    private Path2D shape;
    // Center point in pixel coordinates
    private Point2D center;
    // Grid coordinates (column, row)
    private final Point gridCoords;

    /**
     * Constructs a hexagonal tile with specified parameters.
     * @param center The center point in pixel coordinates
     * @param hexSize The radius/distance from center to vertices
     * @param gridCoords The grid position coordinates
     * @param isFlat Whether the hexagon is flat-topped (true) or pointy-topped (false)
     */
    public Hexagon(Point2D center, int hexSize, Point gridCoords, boolean isFlat) {
        this.center = center;
        this.shape = new Path2D.Double();

        // Draw regular hexagon by calculating 6 vertices
        for (int i = 0; i < 6; i++) {
            // Calculate angle for each vertex
            double angle = 2 * Math.PI / 6 * i;
            
            // Apply rotation offset for pointy-topped hexagons
            double x = center.getX() + hexSize * Math.cos(angle + (isFlat ? 0 : Math.PI / 6));
            double y = center.getY() + hexSize * Math.sin(angle + (isFlat ? 0 : Math.PI / 6));

            // Build the path by moving to first point and lining to others
            if (i == 0) {
                shape.moveTo(x, y);
            } else {
                shape.lineTo(x, y);
            }
        }
        shape.closePath();
        this.gridCoords = gridCoords;
    }

    /**
     * Compares hexagons based on their grid coordinates.
     * @param o The object to compare with
     * @return true if the other object is a Hexagon with same grid coordinates
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return ((Hexagon) o).getGridPoint().equals(this.getGridPoint());
    }

    /**
     * Checks if a point is contained within this hexagon.
     * @param p The point to check
     * @return true if the point is inside the hexagon's shape
     */
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }

    // Standard accessor methods with no modifications
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

    /**
     * Sets a new center point and updates the shape position.
     * @param center The new center point coordinates
     */
    public void setCenter(Point2D center) {
        this.center = center;
        shape.moveTo(center.getX(), center.getY());
    }
}