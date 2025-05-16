package entities;

import fx.*;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

public class Entity {

    private Image   image;
    private Hexagon tile;
    private Point2D location;
    
    protected ArrayList<Path2D> hexTile = new ArrayList<>();
    private final Marker debugMarker;

    public Entity(GraphicsHandler gh, Image image, Hexagon tile, Point2D location)
    {
        this.image       = image;
        this.tile        = tile;
        this.location    = location;
        this.debugMarker = new Marker(tile.getGridPoint(), Marker.COORDINATES, true);
        gh.addMarker(debugMarker);
    }

    public Image getImage() {
        return image;
    }
    public double getDrawSize() {
        return Math.sqrt(3);
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
