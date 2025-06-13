package tools;

import calc.AStar;
import calc.Calc;
import java.awt.Point;
import java.awt.geom.Line2D;
import main.GraphicsHandler;

public class Measure {
    private final GraphicsHandler gh;
    private final Point origin;
    private Point finishedPoint;
    private Line2D line = new Line2D.Double();

    public Measure(GraphicsHandler gh) {
        this.gh = gh;
        this.origin = gh.tileUnderMouse.getGridPoint();
    }

    public Line2D getLine() {
        try {
            line.setLine(
                gh.hexlist.get(origin.x, origin.y).getCenter(), 
                finishedPoint == null ? gh.tileUnderMouse.getCenter() : gh.hexlist.get(finishedPoint.x, finishedPoint.y).getCenter()
            );
        } catch (Exception e) {
            return null;
        }
        
        return line;
    }
    public boolean finish() {
        //returns true if finishing process cleared the measures, so when double clicking.
        if (finishedPoint == null){
            finishedPoint = gh.tileUnderMouse.getGridPoint();
        }
        if (finishedPoint == origin) {
            gh.measure.clear();
            return true;
        }
        return false;
    }
    public int length() {
        if(finishedPoint == null) {
            int l = AStar.run(
                gh.hexlist.get(origin.x,origin.y),gh.tileUnderMouse,
                gh,
                true
            ).size();
            return l == 0 ? 0 : (l-1)*5;
        }
        else return 5 * (int) Calc.distance(origin, finishedPoint);
    }
    public Point getFinishedPoint() {
        return finishedPoint;
    }
    public Point getOrigin() {
        return origin;
    }
}
