package main;

import fx.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GameHandler {

    protected GraphicsHandler gh;
    
    public GameHandler(GraphicsHandler gh){
		this.gh = gh;
	}

	public Hexagon checkCurrentHexagon(Point2D p, Hexagon currentHexagon) {

		if (currentHexagon != null) {
			if(currentHexagon.contains(p))
			{
				return currentHexagon;
			}
		}
		ArrayList<Hexagon> hexlist = gh.getHexlist();

		for(Hexagon hex : hexlist)
		{
			if(hex.contains(p))
			{
				return hex;
			}
		}
        return null;
	}
    
}
