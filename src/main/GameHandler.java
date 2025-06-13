package main;

import calc.AStar;
import entities.Entity;
import fx.*;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main game loop handler that manages:
 * - Game state updates
 * - Character movement
 * - Initiative tracking
 * - Camera movement
 */
public class GameHandler implements Runnable {

    // Reference to graphics handler for rendering
    protected final GraphicsHandler gh;
    // Lock object for thread synchronization
    private final Object lock = new Object();
    // Base movement speed for camera panning
    private int moveSpeed = 3;
    
    // Initiative tracking system
    public HashMap<Entity, Double> init = new HashMap<>();  // Entity -> initiative value
    public ArrayList<Marker> initMarkers = new ArrayList<>();  // Visual markers for initiative
    private Entity currentTurn = null;  // Entity whose turn it currently is
    private int currentTurnIndex = 0;   // Index in initiative order

    public GameHandler(GraphicsHandler gh) {
        this.gh = gh;
    }

    /**
     * Main game update tick (called every frame)
     * Handles camera movement based on keyboard input
     */
    public void tick() {
        // Adjust movement speed based on modifier keys
        if(gh.io.isAltDown)         return;  // Pause movement
        else if(gh.io.isShiftDown)  moveSpeed = 12;  // Fast speed
        else if (gh.io.isCtrlDown)  moveSpeed = 6;   // Medium speed
        else                        moveSpeed = 3;   // Normal speed

        // Handle camera panning
        if(gh.io.ADown) {
            gh.gridOffset(moveSpeed, 0);  // Move left
            gh.repaint();
        }
        if(gh.io.DDown) {
            gh.gridOffset(-moveSpeed, 0); // Move right
            gh.repaint();
        }
        if(gh.io.SDown) {
            gh.gridOffset(0, -moveSpeed); // Move down
            gh.repaint();
        }
        if(gh.io.WDown) {
            gh.gridOffset(0, moveSpeed);  // Move up
            gh.repaint();
        }
        
        // Ensure camera stays within bounds
        gh.outOfBoundsCorrection();
    }

    /**
     * Main game loop thread
     */
    @Override
    public void run() {
        Thread producer = new Thread(() -> {
            synchronized (lock) {
                while (true) { 
                    tick();  // Update game state
                    try {
                        lock.wait(16);  // ~60 FPS (16ms per frame)
                    } catch (InterruptedException e) {}
                }
            }
        });
        producer.start();
    }

    /**
     * Moves a character to a target hexagon using A* pathfinding
     * @param h Target hexagon
     * @param c Character to move
     */
    public void moveCharacter(Hexagon h, entities.Character c) {
        if (h == null || c == null) return;
        
        // Calculate path using A*
        gh.path = AStar.run(c.getTile(), h, gh, false);
        // Update character's position
        c.setTile(h);
    }

    /**
     * Adds multiple entities to initiative tracking
     * @param entities List of entities to add
     */
    public void initiative(ArrayList<Entity> entities) {
        if (entities == null) return;

        for (Entity entity : entities) {
            initiative(entity);
        }
        sortInitiative();
        updateInitiativeMarkers();
    }

    /**
     * Adds a single entity to initiative tracking
     * @param entity Entity to add
     */
    public void initiative(Entity entity) {
        if (entity == null) return;

        if (!init.containsKey(entity)) {
            double randomValue = Math.random();
            // Assign random initiative value
            init.put(entity, Math.random());
            
            // Create visual marker above entity
            Point coords = new Point((int) entity.getOccupiedTiles().getFirst().getCenter().getX(),
                                     (int) entity.getOccupiedTiles().getFirst().getCenter().getY() - gh.hexSize);
            Marker m = new Marker(0, coords, 1, false);
            m.attachTo(entity);
            m.setDebugValue(randomValue);
            initMarkers.add(m);
            gh.addMarker(m);
        }
    }

    /**
     * Updates all initiative markers' appearance based on current turn order
     */
    public void updateInitiativeMarkers() {
        ArrayList<Entity> order = getInitiativeOrder();
        for (int i = 0; i < order.size(); i++) {
            Entity entity = order.get(i);
            for (Marker m : initMarkers) {
                if (m.getAttachedEntity() == entity) {
                    m.setStat(i);  // Show turn order position

                    if (init.containsKey(entity))
                        m.setDebugValue(init.get(entity));

                    // Color coding:
                    if (i == currentTurnIndex) {
                        m.setColor(Color.GREEN);      // Current turn
                    } else if (i == (currentTurnIndex + 1) % order.size()) {
                        m.setColor(new Color(0xF9A801)); // Next turn (orange)
                    } else {
                        m.setColor(Color.RED);       // Other turns
                    }
                    break;
                }
            }
        }
        gh.repaint();
    }

    /**
     * Removes an entity from initiative tracking
     * @param entity Entity to remove
     */
    public void removeFromInitiative(Entity entity) {
        if (entity == null || !init.containsKey(entity)) return;

        init.remove(entity);

        // Find and remove associated marker
        Marker markerToRemove = null;
        for (Marker m : initMarkers) {
            if (m.getAttachedEntity() == entity) {
                markerToRemove = m;
                break;
            }
        }
        if (markerToRemove != null) {
            initMarkers.remove(markerToRemove);
            gh.markers.remove(markerToRemove);
        }

        // Handle turn order adjustments
        if (entity == currentTurn) {
            if (init.isEmpty()) {
                currentTurn = null;
                currentTurnIndex = 0;
            } else {
                nextTurn();  // Advance turn if removed entity was current
            }
        } else if (currentTurnIndex > 0 && 
                   getInitiativeOrder().indexOf(currentTurn) < currentTurnIndex) {
            currentTurnIndex--;  // Adjust index if needed
        }

        updateInitiativeMarkers();
        gh.repaint();
    }

    /**
     * Sorts initiative order from highest to lowest initiative value
     */
    public void sortInitiative() {
        init = init.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))  // Descending sort
            .collect(
                java.util.stream.Collectors.toMap(
                    java.util.Map.Entry::getKey,
                    java.util.Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    java.util.LinkedHashMap::new  // Preserves insertion order
                )
            );
        if (!initMarkers.isEmpty()) {
            updateInitiativeMarkers();
        }
    }

    /**
     * Toggles visibility of initiative markers
     */
    public void toggleShowInitiative() {
        for (Marker m : initMarkers) {
            m.toggleVisible();
        }
        gh.repaint();
    }

    /**
     * Advances to the next turn in initiative order
     */
    public void nextTurn() {
        ArrayList<Entity> order = getInitiativeOrder();
        if (order.isEmpty()) return;

        // Circular advancement through initiative order
        currentTurnIndex = (currentTurnIndex + 1) % order.size();
        currentTurn = order.get(currentTurnIndex);
        updateInitiativeMarkers();
    }

    /**
     * @return Current initiative order as a list of entities
     */
    public ArrayList<Entity> getInitiativeOrder() {
        return new ArrayList<>(init.keySet());
    }

    /**
     * @return Entity whose turn it currently is
     */
    public Entity getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Checks if an entity is in the initiative tracker
     * @param entity Entity to check
     * @return True if entity is being tracked
     */
    public boolean isInInitiative(Entity entity) {
        return init.containsKey(entity);
    }

    /**
     * Clears all initiative tracking data
     */
    public void clearInitiative() {
        for (Marker m : initMarkers) {
            gh.markers.remove(m);
        }
        initMarkers.clear();
        init.clear();
        currentTurn = null;
        currentTurnIndex = 0;
        gh.repaint();
    }
}