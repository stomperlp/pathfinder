package tools;

import java.awt.Image;
import java.awt.Rectangle;

public class Tool {

	public static final int DRAG_MODE   = 0;
	public static final int LENGTH_MODE = 1;
	public static final int HITBOX_MODE = 2;
	public static final int MARK_MODE   = 3;

    private Image icon;
    private int toolMode;
    private Rectangle hitbox;
    
    public Tool(Image icon, int toolMode, int sort) {
        this.icon = icon;
        this.toolMode = toolMode;
        this.hitbox = new Rectangle(1,sort*32-1,30,30);
    }
    
    public Image getIcon() {
        return icon;
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
