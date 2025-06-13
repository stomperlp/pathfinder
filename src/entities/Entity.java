package entities;

import fx.*;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

/**
 * Abstract base class representing a game entity with visual representation and position.
 * Provides core functionality for all game entities with tile-based positioning.
 */
public abstract class Entity implements Cloneable {

    // Visual representation of the entity
    protected Image   image;
    // Current hexagon tile the entity occupies
    protected Hexagon tile;
    // Precise pixel location within the tile
    protected Point2D location;
    
    // List of all hexagon tiles occupied by this entity
    protected ArrayList<Hexagon> occupiedTiles = new ArrayList<>();
    // List of geometric shapes representing the entity's occupied tiles
    protected ArrayList<Path2D> hexTile = new ArrayList<>();

    // Debug marker for displaying coordinates
    protected final Marker debugMarker;
    // Reference to the graphics handler
    protected GraphicsHandler gh;

    /**
     * Constructs a new Entity instance.
     * @param gh GraphicsHandler for rendering
     * @param image Visual representation
     * @param tile Starting hexagon tile
     * @param location Pixel position within the tile
     */
    public Entity(GraphicsHandler gh, Image image, Hexagon tile, Point2D location)
    {
        this.image    = image;
        this.tile     = tile;
        this.location = location;
        this.gh = gh;
        // Create debug marker showing coordinates
        this.debugMarker = new Marker(tile.getGridPoint(), Marker.COORDINATES, true);
        gh.addMarker(debugMarker);
    }

    // Basic accessor methods ----------------------------------------

    public Image getImage() {
        return image;
    }

    /**
     * Gets the size multiplier for rendering this entity.
     * @return Default size multiplier (override in subclasses)
     */
    public double getDrawSize() {
        return Math.sqrt(3);
    }

    /**
     * Updates the debug marker position to match entity's current location.
     */
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

    /**
     * Gets all hexagon tiles occupied by this entity.
     * @return List of occupied tiles
     */
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

    /**
     * Creates a shallow copy of this entity.
     * @return Cloned entity
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    public Entity clone() throws CloneNotSupportedException {
        return (Entity) super.clone();
    }

    // Abstract methods to be implemented by subclasses ---------------

    /**
     * Gets the size category of the entity.
     * @return Size constant (0 by default)
     */
    public int getSize() {
        return 0;
    }

    /**
     * Gets the movement speed of the entity.
     * @return Speed value (0 by default)
     */
    public int getSpeed() {
        return 0;
    }

    /**
     * Gets the initiative value for turn order.
     * @return Initiative value (0 by default)
     */
    public int getInitiative() {
        return 0;
    }
}