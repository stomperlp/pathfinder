package tools;

import calc.AStar;
import entities.Character;
import entities.Entity;
import fx.Hexagon;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import main.GraphicsHandler;

/**
* Line tool for creating linear attacks, measurements, and line-of-sight calculations
* Used for ranged attacks, spell lines, or distance measurements on a hexagonal grid
*/
public class Line {
   /** Reference to the main graphics handler */
   private final GraphicsHandler gh;
   
   /** Grid coordinates of the line's starting point */
   private final Point origin;
   
   /** The geometric line object representing the current line */
   private final Line2D line = new Line2D.Double();

   /**
    * Constructor - creates a new line starting from the current tile under mouse
    * 
    * @param gh Graphics handler containing the hexagonal grid and mouse state
    */
   public Line(GraphicsHandler gh) {
       this.gh = gh;
       // Set origin to the current tile under the mouse cursor
       this.origin = gh.tileUnderMouse.getGridPoint();
   }

   /**
    * Get the current line geometry from origin to target
    * Target is determined by either mouse position or current tile under cursor
    * 
    * @return Line2D object representing the current line, or null if calculation fails
    */
   public Line2D getLine() {
       // Return cached line if no tile under mouse
       if(gh.tileUnderMouse == null) return line;
       
       try {
           // Set line from origin center to target point
           line.setLine(
               gh.hexlist.get(origin.x, origin.y).getCenter(),
               // Use exact mouse position if Ctrl+mouse active, otherwise snap to tile center
               gh.io.isCtrlDown && gh.io.isMouseActive() 
                   ? gh.io.mousePos 
                   : gh.tileUnderMouse.getCenter()
           );
       } catch (Exception e) {
           // Return null if origin tile doesn't exist or other error occurs
           return null;
       }
       
       return line;
   }
   
   /**
    * Calculate the length of the line using pathfinding
    * Uses A* algorithm to find actual traversable distance between tiles
    * 
    * @return Length in game units (feet), where each tile step = 5 feet
    */
   public int length() {
       // Use A* pathfinding to get actual traversable path length
       int pathLength = AStar.run(
           gh.hexlist.get(origin.x, origin.y),  // Start hex
           gh.tileUnderMouse,                   // End hex
           gh,                                  // Graphics handler for map data
           true                                 // Allow diagonal movement
       ).size();
       
       // Convert path steps to game distance (0 if no path found, otherwise (steps-1)*5 feet)
       return pathLength == 0 ? 0 : (pathLength - 1) * 5;
   }
   
   /**
    * Get the grid coordinates of the line's origin point
    * 
    * @return Point containing x,y grid coordinates of the start
    */
   public Point getOrigin() {
       return origin;
   }
   
   /**
    * Find all characters hit by the line attack
    * Samples points along the line and checks for character collisions
    * 
    * @return List of hexagons containing characters hit by the line
    */
   public ArrayList<Hexagon> getAttackedCharacters() {
       ArrayList<Hexagon> attacked = new ArrayList<>();
       
       // Clear previous attack visualization
       gh.attackTiles.clear();
       
       // Sample points along the line based on hex spacing
       for (Point2D p : getPointsAlongLineBySpacing(line, (double) gh.hexSize * Math.sqrt(3))) {
           // Find the closest hexagon to this sample point
           Hexagon h = gh.findClosestHexagon(p);
           if (h == null) continue;
           
           // Check if there's an entity in this hex
           Entity e = (Character) gh.selectEntity(h);
           
           // Add hex to visual attack tiles
           gh.attackTiles.add(h);
           
           if (e instanceof Character c) {
               // If character occupies this hex, add to attacked list
               if(c.getOccupiedTiles().contains(h)){
                   attacked.add(h);
               }
           } else {
               // TODO: Implement collision effects for line attacks hitting terrain/objects
           }
           
           // Update visual display
           gh.repaint();
       }
       return attacked;
   }

   /**
    * Generate evenly spaced points along a line based on distance spacing
    * 
    * @param line The line to sample points from
    * @param spacing Distance between sample points
    * @return List of Point2D objects representing sample points along the line
    */
   public static List<Point2D> getPointsAlongLineBySpacing(Line2D line, double spacing) {
       // Calculate total line length using Pythagorean theorem
       double length = Math.sqrt(Math.pow(line.getX2() - line.getX1(), 2) + 
                                Math.pow(line.getY2() - line.getY1(), 2));
       
       // Calculate number of points needed for the given spacing
       int numPoints = (int) Math.ceil(length / spacing) + 1;
       
       // Generate the points using equal parameter spacing
       return getPointsAlongLineByNumber(line, numPoints);
   }
   
   /**
    * Generate a specific number of evenly distributed points along a line
    * Uses linear interpolation (lerp) to place points at equal parameter intervals
    * 
    * @param line The line to sample points from
    * @param numPoints Number of points to generate along the line
    * @return List of Point2D objects representing evenly distributed points
    */
   public static List<Point2D> getPointsAlongLineByNumber(Line2D line, int numPoints) {
       List<Point2D> points = new ArrayList<>();
       
       // Extract line endpoints
       double x1 = line.getX1();
       double y1 = line.getY1();
       double x2 = line.getX2();
       double y2 = line.getY2();
       
       // Calculate parameter step size for equal distribution
       double step = 1.0 / (numPoints - 1);
       
       // Generate points using linear interpolation
       for (int i = 0; i < numPoints; i++) {
           double t = i * step; // Parameter from 0 to 1
           
           // Linear interpolation formula: P = P1 + t * (P2 - P1)
           double x = x1 + t * (x2 - x1);
           double y = y1 + t * (y2 - y1);
           
           points.add(new Point2D.Double(x, y));
       }
       
       return points;
   }
}