package tools;

import java.awt.Image;
import java.awt.Rectangle;

public class Tool {

	public static final int DRAG_MODE   = 0;
	public static final int LENGTH_MODE = 1;
	public static final int AREA_MODE   = 2;
	public static final int LINE_MODE   = 3;
	public static final int CONE_MODE   = 4;

    private Rectangle hitbox;
    private Image icon;
    private int toolMode;
    private final int sort;
    
    public Tool(Image icon, int toolMode, int sort) {
        this.icon = icon;
        this.toolMode = toolMode;
        this.sort = sort;
        this.hitbox = new Rectangle(1,sort*32-1,30,30);
    }
    
    public Image getIcon() {
        return icon;
    }
    public void updateHitbox(int s) {

        this.hitbox = new Rectangle(1,sort*s-1,s-2,s-2);
    }
    public void setIcon(Image icon) {
        this.icon = icon;
    }
    public int getToolMode() {
        return toolMode;
    }
    public void setToolMode(int toolMode) {
        this.toolMode = toolMode;
    }

    public void moveBy(int x, int y) {
        hitbox.translate(x, y);
    }
    public Rectangle getHitbox() {
        return hitbox;
    }
}
