package fx;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Marker {
    public static final int COORDINATES = 0;
    public static final int STAT        = 1;
    public static final int DICE        = 2;
    public static final int DICERESULT  = 3;
    public static final int TILE        = 4;
    private Point2D coords;
    private Point2D displayCoords;
    private final int purpose;
    private int stat;
    private final boolean isDebugMarker;
    private Color color;

    public Marker(Point2D location, int purpose, boolean isDebugMarker) {
        this.coords         = location;
        this.purpose        = purpose;
        this.isDebugMarker  = isDebugMarker;
    }
    public Marker(int stat, Point coords, int purpose, boolean isDebugMarker) {
        this.stat          = stat;
        this.coords        = coords;
        this.purpose       = purpose;
        this.isDebugMarker = isDebugMarker;
    }

    public void moveTo(Point2D p) {
        coords = p;
        displayCoords = p;
    }
    public void moveTo(Point2D p, Point2D dp) {
        coords = p;
        displayCoords = dp;
    }
    public void moveBy(Point2D p) {
        coords.setLocation(coords.getX()+p.getX(), coords.getY()+ p.getY());
    }
    public String getText() {
        return switch (purpose) {
            case 0       -> "[" + (int) displayCoords.getX() + "|" + (int) displayCoords.getY() + "]";
            case 1, 2, 3 -> Integer.toString(stat);
            default      -> "";
        };
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public Object getRawContent() {
        return switch (purpose) {
            case 0  -> coords;
            case 1  -> stat;
            default -> null;
        };
    }
    public void setStat(int stat) {
        this.stat = stat;
    }
    public Point2D getPoint() {
        return coords;
    }
    public boolean isDebugMarker() {
        return isDebugMarker;
    }
    public int getPurpose() {
        return purpose;
    }
}
