package main;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import tools.Tool;

/**
* Toolbox panel that displays and manages drawing/editing tools
* Extends JPanel to provide a visual toolbar interface
*/
public class Toolbox extends JPanel{

   /** Vertical scroll offset for the toolbox */
   private int scroll;
   
   /** List of all available tools */
   private ArrayList<Tool> tools = new ArrayList<>();
   
   /** Currently selected tool */
   protected Tool selectedTool;
   
   /** Size of each tool icon in pixels */
   private int size;

   /**
    * Constructor - initializes the toolbox with default settings
    * Sets up the panel size and loads all tools
    */
   @SuppressWarnings("OverridableMethodCallInConstructor")
   public Toolbox() {
       super();
       this.size = 32; // Default icon size
       this.setPreferredSize(new Dimension(32, 200)); // Fixed width, scrollable height
       try {
           initializeTools(); // Load all available tools
       } catch (IOException e) {
           // TODO: Implement proper error handling for missing tool icons
           e.printStackTrace();
       }
   }

   /**
    * Custom paint method to render the toolbox
    * Draws all tool icons and highlights the selected tool
    * 
    * @param g Graphics context for drawing
    */
   @Override
   protected void paintComponent(Graphics g) {
       super.paintComponent(g);

       // Cast to Graphics2D for advanced rendering features
       Graphics2D g2d = (Graphics2D) g;
       g2d.setComposite(AlphaComposite.SrcOver.derive(1f)); // Full opacity
       g2d.setColor(Color.BLUE); // Selection highlight color
       g2d.setStroke(new BasicStroke(2)); // Border thickness
       
       // Draw each tool icon vertically
       int height = 0;
       for (Tool tool : getTools()) {
           // Draw tool icon with 1px padding on each side
           g2d.drawImage(tool.getIcon(), 1, height * size, size - 2, size - 2, this);
           height++;
       }

       // Draw selection rectangle around currently selected tool
       if(selectedTool != null) {
           g2d.draw(selectedTool.getHitbox());
       }
   }
   
   /**
    * Initialize all available tools with their icons and modes
    * Loads tool icons from the resources directory
    * 
    * @throws IOException if tool icon files cannot be loaded
    */
   public void initializeTools() throws IOException {
       // Move/drag tool
       tools.add(new Tool(
           ImageIO.read(new File("src/resources/images/toolIcons/move.png")),
           Tool.DRAG_MODE,
           0
       ));
       
       // Length measurement tool
       tools.add(new Tool(
           ImageIO.read(new File("src/resources/images/toolIcons/length.png")),
           Tool.LENGTH_MODE,
           1
       ));
       
       // Area measurement tool
       tools.add(new Tool(
           ImageIO.read(new File("src/resources/images/toolIcons/area.png")),
           Tool.AREA_MODE,
           2
       ));
       
       // Cone drawing tool
       tools.add(new Tool(
           ImageIO.read(new File("src/resources/images/toolIcons/cone.png")),
           Tool.CONE_MODE,
           3
       ));
       
       // Line drawing tool
       tools.add(new Tool(
           ImageIO.read(new File("src/resources/images/toolIcons/line.png")),
           Tool.LINE_MODE,
           4
       ));
   }
   
   /**
    * Get the list of all available tools
    * 
    * @return ArrayList containing all tools
    */
   public ArrayList<Tool> getTools() {
       return tools;
   }
   
   /**
    * Set the list of available tools
    * 
    * @param tools New list of tools to use
    */
   public void setTools(ArrayList<Tool> tools) {
       this.tools = tools;
   }
   
   /**
    * Change the size of tool icons
    * Updates the panel dimensions and tool hitboxes accordingly
    * 
    * @param s Size increment (positive to increase, negative to decrease)
    */
   public void changeSize(int s) {
       size += s; // Adjust icon size
       setPreferredSize(new Dimension(size, 200)); // Update panel width
       
       // Update hitboxes for all tools to match new size
       for(Tool t : tools) {
           t.updateHitbox(size);
       }
   }
   
   /**
    * Get the current icon size
    * 
    * @return Current size of tool icons in pixels
    */
   public int getFrameSize() {
       return size;
   }
}