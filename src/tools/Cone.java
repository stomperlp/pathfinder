package tools;
import calc.Calc;
import fx.Hexagon;
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
        if(gh.tileUnderMouse == null) return radius;
        radius = gh.io.isCtrlDown && gh.io.isMouseActive()
            ? Calc.distance(gh.io.mousePos, originPoint)
            : Calc.distance(gh.tileUnderMouse.getCenter(), originPoint);
        return radius;
    }

    public double getRadiusInTiles() {
        return ((getRadius()+1)/(gh.hexSize*Math.sqrt(3)));
    }

    public Point getOrigin() {
        return origin;
    }

    // Korrekte Winkelberechnung
    // Korrekte Winkelberechnung für Java AWT
    private static double calculateAngle(Point2D point1, Point2D point2) {
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
            double distance = Calc.distance(h.getCenter(), originPoint);
            if(distance < gh.hexSize) continue;
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
        if (angle < 0) angle += 360;
        System.out.println(angle);
    }

    public double getAngle() {
        return angle;
    }

    public double getStartAngle() {
        if(gh.tileUnderMouse == null) return startAngle;
        double mouseAngle = calculateAngle(originPoint, gh.io.isCtrlDown && gh.io.isMouseActive() ? gh.io.mousePos : gh.tileUnderMouse.getCenter());
        startAngle = mouseAngle - angle/2;
        
        // Normalisierung
        startAngle = startAngle % 360;
        if(startAngle < 0) startAngle += 360;
        

        return startAngle;
    }
}