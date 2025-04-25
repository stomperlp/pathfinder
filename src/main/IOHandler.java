package main;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class IOHandler extends MouseAdapter{
	Polygon currentHexagon;
	GraphicsHandler gh;
	int count = 0;

	public IOHandler(GraphicsHandler gh){
		this.gh = gh;
	}
    public void mouseEvent(MouseEvent e) 
	{
		switch (e.getButton()) 
		{
			case 1 -> leftMB();

			case 2 -> middleMB();

			case 3 -> rightMB();
			default -> {
                }
		}
	}
	private void leftMB() {

	}
	private void middleMB() {

	}
	private void rightMB() {

	}
	@Override
	public void mouseMoved(MouseEvent e) {
		//logMousePos(e);
		checkCurrentHexagon(e);

		gh.drawHexagon2(gh.g2d, e.getX(), e.getY(), Color.RED);
	}
	public void logMousePos(MouseEvent e) {
		System.out.println("X: " + e.getX() + ", Y: " + e.getY());	
	}
	public void checkCurrentHexagon(MouseEvent e) {
		if (currentHexagon != null) {
			if(currentHexagon.contains(e.getPoint()) )
			{
				return;
			}
		}
		ArrayList<Polygon> hexlist = gh.getHexlist();


		for(Polygon hex : hexlist)
		{
			if(hex.contains(e.getPoint()))
			{
				currentHexagon = hex;
				gh.drawHexagon2(gh.g2d, e.getX(), e.getY(), Color.RED);
			}
		}
	}
}
