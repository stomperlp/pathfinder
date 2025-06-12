package calc;

import entities.Character;
import entities.Entity;
import fx.Hexagon;
import java.awt.Point;
import java.util.*;
import main.GraphicsHandler;

public class AStar
{

    public static ArrayList<Hexagon> run(Hexagon start, Hexagon end, GraphicsHandler gh, boolean ignoreObstacles) {

        if (start == null || end == null) return null;

        PriorityQueue<HexNode> openList = new PriorityQueue<>((a, b) ->
            Double.compare(a.fCost, b.fCost));
        HashSet<Point> closedList = new HashSet<>();
        HashMap<Point, Point> cameFrom = new HashMap<>();
        HashMap<Point, Double> gCost   = new HashMap<>();

        HexNode startNode = new HexNode(start, 0, heuristic(start, end));
        openList.add(startNode);
        gCost.put(start.getGridPoint(), 0.0);

        while (!openList.isEmpty()) {
            HexNode current = openList.poll();
            Hexagon currentHex = current.hex;
            Point currentPoint = currentHex.getGridPoint();

            if (currentPoint.equals(end.getGridPoint())) {
                if(gh.debugMode) 
                    gh.consol.addLogMessage("Shortest path is " + current.gCost + " tiles long.");

                    return reconstructPath(start, end, cameFrom, gh);
            }

            closedList.add(currentPoint);

            Hexagon[] neighbors = getNeighbors(currentHex, gh);
            for (Hexagon neighbor : neighbors) {
                if (neighbor == null) continue;

                if (isOccupiedByEntity(neighbor, start, gh) && !ignoreObstacles) continue;

                Point neighborPoint = neighbor.getGridPoint();

                if (closedList.contains(neighborPoint)) continue;

                double tentativeGCost = gCost.get(currentPoint) + 1;

                Double neighborGCost = gCost.get(neighborPoint);
                if (neighborGCost == null || tentativeGCost < neighborGCost) {
                    cameFrom.put(neighborPoint, currentPoint);
                    gCost.put(neighborPoint, tentativeGCost);

                    double fCost = tentativeGCost + heuristic(neighbor, end);

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
        if(gh.debugMode)
            gh.consol.addLogMessage("No path found");
        return null;
    }

    public static ArrayList<Hexagon> range(Hexagon center, int speed, entities.Character movingCharacter, GraphicsHandler gh) {

        if(speed == 0 || center == null) return new ArrayList<>();

        ArrayList<Hexagon> available       = new ArrayList<>();
        ArrayList<Hexagon> currentHexagons = new ArrayList<>();
        currentHexagons.add(center);

        for (int i = 0; i < speed; i++) {
            ArrayList<Hexagon> nextHexagons = new ArrayList<>();
            for (Hexagon h : currentHexagons) {
                for (Hexagon hex : getNeighbors(h, gh)) {
                    if (hex != null && !available.contains(hex) && 
                        hex != center && !nextHexagons.contains(hex)) {

                        boolean isValid = true;

                        if (movingCharacter != null) {
                            ArrayList<Hexagon> targetTiles = Character.getOccupiedTiles(hex, movingCharacter.getSize(), gh);

                            for (Hexagon targetTile : targetTiles) {
                                boolean isCurrentlyOccupiedByMovingChar = false;
                                for (Hexagon currentTile : movingCharacter.getOccupiedTiles()) {
                                    if (targetTile.getGridPoint().equals(currentTile.getGridPoint())) {
                                        isCurrentlyOccupiedByMovingChar = true;
                                        break;
                                    }
                                }

                                if (!isCurrentlyOccupiedByMovingChar) {
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

    public static Hexagon[] getNeighbors(Hexagon hex, GraphicsHandler gh) {

        if (hex == null || gh == null) return new Hexagon[0];

        Point key = hex.getGridPoint();

        Hexagon[] neighbors = new Hexagon[6];

        int hexX = (int) key.getX();
        int hexY = (int) key.getY();

        HashMap<Point, Hexagon> hexMap = new HashMap<>();
        for (Hexagon h : gh.getHexlist().values()) {
            hexMap.put(h.getGridPoint(), h);
        }

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

        for (int i = 0; i < 6; i++) {
            neighbors[i] = hexMap.get(neighborPositions[i]);
        }
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

        int ax = (int) a.getGridPoint().getX();
        int ay = (int) a.getGridPoint().getY();
        int bx = (int) b.getGridPoint().getX();
        int by = (int) b.getGridPoint().getY();

        int[] aCube = Calc.toCubeCoordinate(ax,ay);
        int[] bCube = Calc.toCubeCoordinate(bx,by);

        int ax_cube = aCube[0];
        int ay_cube = aCube[1];
        int az_cube = aCube[2];
        
        int bx_cube = bCube[0];
        int by_cube = bCube[1];
        int bz_cube = bCube[2];

        return Math.max(Math.max(
            Math.abs(ax_cube - bx_cube),
            Math.abs(ay_cube - by_cube)),
            Math.abs(az_cube - bz_cube));
    }

    public static boolean isOccupiedByEntity(Hexagon hex, GraphicsHandler gh) {
        return isOccupiedByEntity(hex, null, gh);
    }

    public static boolean isOccupiedByEntity(Hexagon hex, Hexagon sourceHex, GraphicsHandler gh) {
        if (hex == null || gh == null || gh.entities == null) {
            return true;
        }

        Entity sourceEntity = findEntityOccupyingHex(sourceHex, gh);

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

    // Helper method to find entity occupying a hex
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

    private static ArrayList<Hexagon> reconstructPath(Hexagon start, Hexagon end, 
                                                      HashMap<Point, Point> cameFrom, 
                                                      GraphicsHandler gh) {
        ArrayList<Hexagon> path = new ArrayList<>();
        Point current = end.getGridPoint();

        while (!current.equals(start.getGridPoint())) {

            Hexagon hex = gh.getHexlist().get(current.x, current.y);
            if (hex != null) {
                path.add(0, hex);
            }

            current = cameFrom.get(current);
            if (current == null) {
                break;
            }
        }

        if (!path.isEmpty()) {
            path.add(0, start);
        }

        return path;
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