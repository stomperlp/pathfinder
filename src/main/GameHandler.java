package main;

import calc.AStar;
import fx.*;
import java.awt.geom.Point2D;

public class GameHandler implements Runnable{

    protected final GraphicsHandler gh;
	private final Object lock = new Object();
	private int moveSpeed = 3;
    
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
		if(gh.io.isAltDown) 	   moveSpeed = 1000;
		if(gh.io.isShiftDown) 	   moveSpeed = 12;
		else if (gh.io.isCtrlDown) moveSpeed = 6;
		else 				 	   moveSpeed = 3;

        if(gh.io.ADown) {
            gh.gridOffset(moveSpeed, 0);
            gh.repaint();
        }
        if(gh.io.DDown) {
            gh.gridOffset(-moveSpeed, 0);
            gh.repaint();
        }
        if(gh.io.SDown) {
            gh.gridOffset(0, -moveSpeed);
            gh.repaint();
        }
        if(gh.io.WDown) {
            gh.gridOffset(0, moveSpeed);
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
		AStar.run(c.getTile(), h, gh, false);
		c.setTile(h);
	}
	
}
