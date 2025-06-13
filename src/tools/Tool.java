package tools;

import java.awt.Image;
import java.awt.Rectangle;

/**
* Represents a single tool in the toolbox with its icon, functionality, and interaction area
* Each tool has a specific mode that determines its behavior when selected
*/
public class Tool {

   // Tool mode constants - define the different types of tools available
   /** Mode for dragging/moving objects on the map */
   public static final int DRAG_MODE   = 0;
   
   /** Mode for measuring distances between points */
   public static final int LENGTH_MODE = 1;
   
   /** Mode for calculating circular area effects */
   public static final int AREA_MODE   = 2;
   
   /** Mode for creating linear attacks or measurements */
   public static final int LINE_MODE   = 3;
   
   /** Mode for cone-shaped area calculations */
   public static final int CONE_MODE   = 4;

   /** Rectangle defining the clickable area of the tool in the toolbox */
   private Rectangle hitbox;
   
   /** Visual icon displayed for this tool */
   private Image icon;
   
   /** Current operational mode of the tool */
   private int toolMode;
   
   /** Sort order/position of the tool in the toolbox (immutable) */
   private final int sort;
   
   /**
    * Constructor - creates a new tool with specified properties
    * 
    * @param icon Visual representation of the tool
    * @param toolMode Operational mode (use Tool.MODE constants)
    * @param sort Position/order in the toolbox (0 = first, 1 = second, etc.)
    */
   public Tool(Image icon, int toolMode, int sort) {
       this.icon = icon;
       this.toolMode = toolMode;
       this.sort = sort;
       
       // Initialize hitbox with default 32px size and position based on sort order
       // Position: x=1, y=sort*32-1 (stacked vertically with 1px border)
       // Size: 30x30 (32px minus 2px border)
       this.hitbox = new Rectangle(1, sort * 32 - 1, 30, 30);
   }
   
   /**
    * Get the tool's visual icon
    * 
    * @return Image object representing the tool's icon
    */
   public Image getIcon() {
       return icon;
   }
   
   /**
    * Update the tool's hitbox dimensions and position based on new size
    * Called when the toolbox is resized
    * 
    * @param s New size for tool icons in pixels
    */
   public void updateHitbox(int s) {
       // Recalculate hitbox: maintain 1px border, scale with new size
       // Position: x=1, y=sort*newSize-1 (maintain vertical stacking)
       // Size: (newSize-2) x (newSize-2) (maintain border)
       this.hitbox = new Rectangle(1, sort * s - 1, s - 2, s - 2);
   }
   
   /**
    * Set a new icon for the tool
    * 
    * @param icon New image to display for this tool
    */
   public void setIcon(Image icon) {
       this.icon = icon;
   }
   
   /**
    * Get the current operational mode of the tool
    * 
    * @return Tool mode constant (DRAG_MODE, LENGTH_MODE, etc.)
    */
   public int getToolMode() {
       return toolMode;
   }
   
   /**
    * Change the tool's operational mode
    * 
    * @param toolMode New tool mode (use Tool.MODE constants)
    */
   public void setToolMode(int toolMode) {
       this.toolMode = toolMode;
   }

   /**
    * Move the tool's hitbox by specified offset
    * Used for repositioning tools within the toolbox
    * 
    * @param x Horizontal offset in pixels
    * @param y Vertical offset in pixels
    */
   public void moveBy(int x, int y) {
       hitbox.translate(x, y);
   }
   
   /**
    * Get the tool's clickable area
    * Used for mouse interaction detection
    * 
    * @return Rectangle defining the tool's hitbox boundaries
    */
   public Rectangle getHitbox() {
       return hitbox;
   }
}