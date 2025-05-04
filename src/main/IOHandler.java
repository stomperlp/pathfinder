package main;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;


public class IOHandler extends MouseAdapter{
	
	private final int DRAG_MODE = 0;

	protected Path2D currentHexagon;
	protected GraphicsHandler gh;
	protected int count = 0;
	protected int mode = 0;

	public IOHandler(GraphicsHandler gh){
		this.gh = gh;
	}

	//Raw inputs from listeners
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

	public void mouseWheelMoved(MouseWheelEvent e) {
		//logMousePos(e);

		int notches = e.getWheelRotation();

		if (notches < 0) {

		} else {

		}
		gh.zoom(notches, e.getPoint());

			
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		switch (mode) 
		{
			case DRAG_MODE -> gh.drag(e);

		}
	}
	void keyTyped(KeyEvent e) {

	}
	void keyPressed(KeyEvent e) {
        switch(e.getKeyChar()) {
			case 'w' -> WPressed();
		}
	}
	void keyReleased(KeyEvent e) {
        
    }






	//spesific inputs
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
	private void APressed() {
		
	}
	private void DPressed() {

	}
	private void SPressed() {

	}
	private void WPressed() {

	}
	private void MMBReleased() {

	}
	private void RMBReleased() {

	}
	

	//Other Methods
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
		ArrayList<Path2D> hexlist = gh.getHexlist();


		for(Path2D hex : hexlist)
		{
			if(hex.contains(e.getPoint()))
			{
				currentHexagon = hex;
				gh.drawSelectedTile(currentHexagon);
			}
		}
	}

    
}
