package entities;

import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import fx.*;
import main.GraphicsHandler;

public class Entity {

    public static final int TINY = -2;
    public static final int SMALL = -1;
    public static final int NORMAL = 0;
    public static final int LARGE = 1;
    public static final int HUGE = 2;
    public static final int GARGANTUAN = 3;

    private final GraphicsHandler gh;
    private Image image;
    private Hexagon tile;
    private int size;
    protected ArrayList<Path2D> hexTile = new ArrayList<>();
    private Point2D location;
    private Marker debugMarker;
    public Entity(GraphicsHandler gh, Image image, Hexagon tile, Point2D location, int size)
    {
        this.gh = gh;
        this.image = image;
        this.tile = tile;
        this.location = location;
        this.size = size;
        this.debugMarker = new Marker(tile.getGridPoint(), Marker.COORDINATES, true);
        gh.addMarker(debugMarker);
    }

    public Image getImage() {
        return image;
    }
    public int getSize() {
        return size;
    }
    public double getDrawSize() {
        return switch (size) {
            case TINY -> 1;
            case SMALL -> 1.5;
            case NORMAL -> Math.sqrt(3);
            case LARGE -> 3/4;
            case HUGE -> 2;
            case GARGANTUAN -> 2;
            default -> 0;
        };
    }
    public void debugUpdate() {
        debugMarker.moveTo(location, tile.getGridPoint());
    }
    public void setImage(Image image) {
        this.image = image;
    }
    public Hexagon getTile() {
        return tile;
    }
    public void setTile(Hexagon tile) {
        this.tile = tile;
    }
    public Point2D getLocation() {
        if (tile != null) location = tile.getCenter();
        return location;
    }
    public void setLocation(Point2D p) {
        this.location = p;
    }
}
