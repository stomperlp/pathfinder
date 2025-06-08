package main;

import calc.AStar;
import entities.Entity;
import fx.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class GameHandler implements Runnable{

	protected final GraphicsHandler gh;
	private final Object lock = new Object();
	private int moveSpeed = 3;
	public HashMap<Entity, Double> init = new HashMap<>();

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
		if (h == null || c == null) return;
		AStar.run(c.getTile(), h, gh, false);
		c.setTile(h);
	}

	public void initiative(ArrayList<Entity> entities) {
		if (entities == null) return;
		for (Entity entity : entities) {
			initiative(entity);
		}
	}
	public void initiative(Entity entity) {
		if (entity == null) return;
		if (!init.containsKey(entity))
			init.put(entity, Math.random());
		
	}
	public void removeFromInitiative(Entity entity) {
		if (entity == null) return;
		init.remove(entity);
	}
	public void sortInitiative() {
		init = init.entrySet().stream()
		.sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
		.collect(
			java.util.stream.Collectors.toMap(
				java.util.Map.Entry::getKey,
				java.util.Map.Entry::getValue,
				(oldValue, newValue) -> oldValue,
				java.util.LinkedHashMap::new
			)
		);
	}
	public void showInitiative() {
			
	}
}
