package main;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class IOHandler extends MouseAdapter{
	
	private final int DRAG_MODE = 0;

	protected Polygon currentHexagon;
	protected GraphicsHandler gh;
	protected int count = 0;
	protected int mode = 0;

	public IOHandler(GraphicsHandler gh){
		this.gh = gh;
	}
    public void mousePressed(MouseEvent e) 
	{
		switch (e.getButton()) 
		{
			case 1 -> LMBPressed(e);

			case 2 -> MMBPressed();

			case 3 -> RMBPressed();

			default -> {}
		}
	}


	public void mouseReleased(MouseEvent e) 
	{
		switch (e.getButton()) 
		{
			case 1 -> LMBReleased(e);

			case 2 -> MMBReleased();

			case 3 -> RMBReleased();

			default -> {}
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		//logMousePos(e);
		checkCurrentHexagon(e);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		switch (mode) 
		{
			case DRAG_MODE -> gh.drag(e);

		}
	}

	private void LMBPressed(MouseEvent e) {
		switch (mode) 
		{
			case DRAG_MODE -> gh.dragStart = e.getPoint();

		}
	}
	
	private void MMBPressed() {

	}
	private void RMBPressed() {

	}
	private void LMBReleased(MouseEvent e) {
		switch (mode) 
		{
			case DRAG_MODE -> gh.dragStart = null;

		}
	}
	private void MMBReleased() {

	}
	private void RMBReleased() {

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
				gh.drawSelectedTile(currentHexagon);
			}
		}
	}
}
