package tools;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Calc {
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(
            img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        
        return bimage;
    }

    public static Image cutCirce(Image image) {
        
        BufferedImage bImage = toBufferedImage(image);
        int diameter = Math.min(bImage.getWidth(), bImage.getHeight());
        BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        
        int radius = diameter / 2;
        int centerX = radius;
        int centerY = radius;
        
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
}
