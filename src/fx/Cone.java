package fx;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

public class Cone {
    private final GraphicsHandler gh;
    private final Point origin;
    private Point2D originPoint;
    private double radius;
    private double angle;
    private double startAngle;

    public Cone(GraphicsHandler gh) {
        this.gh = gh;
        this.origin = gh.tileUnderMouse.getGridPoint();
        originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
        angle = 91;
    }

    public double getRadius() {
        originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
        radius = Math.sqrt(Math.pow(gh.io.mousePos.getX() - originPoint.getX(), 2) +
                Math.pow(gh.io.mousePos.getY() - originPoint.getY(), 2));
        return radius;
    }

    public double getRadiusInTiles() {
        return (getRadius()/(gh.hexSize*Math.sqrt(3)));
    }

    public Point getOrigin() {
        return origin;
    }

    // Korrekte Winkelberechnung
    // Korrekte Winkelberechnung für Java AWT
    private double calculateAngle(Point2D point1, Point2D point2) {
        double dx = point2.getX() - point1.getX();
        double dy = point2.getY() - point1.getY();
        // Negatives dy für AWT-Koordinatensystem (Y wächst nach unten)
        double angle = Math.atan2(-dy, dx) * 180 / Math.PI;
        // Normalisierung auf 0-360°
        if (angle < 0) angle += 360;
        return angle;
    }
    // Prüft ob ein Winkel im Bereich liegt (berücksichtigt Wrap-around)
    private boolean isAngleInRange(double testAngle, double startAngle, double rangeAngle) {
        // Normalisierung
        testAngle = testAngle % 360;
        if (testAngle < 0) testAngle += 360;
        
        double endAngle = (startAngle + rangeAngle) % 360;
        
        if (startAngle <= endAngle) {
            // Normaler Fall
            return testAngle >= startAngle && testAngle <= endAngle;
        } else {
            // Wrap-around über 0°
            return testAngle >= startAngle || testAngle <= endAngle;
        }
    }

    public ArrayList<Hexagon> getAttackedCharacters() {
        ArrayList<Hexagon> attacked = new ArrayList<>();
        gh.attackTiles.clear();
        getRadius();
        getStartAngle(); // Aktualisiert startAngle
        
        for(Hexagon h : gh.hexlist.values()) {
            double distance = Math.sqrt(Math.pow(h.getCenter().getX() - originPoint.getX(), 2) +
                    Math.pow(h.getCenter().getY() - originPoint.getY(), 2));
            
            if (distance <= radius + 1) {
                double hexAngle = calculateAngle(originPoint, h.getCenter());
                
                if (isAngleInRange(hexAngle, startAngle, angle)) {
                    attacked.add(h);
                    gh.attackTiles.add(h);
                }
            }
        }
        
        gh.repaint();
        return attacked;
    }

    public void changeAngle(int notches) {
        angle += notches * 5;
        angle = angle % 360;
        if (angle < 0) angle += 360; // Negative Winkel vermeiden
        System.out.println(angle);
    }

    public double getAngle() {
        return angle;
    }

    public double getStartAngle() {
        double mouseAngle = calculateAngle(originPoint, gh.io.mousePos);
        startAngle = mouseAngle - angle/2;
        
        // Normalisierung
        startAngle = startAngle % 360;
        
        return startAngle;
    }
}