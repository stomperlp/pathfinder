package calc;

import entities.Character;
import entities.Entity;
import fx.Hexagon;
import java.awt.Point;
import java.util.*;
import main.GraphicsHandler;

/**
 * A* Pathfinding implementation for hexagonal grids.
 * Contains methods for pathfinding and movement range calculation.
 */
public class AStar {

    /**
     * A* pathfinding algorithm to find the shortest path between two hexagons.
     * @param start The starting hexagon
     * @param end The target hexagon
     * @param gh GraphicsHandler containing game state
     * @param ignoreObstacles If true, will ignore entity collisions
     * @return List of hexagons representing the path, or null if no path exists
     */
    public static ArrayList<Hexagon> run(Hexagon start, Hexagon end, GraphicsHandler gh, boolean ignoreObstacles) {
        // Early exit if start or end is null
        if (start == null || end == null) return null;

        // Open list prioritizes nodes with lowest fCost
        PriorityQueue<HexNode> openList = new PriorityQueue<>((a, b) ->
            Double.compare(a.fCost, b.fCost));
        // Closed list tracks already evaluated nodes
        HashSet<Point> closedList = new HashSet<>();
        // Tracks the optimal path (node -> cameFrom)
        HashMap<Point, Point> cameFrom = new HashMap<>();
        // Tracks the cost from start to each node
        HashMap<Point, Double> gCost = new HashMap<>();

        // Initialize with start node
        HexNode startNode = new HexNode(start, 0, heuristic(start, end));
        openList.add(startNode);
        gCost.put(start.getGridPoint(), 0.0);

        while (!openList.isEmpty()) {
            HexNode current = openList.poll();
            Hexagon currentHex = current.hex;
            Point currentPoint = currentHex.getGridPoint();

            // Check if we've reached the destination
            if (currentPoint.equals(end.getGridPoint())) {
                if(gh.debugMode) 
                    gh.consol.addLogMessage("Shortest path is " + current.gCost + " tiles long.");
                return reconstructPath(start, end, cameFrom, gh);
            }

            closedList.add(currentPoint);

            // Evaluate all neighbors
            Hexagon[] neighbors = getNeighbors(currentHex, gh);
            for (Hexagon neighbor : neighbors) {
                if (neighbor == null) continue;

                // Skip if neighbor is occupied (unless ignoring obstacles)
                if (isOccupiedByEntity(neighbor, start, gh) && !ignoreObstacles) continue;

                Point neighborPoint = neighbor.getGridPoint();

                // Skip if already evaluated
                if (closedList.contains(neighborPoint)) continue;

                // Calculate tentative movement cost
                double tentativeGCost = gCost.get(currentPoint) + 1;

                // If this path to neighbor is better than any previous one
                Double neighborGCost = gCost.get(neighborPoint);
                if (neighborGCost == null || tentativeGCost < neighborGCost) {
                    cameFrom.put(neighborPoint, currentPoint);
                    gCost.put(neighborPoint, tentativeGCost);

                    double fCost = tentativeGCost + heuristic(neighbor, end);

                    // Update existing node in open list if present
                    boolean found = false;
                    for (HexNode node : openList) {
                        if (node.hex.getGridPoint().equals(neighborPoint)) {
                            found = true;
                            node.fCost = fCost;
                            break;
                        }
                    }

                    // Add to open list if not already there
                    if (!found) {
                        openList.add(new HexNode(neighbor, tentativeGCost, fCost));
                    }
                }
            }
        }
        
        if(gh.debugMode)
            gh.consol.addLogMessage("No path found");
        return null;
    }

    /**
     * Calculates all reachable hexagons within a given movement range.
     * @param center Starting hexagon
     * @param speed Maximum movement distance
     * @param movingCharacter The character moving (null for no collision checks)
     * @param gh GraphicsHandler containing game state
     * @return List of reachable hexagons
     */
    public static ArrayList<Hexagon> range(Hexagon center, int speed, Character movingCharacter, GraphicsHandler gh) {
        if(speed == 0 || center == null) return new ArrayList<>();

        ArrayList<Hexagon> available = new ArrayList<>();
        ArrayList<Hexagon> currentHexagons = new ArrayList<>();
        currentHexagons.add(center);

        // Expand outward for each movement point
        for (int i = 0; i < speed; i++) {
            ArrayList<Hexagon> nextHexagons = new ArrayList<>();
            for (Hexagon h : currentHexagons) {
                for (Hexagon hex : getNeighbors(h, gh)) {
                    if (hex != null && !available.contains(hex) && 
                        hex != center && !nextHexagons.contains(hex)) {

                        boolean isValid = true;

                        // Special handling for character movement with collision checks
                        if (movingCharacter != null) {
                            ArrayList<Hexagon> targetTiles = Character.getOccupiedTiles(hex, movingCharacter.getSize(), gh);

                            for (Hexagon targetTile : targetTiles) {
                                // Check if target tile is currently occupied by moving character
                                boolean isCurrentlyOccupiedByMovingChar = false;
                                for (Hexagon currentTile : movingCharacter.getOccupiedTiles()) {
                                    if (targetTile.getGridPoint().equals(currentTile.getGridPoint())) {
                                        isCurrentlyOccupiedByMovingChar = true;
                                        break;
                                    }
                                }

                                if (!isCurrentlyOccupiedByMovingChar) {
                                    // Check for collisions with other entities
                                    for (Entity entity : gh.entities) {
                                        if (entity != movingCharacter) {
                                            for (Hexagon occupiedTile : entity.getOccupiedTiles()) {
                                                if (targetTile.getGridPoint().equals(occupiedTile.getGridPoint())) {
                                                    isValid = false;
                                                    break;
                                                }
                                            }
                                            if (!isValid) break;
                                        }
                                    }
                                }
                                if (!isValid) break;
                            }
                        } else {
                            // Simple collision check for non-character movement
                            isValid = !isOccupiedByEntity(hex, center, gh);
                        }
                        
                        if (isValid) {
                            available.add(hex);
                            nextHexagons.add(hex);
                        }
                    }
                }
            }
            currentHexagons = nextHexagons;
        }
        return available;
    }

    /**
     * Gets all neighboring hexagons for a given hexagon.
     * @param hex The center hexagon
     * @param gh GraphicsHandler containing hexagon map
     * @return Array of 6 neighboring hexagons (some may be null)
     */
    public static Hexagon[] getNeighbors(Hexagon hex, GraphicsHandler gh) {
        if (hex == null || gh == null) return new Hexagon[0];

        Point key = hex.getGridPoint();
        Hexagon[] neighbors = new Hexagon[6];

        int hexX = (int) key.getX();
        int hexY = (int) key.getY();

        // Create map for quick lookup
        HashMap<Point, Hexagon> hexMap = new HashMap<>();
        for (Hexagon h : gh.getHexlist().values()) {
            hexMap.put(h.getGridPoint(), h);
        }

        // Offset patterns differ between even and odd rows
        Point[] neighborPositions;
        if (hexY % 2 == 0) {  // Even row
            neighborPositions = new Point[] {
                new Point(hexX-1, hexY  ),
                new Point(hexX-1, hexY+1),
                new Point(hexX  , hexY+1),
                new Point(hexX+1, hexY  ),
                new Point(hexX  , hexY-1),
                new Point(hexX-1, hexY-1)
            };
        } else {  // Odd row
            neighborPositions = new Point[] {
                new Point(hexX-1, hexY  ),
                new Point(hexX  , hexY+1),
                new Point(hexX+1, hexY+1),
                new Point(hexX+1, hexY  ),
                new Point(hexX+1, hexY-1),
                new Point(hexX  , hexY-1)
            };
        }

        // Lookup each neighbor position
        for (int i = 0; i < 6; i++) {
            neighbors[i] = hexMap.get(neighborPositions[i]);
        }
        return neighbors;
    }

    /**
     * Gets a specific neighbor of a hexagon.
     * @param hex The center hexagon
     * @param neighborNR Numbered position of neighbor (1-6)
     * @param gh GraphicsHandler containing hexagon map
     * @return The requested neighbor hexagon, or null if doesn't exist
     */
    public static Hexagon getNeighbor(Hexagon hex, int neighborNR, GraphicsHandler gh) {
        Hexagon neighbor;
        int hexX = (int) hex.getGridPoint().getX();
        int hexY = (int) hex.getGridPoint().getY();

        // Different neighbor positions for even/odd rows
        if (hexY % 2 == 0) {
            neighbor = switch(neighborNR) {
                case 1  -> gh.getHexlist().get(hexX-1, hexY  );
                case 2  -> gh.getHexlist().get(hexX-1, hexY+1);
                case 3  -> gh.getHexlist().get(hexX  , hexY+1);
                case 4  -> gh.getHexlist().get(hexX+1, hexY  );
                case 5  -> gh.getHexlist().get(hexX  , hexY-1);
                case 6  -> gh.getHexlist().get(hexX-1, hexY-1);
                default -> null;
            };
        } else {
            neighbor = switch(neighborNR) {
                case 1  -> gh.getHexlist().get(hexX-1, hexY  );
                case 2  -> gh.getHexlist().get(hexX  , hexY+1);
                case 3  -> gh.getHexlist().get(hexX+1, hexY+1);
                case 4  -> gh.getHexlist().get(hexX+1, hexY  );
                case 5  -> gh.getHexlist().get(hexX+1, hexY-1);
                case 6  -> gh.getHexlist().get(hexX  , hexY-1);
                default -> null;
            };
        }
        return neighbor;
    }

    /**
     * Heuristic function for A* (hexagonal Manhattan distance).
     * @param a Starting hexagon
     * @param b Target hexagon
     * @return Distance estimate between the two hexagons
     */
    public static double heuristic(Hexagon a, Hexagon b) {
        // Convert axial to cube coordinates
        int ax = (int) a.getGridPoint().getX();
        int ay = (int) a.getGridPoint().getY();
        int bx = (int) b.getGridPoint().getX();
        int by = (int) b.getGridPoint().getY();

        int[] aCube = Calc.toCubeCoordinate(ax,ay);
        int[] bCube = Calc.toCubeCoordinate(bx,by);

        // Calculate cube coordinate distance
        return Math.max(Math.max(
            Math.abs(aCube[0] - bCube[0]),
            Math.abs(aCube[1] - bCube[1])),
            Math.abs(aCube[2] - bCube[2]));
    }

    /**
     * Checks if a hexagon is occupied by any entity.
     * @param hex Hexagon to check
     * @param gh GraphicsHandler containing entity list
     * @return True if occupied, false otherwise
     */
    public static boolean isOccupiedByEntity(Hexagon hex, GraphicsHandler gh) {
        return isOccupiedByEntity(hex, null, gh);
    }

    /**
     * Checks if a hexagon is occupied by any entity except the source.
     * @param hex Hexagon to check
     * @param sourceHex Hexagon that may contain the source entity
     * @param gh GraphicsHandler containing entity list
     * @return True if occupied, false otherwise
     */
    public static boolean isOccupiedByEntity(Hexagon hex, Hexagon sourceHex, GraphicsHandler gh) {
        if (hex == null || gh == null || gh.entities == null) {
            return true;
        }

        // Find entity occupying source hex (if any)
        Entity sourceEntity = findEntityOccupyingHex(sourceHex, gh);

        // Check all entities except the source entity
        for (Entity entity : gh.entities) {
            if (entity == sourceEntity) continue;

            for (Hexagon occupiedTile : entity.getOccupiedTiles()) {
                if (occupiedTile != null && hex.equals(occupiedTile)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Helper method to find which entity occupies a hexagon.
     * @param hex Hexagon to check
     * @param gh GraphicsHandler containing entity list
     * @return The occupying entity, or null if none
     */
    private static Entity findEntityOccupyingHex(Hexagon hex, GraphicsHandler gh) {
        if (hex == null || gh == null || gh.entities == null) {
            return null;
        }

        for (Entity entity : gh.entities) {
            for (Hexagon occupiedTile : entity.getOccupiedTiles()) {
                if (occupiedTile != null && hex.getGridPoint().equals(occupiedTile.getGridPoint())) {
                    return entity;
                }
            }
        }
        return null;
    }

    /**
     * Reconstructs the path from start to end using the cameFrom map.
     * @param start Starting hexagon
     * @param end Target hexagon
     * @param cameFrom Map of node origins
     * @param gh GraphicsHandler containing hexagon map
     * @return List of hexagons representing the path
     */
    private static ArrayList<Hexagon> reconstructPath(Hexagon start, Hexagon end, 
                                                    HashMap<Point, Point> cameFrom, 
                                                    GraphicsHandler gh) {
        ArrayList<Hexagon> path = new ArrayList<>();
        Point current = end.getGridPoint();

        // Work backwards from end to start
        while (!current.equals(start.getGridPoint())) {
            Hexagon hex = gh.getHexlist().get(current.x, current.y);
            if (hex != null) {
                path.add(0, hex);  // Add to front to maintain order
            }

            current = cameFrom.get(current);
            if (current == null) {
                break;
            }
        }

        // Add start node if path exists
        if (!path.isEmpty()) {
            path.add(0, start);
        }

        return path;
    }

    /**
     * Helper class for A* algorithm nodes.
     * Stores hexagon, movement cost (g), and total cost (f = g + h).
     */
    private static class HexNode {
        Hexagon hex;
        double gCost;  // Cost from start to this node
        double fCost;  // Total cost (g + heuristic)

        public HexNode(Hexagon hex, double gCost, double fCost) {
            this.hex = hex;
            this.gCost = gCost;
            this.fCost = fCost;
        }
    }
}