package main;

/**
* Main application class
* Serves as the entry point for the program
*/
public class Main {
   
   /**
    * Main method - application entry point
    * Executed automatically when the program starts
    * 
    * @param args Command line arguments (not used)
    */
   public static void main(String[] args) {
       // Create a new instance of GraphicsHandler
       GraphicsHandler g = new GraphicsHandler();
       
       // Make the graphics window visible
       g.setVisible(true);
   }
}