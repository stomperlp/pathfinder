package calc;

import fx.Hexagon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import main.GraphicsHandler;

/**
 * A utility class providing various calculation and image manipulation methods
 * for hexagonal grid systems and graphics processing.
 */
public class Calc {

    /**
     * Converts an Image to a BufferedImage.
     * @param img The Image to convert
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        // If already a BufferedImage, return directly
        if (img instanceof BufferedImage bufferedImage) {
            return bufferedImage;
        }

        // Create a new BufferedImage with transparency
        BufferedImage bimage = new BufferedImage(
            img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the original image onto the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    /**
     * Crops an image into a circular shape.
     * @param image The image to crop
     * @return The circular-cropped image
     */
    public static Image cutCirce(Image image) {
        BufferedImage bImage = toBufferedImage(image);
        // Use smallest dimension as diameter
        int diameter = Math.min(bImage.getWidth(), bImage.getHeight());
        BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        
        int radius = diameter / 2;
        int centerX = radius;
        int centerY = radius;
        
        // Iterate through each pixel and only keep those within the circle
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance <= radius) {
                    output.setRGB(x, y, bImage.getRGB(x, y));
                }
            }
        }
        return output;
    }

    /**
     * Crops an image into a hexagonal shape.
     * @param image The image to crop
     * @return The hexagonal-cropped image
     */
    public static Image cutHex(Image image) {
        BufferedImage bImage = toBufferedImage(image);
        // Calculate hexagon size based on image height
        int hexSize = (int) (bImage.getHeight()/2);
        
        // Create reference hexagon for contains() checks
        Hexagon hex = new Hexagon(
            new Point2D.Double(
                hexSize*2/Math.sqrt(3),  // Center X
                hexSize                  // Center Y
            ), 
            (int) (hexSize*2/Math.sqrt(3)),  // Size
            null, 
            GraphicsHandler.isFlat       // Flat or pointy orientation
        );
        
        // Create output image with dimensions to fit the hexagon
        BufferedImage output = new BufferedImage( 
            (int) (hexSize*4/Math.sqrt(3)),  // Width
            (int) (hexSize*2),               // Height
            BufferedImage.TYPE_INT_ARGB
        );
        
        // Iterate through each pixel and only keep those within the hexagon
        for (int y = 0; y < (int) hexSize*2; y++) {
            for (int x = 0; x < (int) hexSize*4/Math.sqrt(3); x++) {
                try {
                    if(hex.contains(new Point2D.Double(x,y))){
                        output.setRGB(x, y, bImage.getRGB(x, y));
                    }
                } catch (Exception e) {
                    // Silently handle any pixel access errors
                }
            }
        }
        return output;
    }

    /**
     * Converts axial coordinates (x,y) to cube coordinates (x,y,z).
     * @param x The axial x coordinate
     * @param y The axial y coordinate (row)
     * @return Cube coordinates as int array [x, y, z]
     */
    public static int[] toCubeCoordinate(int x, int y) {
        int offset = (y >> 1); // Equivalent to y/2 for offset adjustment
        int x_cube = x - offset;
        int z_cube = y;
        int y_cube = -x_cube - z_cube; // Cube coordinates must sum to 0
        return new int[] {x_cube, y_cube, z_cube};
    }

    /**
     * Converts cube coordinates back to axial coordinates.
     * @param cube Cube coordinates as int array [x, y, z]
     * @return The corresponding axial Point (x,y)
     */
    public static Point toPoint(int[] cube) {
        int y = cube[2];
        int x = cube[0] + (cube[2] >> 1); // Apply offset based on row
        return new Point(x,y);
    }

    /**
     * Calculates Euclidean distance between two points.
     * @param a First point
     * @param b Second point
     * @return The distance between the points
     */
    public static double distance(Point2D a, Point2D b) {
        return Math.sqrt(
            Math.pow(a.getX() - b.getX(), 2) + 
            Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * Converts a Point2D to an integer Point.
     * @param p The Point2D to convert
     * @return A new Point with integer coordinates
     */
    public static Point toPoint(Point2D p) {
        return new Point((int) p.getX(), (int) p.getY());
    }
}