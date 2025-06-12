package entities;

import fx.*;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

public abstract class Entity {

    protected Image   image;
    protected Hexagon tile;
    protected Point2D location;
    
    protected ArrayList<Hexagon> occupiedTiles = new ArrayList<>();
    protected ArrayList<Path2D> hexTile = new ArrayList<>();

    protected final Marker debugMarker;
    protected GraphicsHandler gh;

    public Entity(GraphicsHandler gh, Image image, Hexagon tile, Point2D location)
    {
        this.image    = image;
        this.tile     = tile;
        this.location = location;
        this.gh = gh;
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
    public Marker getMarker() {
        return debugMarker;
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
    public ArrayList<Hexagon> getOccupiedTiles() {
        return occupiedTiles;
    }
    public void setOccupiedTiles(ArrayList<Hexagon> occupiedTiles) {
        this.occupiedTiles = occupiedTiles;
    }
    public Point2D getLocation() {
        return location;
    }
    public void setLocation(Point2D p) {
        this.location = p;
    }

    // Placeholder methods for character attributes -------------------

    public int getSize() {
        return 0;
    }
    public int getSpeed() {
        return 0;
    }
    public int getInitiative() {
        return 0;
    }
}
