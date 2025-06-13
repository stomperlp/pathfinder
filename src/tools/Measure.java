package tools;

import calc.AStar;
import calc.Calc;
import java.awt.Point;
import java.awt.geom.Line2D;
import main.GraphicsHandler;

/**
* Measurement tool for calculating distances between points on a hexagonal grid
* Allows users to measure distances with visual feedback and pathfinding-based calculations
*/
public class Measure {
   /** Reference to the main graphics handler */
   private final GraphicsHandler gh;
   
   /** Grid coordinates of the measurement's starting point */
   private final Point origin;
   
   /** Grid coordinates of the measurement's end point (null while measuring) */
   private Point finishedPoint;
   
   /** The geometric line representing the current measurement */
   private Line2D line = new Line2D.Double();

   /**
    * Constructor - creates a new measurement starting from the current tile under mouse
    * 
    * @param gh Graphics handler containing the hexagonal grid and mouse state
    */
   public Measure(GraphicsHandler gh) {
       this.gh = gh;
       // Set origin to the current tile under the mouse cursor
       this.origin = gh.tileUnderMouse.getGridPoint();
   }

   /**
    * Get the current measurement line geometry
    * Line extends from origin to either the finished point or current mouse position
    * 
    * @return Line2D object representing the measurement line, or null if calculation fails
    */
   public Line2D getLine() {
       try {
           // Set line from origin center to target point
           line.setLine(
               gh.hexlist.get(origin.x, origin.y).getCenter(),
               // Use finished point if measurement is complete, otherwise follow mouse
               finishedPoint == null 
                   ? gh.tileUnderMouse.getCenter() 
                   : gh.hexlist.get(finishedPoint.x, finishedPoint.y).getCenter()
           );
       } catch (Exception e) {
           // Return null if origin/target tile doesn't exist or other error occurs
           return null;
       }
       
       return line;
   }
   
   /**
    * Finalize the measurement at the current mouse position
    * Handles special case of double-clicking on origin to clear all measurements
    * 
    * @return true if measurements were cleared (double-click on origin), false otherwise
    */
   public boolean finish() {
       // Set the finished point if not already set
       if (finishedPoint == null){
           finishedPoint = gh.tileUnderMouse.getGridPoint();
       }
       
       // Special case: if finishing on the same tile as origin, clear all measurements
       if (finishedPoint.equals(origin)) {
           gh.measure.clear();
           return true; // Indicate that measurements were cleared
       }
       
       return false; // Normal completion, measurement remains active
   }

   /**
    * Calculates the length of the current path.
    * @return Path length in pixels (waypoints * 5) or 0 for empty path
    */
    public int length() {
    // Check if the path is not yet finished
    if(finishedPoint == null) {
        // Calculate optimal path using A* algorithm from start point to current mouse position
        int l = AStar.run(
            gh.hexlist.get(origin.x,origin.y),  // Start point in hex grid
            gh.tileUnderMouse,                  // Target point under mouse cursor
            gh,                                 // Game handler for game field context
            true                                // Enable A* optimization
        ).size();
        
        // Convert waypoints to pixel length: (waypoints-1)*5, since first point doesn't count
        return l == 0 ? 0 : (l-1)*5;
    }
    else 
        // Path is finished: calculate direct distance between start and end point
        return 5 * (int) Calc.distance(origin, finishedPoint);
    }
   
   /**
    * Get the grid coordinates of the measurement's end point
    * 
    * @return Point containing x,y grid coordinates of the end, or null if not finished
    */
   public Point getFinishedPoint() {
       return finishedPoint;
   }
   
   /**
    * Get the grid coordinates of the measurement's starting point
    * 
    * @return Point containing x,y grid coordinates of the origin
    */
   public Point getOrigin() {
       return origin;
   }
}