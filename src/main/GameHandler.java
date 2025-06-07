package main;

import calc.AStar;
import fx.*;
import java.awt.geom.Point2D;

public class GameHandler implements Runnable{

    protected final GraphicsHandler gh;
	private final Object lock = new Object();

    
    public GameHandler(GraphicsHandler gh){
		this.gh = gh;
	}

	public Hexagon findClosestHexagon(Point2D point) {
		Hexagon closest = null;
		double minDist = gh.hexSize;
		for (Hexagon hex : gh.hexlist.values()) {
			double dist = point.distance(hex.getCenter());
			if (dist < minDist) {
				minDist = dist;
				closest = hex;
			}
		}
		return closest;
	}
    public void tick(){

        if(gh.io.ADown) {
            gh.gridOffset(3, 0);
            gh.repaint();
        }
        if(gh.io.DDown) {
            gh.gridOffset(-3, 0);
            gh.repaint();
        }
        if(gh.io.SDown) {
            gh.gridOffset(0, -3);
            gh.repaint();
        }
        if(gh.io.WDown) {
            gh.gridOffset(0, 3);
            gh.repaint();
        }
	}

	@Override
	public void run() {
		Thread producer = new Thread(() -> {
			synchronized (lock) {
				while (true) { 
					tick();
					try {
						lock.wait(16);
					} catch (InterruptedException e) {}
				}
			}
		});
		producer.start();
	}

	public void moveCharacter(Hexagon h, entities.Character c) {
		// TODO: A* Implementation
		AStar.run(c.getTile(), h, gh);
		c.setTile(h);
	}
	
}
