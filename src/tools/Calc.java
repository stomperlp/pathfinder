package tools;

import fx.Hexagon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Calc {
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage bufferedImage) {
            return bufferedImage;
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
    public static Image cutHex(Image image) {
        
        BufferedImage bImage = toBufferedImage(image);
        int hexSize = (int) (bImage.getHeight()/2);
        Hexagon hex = new Hexagon(new Point2D.Double(hexSize*2/Math.sqrt(3), hexSize), (int) (hexSize*2/Math.sqrt(3)), null);
        BufferedImage output = new BufferedImage( (int) (hexSize*4/Math.sqrt(3)), (int) (hexSize*2), BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < (int) hexSize*2; y++) {
            System.out.println(y);
            for (int x = 0; x < (int) hexSize*4/Math.sqrt(3); x++) {
                try {
                    if(hex.contains(new Point2D.Double(x,y))){
                        output.setRGB(x, y, bImage.getRGB(x, y));
                    }
                    
                } catch (Exception e) {
                }
            }
        }
        return output;
    }
}

