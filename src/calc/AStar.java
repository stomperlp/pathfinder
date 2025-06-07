package calc;

import fx.Hexagon;
import java.awt.Point;
import java.util.*;
import main.GraphicsHandler;

public class AStar 
{
    private static final HashMap<Point, Point[]> neighborCache = new HashMap<>();

    public static ArrayList<Hexagon> range(Hexagon center, int speed, GraphicsHandler gh) {

        if(speed == 0 || center == null) return new ArrayList<>();

        ArrayList<Hexagon> available       = new ArrayList<>();
        ArrayList<Hexagon> currentHexagons = new ArrayList<>();
        currentHexagons.add(center);

        for (int i = 0; i < speed; i++) {
            ArrayList<Hexagon> nextHexagons = new ArrayList<>();
            for (Hexagon h : currentHexagons) {
                for (Hexagon hex : getNeighbors(h, gh)) {
                    if (hex != null   && !available.contains(hex) && 
                    hex != center && !nextHexagons.contains(hex)) {
                        available.add(hex);
                        nextHexagons.add(hex);
                    }
                }
            }
            currentHexagons = nextHexagons;
        }
        return available;
    }

    public static Hexagon[] getNeighbors(Hexagon hex, GraphicsHandler gh) {
        Point key = hex.getGridPoint();
        
        Hexagon[] neighbors = new Hexagon[6];

        // Return cached result if available
        if (neighborCache.containsKey(key)) {
            for(Point p : neighborCache.get(key)) {
                for (Hexagon h : gh.getHexlist().values()) {
                    if (h.getGridPoint().equals(p)) {
                        neighbors[Arrays.asList(neighborCache.get(key)).indexOf(p)] = h;
                    }
                }
            }
            return neighbors;
        }
        
        int hexX = (int) key.getX();
        int hexY = (int) key.getY();
        
        // Create a temporary map for quick lookup
        HashMap<Point, Hexagon> hexMap = new HashMap<>();
        for (Hexagon h : gh.getHexlist().values()) {
            hexMap.put(h.getGridPoint(), h);
        }
        
        // Define all possible neighbor positions
        Point[] neighborPositions;
        if (hexY % 2 == 0) {
            neighborPositions = new Point[] {
                new Point(hexX-1, hexY  ),
                new Point(hexX-1, hexY+1),
                new Point(hexX  , hexY+1),
                new Point(hexX+1, hexY  ),
                new Point(hexX  , hexY-1),
                new Point(hexX-1, hexY-1)
            };
        } else {
            neighborPositions = new Point[] {
                new Point(hexX-1, hexY  ),
                new Point(hexX  , hexY+1),
                new Point(hexX+1, hexY+1),
                new Point(hexX+1, hexY  ),
                new Point(hexX+1, hexY-1),
                new Point(hexX  , hexY-1)
            };
        }
        
        // Look up each position
        for (int i = 0; i < 6; i++) {
            neighbors[i] = hexMap.get(neighborPositions[i]);
        }
        
        // Cache the result
        neighborCache.put(key, neighborPositions);
        return neighbors;
    }

    public static Hexagon getNeighbor(Hexagon hex, int neighborNR, GraphicsHandler gh) {

        Hexagon neighbor;

        int hexX = (int) hex.getGridPoint().getX();
        int hexY = (int) hex.getGridPoint().getY();

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

    public static double heuristic(Hexagon a, Hexagon b) {

        // Proper hexagonal distance for odd-q offset coordinates
        int ax = (int) a.getGridPoint().getX();
        int ay = (int) a.getGridPoint().getY();
        int bx = (int) b.getGridPoint().getX();
        int by = (int) b.getGridPoint().getY();
        
        // Convert to cube coordinates
        int a_offset = (ay >> 1); // Equivalent to ay/2 for offset adjustment
        int ax_cube = ax - a_offset;
        int az_cube = ay;
        int ay_cube = -ax_cube - az_cube;
        
        int b_offset = (by >> 1); // Equivalent to by/2 for offset adjustment
        int bx_cube = bx - b_offset;
        int bz_cube = by;
        int by_cube = -bx_cube - bz_cube;
        
        // Hex distance in cube coordinates is the maximum of the absolute differences
        return Math.max(Math.max(
            Math.abs(ax_cube - bx_cube),
            Math.abs(ay_cube - by_cube)),
            Math.abs(az_cube - bz_cube));
    }

    public static void run(Hexagon start, Hexagon end, GraphicsHandler gh)
    {
        if (start == null || end == null) return;

        // Open list (hexagons to be evaluated)
        PriorityQueue<HexNode> openList = new PriorityQueue<>((a, b) ->
            Double.compare(a.fCost, b.fCost));

        // Closed list (already evaluated hexagons)
        HashSet<Point> closedList = new HashSet<>();

        // Map to track the best path
        HashMap<Point, Point> cameFrom = new HashMap<>();

        // Map to store g costs
        HashMap<Point, Double> gCost = new HashMap<>();

        // Initial node
        HexNode startNode = new HexNode(start, 0, heuristic(start, end));
        openList.add(startNode);
        gCost.put(start.getGridPoint(), 0.0);

        while (!openList.isEmpty()) {
            HexNode current = openList.poll();
            Hexagon currentHex = current.hex;
            Point currentPoint = currentHex.getGridPoint();

            // If we reached the end, reconstruct and return the path
            if (currentPoint.equals(end.getGridPoint())) {
                // Path found - could reconstruct here if needed
                System.out.println("Path found!");
                System.out.println("Shortest path is " + current.gCost + " tiles long.");
                return;
            }

            closedList.add(currentPoint);

            // Check all neighbors
            Hexagon[] neighbors = getNeighbors(currentHex, gh);
            for (Hexagon neighbor : neighbors) {
                if (neighbor == null) continue;

                Point neighborPoint = neighbor.getGridPoint();

                // Skip already evaluated hexagons
                if (closedList.contains(neighborPoint)) continue;

                // Calculate tentative g cost
                double tentativeGCost = gCost.get(currentPoint) + 1; // Assuming uniform cost

                // If neighbor not in open list or new path is better
                Double neighborGCost = gCost.get(neighborPoint);
                if (neighborGCost == null || tentativeGCost < neighborGCost) {
                    // Update this path as the best path to neighbor
                    cameFrom.put(neighborPoint, currentPoint);
                    gCost.put(neighborPoint, tentativeGCost);

                    double fCost = tentativeGCost + heuristic(neighbor, end);

                    // Add to open list if not already there
                    boolean found = false;
                    for (HexNode node : openList) {
                        if (node.hex.getGridPoint().equals(neighborPoint)) {
                            found = true;
                            node.fCost = fCost;
                            break;
                        }
                    }

                    if (!found) {
                        openList.add(new HexNode(neighbor, tentativeGCost, fCost));
                    }
                }
            }
        }

        // No path found
        System.out.println("No path found");
    }

    private static class HexNode {
        Hexagon hex;
        double gCost;
        double fCost;

        public HexNode(Hexagon hex, double gCost, double fCost) {
            this.hex = hex;
            this.gCost = gCost;
            this.fCost = fCost;
        }
    }
}