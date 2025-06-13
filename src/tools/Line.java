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

public class Line {
    private final GraphicsHandler gh;
    private final Point origin;
    private final Line2D line = new Line2D.Double();

    public Line(GraphicsHandler gh) {
        this.gh = gh;
        this.origin = gh.tileUnderMouse.getGridPoint();
    }

    public Line2D getLine() {
        
        if(gh.tileUnderMouse == null) return line;
        try {
            line.setLine(
                gh.hexlist.get(origin.x, origin.y).getCenter(),
                gh.io.isCtrlDown && gh.io.isMouseActive() ? gh.io.mousePos : gh.tileUnderMouse.getCenter()
            );
        } catch (Exception e) {
            return null;
        }
        
        return line;
    }
    public int length() {
        int l = AStar.run(
            gh.hexlist.get(origin.x,origin.y),gh.tileUnderMouse,
            gh,
            true
        ).size();
        return l == 0 ? 0 : (l-1)*5;
    }
    public Point getOrigin() {
        return origin;
    }
    public ArrayList<Hexagon> getAttackedCharacters() {
        ArrayList<Hexagon> attacked = new ArrayList<>();
        gh.attackTiles.clear();
        for (Point2D p : getPointsAlongLineBySpacing(line, (double) gh.hexSize * Math.sqrt(3))) {
            Hexagon h = gh.findClosestHexagon(p);
            if (h == null) continue;
            Entity e = (Character) gh.selectEntity(h);
            gh.attackTiles.add(h);
            if (e instanceof Character c) {
                if(c.getOccupiedTiles().contains(h)){
                    attacked.add(h);
                }
            } else {
                //TODO Implement collision effect for line attacks
            }
            gh.repaint();
        }
        return attacked;
    }
    public static int getNumPointsAlongLineBySpacing(Line2D line, double spacing) {
        double length = Math.sqrt(Math.pow(line.getX2() - line.getX1(), 2) + 
                                 Math.pow(line.getY2() - line.getY1(), 2));
        
        return (int) Math.ceil(length / spacing) + 1;
    }
    public static List<Point2D> getPointsAlongLineBySpacing(Line2D line, double spacing) {
        int numPoints = getNumPointsAlongLineBySpacing(line, spacing);
        
        return getPointsAlongLineByNumber(line, numPoints);
    }
    public static List<Point2D> getPointsAlongLineByNumber(Line2D line, int numPoints) {
        List<Point2D> points = new ArrayList<>();
        
        double x1 = line.getX1();
        double y1 = line.getY1();
        double x2 = line.getX2();
        double y2 = line.getY2();
        
        // Calculate the step size for equal spacing
        double step = 1.0 / (numPoints - 1);
        
        for (int i = 0; i < numPoints; i++) {
            double t = i * step; // Parameter from 0 to 1
            
            // Linear interpolation
            double x = x1 + t * (x2 - x1);
            double y = y1 + t * (y2 - y1);
            
            points.add(new Point2D.Double(x, y));
        }
        
        return points;
    }
}
