package tools;

import fx.Hexagon;
import main.GraphicsHandler;

public class AStar 
{
    public static void run(Hexagon start, Hexagon end)
    {
        
    }

    public static Hexagon[] getNeighbors(Hexagon hex, GraphicsHandler gh)
    {
        Hexagon[] neighbors = new Hexagon[6];

        int hexX = (int) hex.getGridPoint().getX();
        int hexY = (int) hex.getGridPoint().getY();

        for(Hexagon h: gh.getHexlist())
        {
            if(hex.getGridPoint().getX() % 2 == 0) {
                int hX = (int) h.getGridPoint().getX();
                int hY = (int) h.getGridPoint().getY();

                if(hX == hexX-2 && hY == hexY){
                    neighbors[0] = h;
                }
                else if(hX == hexX-1 && hY == hexY){
                    neighbors[1] = h;
                }
                else if(hX == hexX+1 && hY == hexY){
                    neighbors[2] = h;
                }
                else if(hX == hexX+2 && hY == hexY){
                    neighbors[3] = h;
                }
                else if(hX == hexX+1 && hY == hexY-1){
                    neighbors[4] = h;
                }
                else if(hX == hexX-1 && hY == hexY-1){
                    neighbors[5] = h;
                }
            }
            else {
                    int hX = (int) h.getGridPoint().getX();
                    int hY = (int) h.getGridPoint().getY();
    
                    if(hX == hexX-2 && hY == hexY){
                        neighbors[0] = h;
                    }
                    else if(hX == hexX-1 && hY == hexY+1){
                        neighbors[1] = h;
                    }
                    else if(hX == hexX+1 && hY == hexY+1){
                        neighbors[2] = h;
                    }
                    else if(hX == hexX+2 && hY == hexY){
                        neighbors[3] = h;
                    }
                    else if(hX == hexX+1 && hY == hexY){
                        neighbors[4] = h;
                    }
                    else if(hX == hexX-1 && hY == hexY){
                        neighbors[5] = h;
                    }
                }
        }
        return neighbors;
    }
    
}
