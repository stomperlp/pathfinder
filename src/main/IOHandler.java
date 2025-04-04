package main;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IOHandler extends MouseAdapter{
    public void mouseEvent(MouseEvent e) 
	{
		switch (e.getButton()) 
		{
			case 1:
				//moveSelCreature(new Point(e.getX(), e.getY()));
				break;

			case 3:
				//this.selCreature = selectCreature(new Point(e.getX(), e.getY()));

			default: break;
		}
	}
	public void mouseMoved(MouseEvent e) {
		logMousePos(e);
	}
	public void logMousePos(MouseEvent e) {
		System.out.println("X: " + e.getX() + ", Y: " + e.getY());	
	}
}
