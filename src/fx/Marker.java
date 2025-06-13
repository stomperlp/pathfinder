package fx;

import entities.Entity;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import main.GraphicsHandler;

/**
 * Represents a visual marker that can display various types of information
 * (coordinates, stats, dice results) on the game grid.
 */
public class Marker {

    // Marker type constants
    public static final int COORDINATES = 0;  // Displays grid coordinates
    public static final int STAT        = 1;  // Displays numerical statistics
    public static final int DICE        = 2;  // Represents dice
    public static final int DICERESULT  = 3;  // Shows dice roll results
    public static final int TILE        = 4;  // Marks specific tiles
    
    // Shared graphics handler reference
    public static GraphicsHandler gh;

    // Marker properties
    private final boolean isDebugMarker;  // Whether this is a debug marker
    private final int purpose;           // The marker type (using above constants)
    private Point2D displayCoords;       // Coordinates for display purposes
    private Point2D coords;              // Actual coordinates
    private Color color;                 // Display color
    private double stat;                 // Numerical value being tracked
    private boolean isVisible = true;    // Visibility flag
    private String suffix = "";          // Additional text suffix
    private Entity attachedTo;           // Entity this marker is attached to
    private double debugValue;           // Additional debug information

    /**
     * Creates a marker at a specific location.
     * @param location The position coordinates
     * @param purpose The marker type (use class constants)
     * @param isDebugMarker Whether this is a debug marker
     */
    public Marker(Point2D location, int purpose, boolean isDebugMarker) {
        this.coords         = location;
        this.purpose        = purpose;
        this.isDebugMarker  = isDebugMarker;
    }

    /**
     * Creates a marker with a numerical value.
     * @param stat The numerical value to display
     * @param coords The position coordinates
     * @param purpose The marker type (use class constants)
     * @param isDebugMarker Whether this is a debug marker
     */
    public Marker(double stat, Point coords, int purpose, boolean isDebugMarker) {
        this.stat          = stat;
        this.coords        = coords;
        this.purpose       = purpose;
        this.isDebugMarker = isDebugMarker;
    }

    // Movement methods ----------------------------------------------

    /**
     * Moves marker to specified coordinates.
     * @param p New position coordinates
     */
    public void moveTo(Point2D p) {
        coords = p;
        displayCoords = p;
    }

    /**
     * Moves marker to specified coordinates with separate display coordinates.
     * @param p Actual position coordinates
     * @param dp Display position coordinates
     */
    public void moveTo(Point2D p, Point2D dp) {
        coords = p;
        displayCoords = dp;
    }

    /**
     * Moves marker by specified offset.
     * @param p Offset to move by
     */
    public void moveBy(Point2D p) {
        coords.setLocation(coords.getX()+p.getX(), coords.getY()+ p.getY());
    }

    // Display methods -----------------------------------------------

    /**
     * Gets the text to display for this marker.
     * @return Formatted text based on marker type
     */
    public String getText() {
        // Special debug display for stat markers attached to entities
        if (purpose == STAT && Marker.gh != null && Marker.gh.debugMode && attachedTo != null)
            return (int)stat + " [" + String.format("%.3f", debugValue) + "]" + suffix;

        // Standard display formats
        return switch (purpose) {
            case 0       -> "[" + (int) displayCoords.getX() + "|" + (int) displayCoords.getY() + "]";  // Coordinates
            case 1, 2, 3 -> Double.toString(stat) + " " + suffix;  // Stats/dice
            default      -> "";  // Empty for other types
        };
    }

    // Accessor methods (no modifications) ---------------------------

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

    public boolean isVisible() {
        return isVisible;
    }

    public void toggleVisible() {
        isVisible = !isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    // Entity attachment methods -------------------------------------

    /**
     * Updates marker position based on attached entity's position.
     */
    public void update() {
        if(attachedTo == null) return;
        
        Point coord = new Point((int) attachedTo.getOccupiedTiles().getFirst().getCenter().getX(),
                                (int) attachedTo.getOccupiedTiles().getFirst().getCenter().getY() - gh.hexSize);
        this.coords = coord;
    }

    public void attachTo(Entity e) {
        attachedTo = e;
    }

    public Entity getAttachedEntity() {
        return attachedTo;
    }

    public void setDebugValue(double value) {
        this.debugValue = value;
    }
}