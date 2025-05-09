package fx;

import java.awt.Point;

public class Marker {
    public static final int COORDINATES = 0;
    public static final int STAT = 1;
    public static final int DICE = 2;
    public static final int DICERESULT = 3;
    private Point coords;
    private final int purpose;
    private int stat;
    private boolean isDebugMarker;

    public Marker(Point coords, int purpose, boolean isDebugMarker) {
        this.coords = coords;
        this.purpose = purpose;
        this.isDebugMarker = isDebugMarker;
    }
    public Marker(int stat, Point coords, int purpose, boolean isDebugMarker) {
        this.stat = stat;
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
        return switch (purpose) {
            case 0 -> "[" + coords.x + "|" + coords.y + "]";
            case 1, 2, 3 -> Integer.toString(stat);
            default -> "";
        };
    }
    public Object getRawContent() {
        return switch (purpose) {
            case 0 -> coords;
            case 1 -> stat;
            default -> null;
        };
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
