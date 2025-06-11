package main;

import calc.AStar;
import entities.Entity;
import fx.*;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class GameHandler implements Runnable {

	protected final GraphicsHandler gh;
	private final Object lock = new Object();
	private int moveSpeed = 3;
	public HashMap<Entity, Double> init = new HashMap<>();
	public ArrayList<Marker> intiMarkers = new ArrayList<>();
	private Entity currentTurn = null;
	private int currentTurnIndex = 0;

	public GameHandler(GraphicsHandler gh) {
		this.gh = gh;
	}

	public Hexagon findClosestHexagon(Point2D point) {
		Hexagon closest = null;
		double minDist = gh.hexSize-1;
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
		if(gh.io.isAltDown) 	   moveSpeed = 200;
		else if(gh.io.isShiftDown) moveSpeed = 12;
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
		gh.outOfBoundsCorrection();
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
		sortInitiative();
	}
	public void initiative(Entity entity) {
		if (entity == null) return;

		if (!init.containsKey(entity)) {
			init.put(entity, Math.random());
			Point coords = new Point((int) entity.getOccupiedTiles().getFirst().getCenter().getX(),
								   (int) entity.getOccupiedTiles().getFirst().getCenter().getY() - gh.hexSize);
			Marker m = new Marker(init.get(entity), coords , 1, false);
			m.attachTo(entity);
			intiMarkers.add(m);
			gh.addMarker(m);
		}
	}
	public void removeFromInitiative(Entity entity) {
		if (entity == null) return;

		Marker markerToRemove = null;
		Point entityPosition = entity.getOccupiedTiles().getFirst().getGridPoint();
		for (Marker m : intiMarkers) {
			if (m.getPoint().equals(entityPosition)) {
				markerToRemove = m;
				break;
			}
		}
		if (markerToRemove != null) {
            intiMarkers.remove(markerToRemove);
            gh.markers.remove(markerToRemove);
        }
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
	public void toggleShowInitiative() {
		for (Marker m : intiMarkers) {
			m.toggleVisible();
		}
		gh.repaint();
	}
	public void nextTurn() {
		ArrayList<Entity> order = getInitiativeOrder();
		currentTurnIndex = (currentTurnIndex + 1) % order.size();
		currentTurn = order.get(currentTurnIndex);
	}
	public ArrayList<Entity> getInitiativeOrder() {
		return new ArrayList<>(init.keySet());
	}
	public Entity getCurrentTurn() {
		return currentTurn;
	}
	public Entity getNextInInitiative(Entity current) {
		ArrayList<Entity> order = getInitiativeOrder();
		int currentIndex = order.indexOf(current);
		if (currentIndex == -1 || currentIndex == order.size() - 1) {
			return order.isEmpty() ? null : order.get(0);
		}
		return order.get(currentIndex + 1);
	}
	public boolean isInInitiative(Entity entity) {
		return init.containsKey(entity);
	}
}
