package tools;

import calc.Calc;
import fx.Hexagon;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

/**
* Cone tool for calculating and visualizing cone-shaped areas on a hexagonal grid
* Used for directional area-of-effect calculations like spell cones or weapon attacks
*/
public class Cone {
   /** Reference to the main graphics handler */
   private final GraphicsHandler gh;
   
   /** Grid coordinates of the cone's origin point */
   private final Point origin;
   
   /** World coordinates of the cone's origin point */
   private Point2D originPoint;
   
   /** Current radius/range of the cone in pixels */
   private double radius;
   
   /** Angular width of the cone in degrees */
   private double angle;
   
   /** Starting angle of the cone (center direction minus half the cone angle) */
   private double startAngle;

   /**
    * Constructor - creates a new cone centered on the current tile under mouse
    * 
    * @param gh Graphics handler containing the hexagonal grid and mouse state
    */
   public Cone(GraphicsHandler gh) {
       this.gh = gh;
       // Set origin to the current tile under the mouse cursor
       this.origin = gh.tileUnderMouse.getGridPoint();
       // Get the world coordinates of the origin tile's center
       originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
       // Default cone angle (slightly wider than 90° for better coverage)
       angle = 91;
   }

   /**
    * Calculate and return the current radius of the cone
    * Radius is determined by either mouse position (if Ctrl+mouse active) or current tile
    * 
    * @return Current radius in pixels
    */
   public double getRadius() {
       // Update origin point coordinates
       originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
       
       // Return cached radius if no tile under mouse
       if(gh.tileUnderMouse == null) return radius;
       
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
       return ((getRadius() + 1) / (gh.hexSize * Math.sqrt(3)));
   }

   /**
    * Get the grid coordinates of the cone's origin point
    * 
    * @return Point containing x,y grid coordinates of the center
    */
   public Point getOrigin() {
       return origin;
   }

   /**
    * Calculate the angle between two points in degrees
    * Corrected for Java AWT coordinate system (Y grows downward)
    * 
    * @param point1 Starting point (origin)
    * @param point2 Target point
    * @return Angle in degrees (0-360°), where 0° is east, 90° is north
    */
   private static double calculateAngle(Point2D point1, Point2D point2) {
       double dx = point2.getX() - point1.getX();
       double dy = point2.getY() - point1.getY();
       
       // Negative dy for AWT coordinate system (Y grows downward)
       double angle = Math.atan2(-dy, dx) * 180 / Math.PI;
       
       // Normalize to 0-360° range
       if (angle < 0) angle += 360;
       return angle;
   }
   
   /**
    * Check if a test angle falls within a specified angular range
    * Handles wrap-around at 0°/360° boundary
    * 
    * @param testAngle Angle to test (in degrees)
    * @param startAngle Starting angle of the range
    * @param rangeAngle Width of the angular range
    * @return true if testAngle is within the range
    */
   private boolean isAngleInRange(double testAngle, double startAngle, double rangeAngle) {
       double endAngle = (startAngle + rangeAngle) % 360;
       
       if (startAngle <= endAngle) {
           // Normal case - range doesn't cross 0°
           return testAngle >= startAngle && testAngle <= endAngle;
       } else {
           // Wrap-around case - range crosses 0°/360° boundary
           return testAngle >= startAngle || testAngle <= endAngle;
       }
   }

   /**
    * Find all hexagons within the current cone area
    * Updates the visual attack tiles and triggers a repaint
    * 
    * @return List of all hexagons within the cone of effect
    */
   public ArrayList<Hexagon> getAttackedCharacters() {
       ArrayList<Hexagon> attacked = new ArrayList<>();
       
       // Clear previous attack visualization
       gh.attackTiles.clear();
       
       // Update current radius and start angle
       getRadius();
       getStartAngle();
       
       // Check each hexagon in the grid
       for(Hexagon h : gh.hexlist.values()) {
           double distance = Calc.distance(h.getCenter(), originPoint);
           
           // Skip the origin hex (too close to origin)
           if(distance < gh.hexSize) continue;
           
           // Check if hexagon is within radius
           if (distance <= radius + 1) {
               // Calculate angle from origin to this hexagon
               double hexAngle = calculateAngle(originPoint, h.getCenter());
               
               // Check if hexagon is within the cone's angular range
               if (isAngleInRange(hexAngle, startAngle, angle)) {
                   attacked.add(h);           // Add to result list
                   gh.attackTiles.add(h);     // Add to visual highlight list
               }
           }
       }
       
       // Trigger visual update to show affected area
       gh.repaint();
       return attacked;
   }

   /**
    * Adjust the cone's angular width
    * 
    * @param notches Number of 5-degree increments to change (positive or negative)
    */
   public void changeAngle(int notches) {
       angle += notches * 5; // 5 degrees per notch
       
       // Normalize angle to 0-360° range
       angle = angle % 360;
       if (angle < 0) angle += 360;
       
       System.out.println(angle); // Debug output
   }

   /**
    * Get the current angular width of the cone
    * 
    * @return Cone angle in degrees
    */
   public double getAngle() {
       return angle;
   }

   /**
    * Calculate and return the cone's starting angle
    * The cone is centered on the mouse direction, so start angle is center minus half the cone width
    * 
    * @return Starting angle of the cone in degrees
    */
   public double getStartAngle() {
       // Return cached angle if no tile under mouse
       if(gh.tileUnderMouse == null) return startAngle;
       
       // Calculate the direction the cone is pointing
       double mouseAngle = calculateAngle(originPoint, 
           gh.io.isCtrlDown && gh.io.isMouseActive() 
               ? gh.io.mousePos 
               : gh.tileUnderMouse.getCenter()
       );
       
       // Center the cone on the mouse direction
       startAngle = mouseAngle - angle/2;
       
       // Normalize to 0-360° range
       startAngle = startAngle % 360;
       if(startAngle < 0) startAngle += 360;
       
       return startAngle;
   }
}