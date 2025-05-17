package entities;

import fx.Hexagon;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;
import tools.Calc;

public class Wall extends Entity {

    public Wall(GraphicsHandler gh, Image image, Hexagon tile, Point2D location) {
        super(gh, Calc.cutHex(image), tile, location);
    }

    @Override
    public ArrayList<Hexagon> getOccupiedTiles() {
        occupiedTiles.clear();
        occupiedTiles.add(tile);
        return occupiedTiles;
    }
}
