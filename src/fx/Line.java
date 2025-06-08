package fx;

import calc.AStar;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import main.GraphicsHandler;

public class Line {
    private final GraphicsHandler gh;
    private final Point origin;
    private Point finishedPoint;
    private Line2D line = new Line2D.Double();

    public Line(GraphicsHandler gh) {
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
    public ArrayList<Hexagon> tiles() {
        return AStar.run(
            gh.hexlist.get(origin.x,origin.y),
            getFinishedPoint() == null ? gh.tileUnderMouse : gh.hexlist.get(finishedPoint.x, finishedPoint.y),
            gh,
            true
        );
    }
    public int length() {
        int l = AStar.run(
            gh.hexlist.get(origin.x,origin.y),
            getFinishedPoint() == null ? gh.tileUnderMouse : gh.hexlist.get(finishedPoint.x, finishedPoint.y),
            gh,
            true
        ).size();
        return l == 0 ? 0 : (l-1)*5;
    }
    public Point getFinishedPoint() {
        return finishedPoint;
    }
    public Point getOrigin() {
        return origin;
    }
}
