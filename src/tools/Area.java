package tools;

import calc.Calc;
import fx.Hexagon;
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
        if(gh.tileUnderMouse == null) return radius;
        originPoint = gh.hexlist.get(origin.x, origin.y).getCenter();
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
    public ArrayList<Hexagon> getAttackedCharacters() {
        ArrayList<Hexagon> attacked = new ArrayList<>();
        gh.attackTiles.clear();
        getRadius();
        for(Hexagon h : gh.hexlist.values()) {
            if ( 
                radius+1 > Calc.distance(h.getCenter(), originPoint)
            ) {
                attacked.add(h);
                gh.attackTiles.add(h);
            }
        }
        gh.repaint();
        return attacked;
    }
}
