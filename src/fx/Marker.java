package fx;

import java.awt.Point;

public class Marker {
    public static final int COORDINATES = 0;
    public static final int STATS = 1;
    private Point coords;
    private final int purpose;
    private int stat;
    private boolean isDebugMarker;

    public Marker(Point coords, int purpose, boolean isDebugMarker) {
        this.coords = coords;
        this.purpose = purpose;
        this.isDebugMarker = isDebugMarker;
    }

    public void moveTo(Point p) {
        coords = p;
    }
    public void moveBy(Point p) {
        coords.translate(p.x, p.y);
    }
    public String getText() {
        switch (purpose) {
            case 0: return "[" + coords.x + "|" + coords.y + "]";
            case 1: return Integer.toString(stat); 
            default: return "";
        }
    }
    public void setStat(int stat) {
        this.stat = stat;
    }
    public Point getPoint() {
        return coords;
    }
    public boolean isDebugMarker() {
        return isDebugMarker;
    }
    public int getPurpose() {
        return purpose;
    }
}
