package fx;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

public class Area {
    private final GraphicsHandler gh;
    private final Point origin;
    private Point2D originPoint;
    private double radius;

    public Area(GraphicsHandler gh) {
        this.gh = gh;
        this.origin = gh.tileUnderMouse.getGridPoint();
        originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
    }

    public double getRadius() {
        originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
        radius = Math.sqrt(Math.pow(gh.tileUnderMouse.getCenter().getX() - originPoint.getX(), 2) + 
                           Math.pow(gh.tileUnderMouse.getCenter().getY() - originPoint.getY(), 2));
        
        return radius;
    }
    public double getRadiusInTiles() {
        return (getRadius()/(gh.hexSize*Math.sqrt(3)));
    }
    public Point getOrigin() {
        return origin;
    }
    public ArrayList<Hexagon> getAttackedCharacters() {
        ArrayList<Hexagon> attacked = new ArrayList<>();
        gh.attackTiles.clear();
        getRadius();
        for(Hexagon h : gh.hexlist.values()) {
            if ( 
                radius+1 > 
                Math.sqrt(Math.pow(h.getCenter().getX() - originPoint.getX(), 2) + 
                          Math.pow(h.getCenter().getY() - originPoint.getY(), 2))
            ) {
                attacked.add(h);
                gh.attackTiles.add(h);
            }
        }
        gh.repaint();
        return attacked;
    }
}
