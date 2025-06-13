package tools;

import calc.Calc;
import fx.Hexagon;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

/**
* Area tool for calculating and visualizing circular areas on a hexagonal grid
* Used for area-of-effect calculations, spell ranges, or measurement tools
*/
public class Area {
   /** Reference to the main graphics handler */
   private final GraphicsHandler gh;
   
   /** Grid coordinates of the area's center point */
   private final Point origin;
   
   /** World coordinates of the area's center point */
   private Point2D originPoint;
   
   /** Current radius of the area in pixels */
   private double radius;

   /**
    * Constructor - creates a new area centered on the current tile under mouse
    * 
    * @param gh Graphics handler containing the hexagonal grid and mouse state
    */
   public Area(GraphicsHandler gh) {
       this.gh = gh;
       // Set origin to the current tile under the mouse cursor
       this.origin = gh.tileUnderMouse.getGridPoint();
       // Get the world coordinates of the origin tile's center
       originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
   }

   /**
    * Calculate and return the current radius of the area
    * Radius is determined by either mouse position (if Ctrl+mouse active) or current tile
    * 
    * @return Current radius in pixels
    */
   public double getRadius() {
       // Return cached radius if no tile under mouse
       if(gh.tileUnderMouse == null) return radius;
       
       // Update origin point coordinates
       originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
       
       // Calculate radius based on input method:
       // - If Ctrl+mouse active: use exact mouse position for precise measurement
       // - Otherwise: snap to center of tile under mouse for grid-aligned measurement
       radius = gh.io.isCtrlDown && gh.io.isMouseActive()
           ? Calc.distance(gh.io.mousePos, originPoint)
           : Calc.distance(gh.tileUnderMouse.getCenter(), originPoint);
       
       return radius;
   }
   
   /**
    * Convert the current radius from pixels to tile units
    * Accounts for hexagonal tile geometry
    * 
    * @return Radius expressed in number of tiles
    */
   public double getRadiusInTiles() {
       // Convert pixel radius to tile radius using hexagon geometry
       // Factor includes hexagon size and sqrt(3) for proper hexagonal spacing
       return (getRadius() + 1 / (gh.hexSize * Math.sqrt(3)));
   }
   
   /**
    * Get the grid coordinates of the area's origin point
    * 
    * @return Point containing x,y grid coordinates of the center
    */
   public Point getOrigin() {
       return origin;
   }
   
   /**
    * Find all hexagons within the current area radius
    * Updates the visual attack tiles and triggers a repaint
    * 
    * @return List of all hexagons within the area of effect
    */
   public ArrayList<Hexagon> getAttackedCharacters() {
       ArrayList<Hexagon> attacked = new ArrayList<>();
       
       // Clear previous attack visualization
       gh.attackTiles.clear();
       
       // Update current radius
       getRadius();
       
       // Check each hexagon in the grid
       for(Hexagon h : gh.hexlist.values()) {
           // Include hexagon if its center is within radius (+1 pixel tolerance)
           if (radius + 1 > Calc.distance(h.getCenter(), originPoint)) {
               attacked.add(h);           // Add to result list
               gh.attackTiles.add(h);     // Add to visual highlight list
           }
       }
       
       // Trigger visual update to show affected area
       gh.repaint();
       return attacked;
   }
}