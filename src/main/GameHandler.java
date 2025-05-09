package main;

import fx.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GameHandler {

<<<<<<< HEAD
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
    
=======
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
>>>>>>> 0f54971dbb3d2df5a9f1efda13a1c077b2781c5f
}
