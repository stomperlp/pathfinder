package main;
import fx.Marker;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class IOHandler extends MouseAdapter{
	
	private final int DRAG_MODE = 0;

	protected Path2D currentHexagon;
	protected GraphicsHandler gh;
	protected int count = 0;
	protected int mode = 0;
	protected boolean isShiftDown = false;
	protected  boolean isCtrlDown = false;
	protected Marker mouseLog;


	public IOHandler(GraphicsHandler gh){
		this.gh = gh;
	}

	public File openFileBrowser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "PNG Images (*.png)", "png");
        fileChooser.setFileFilter(filter);
        // Disable the "All files" option
        fileChooser.setAcceptAllFileFilterUsed(false);
        // Show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(gh);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile;
        }
        return null;
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
		if (gh.debugMode) logMousePos(e);
		checkCurrentHexagon(e);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		gh.zoom(notches, e.getPoint());
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (gh.debugMode) logMousePos(e);
		switch (mode) 
		{
			case DRAG_MODE -> gh.drag(e);
		}
	}
	void keyTyped(KeyEvent e) {

        switch(e.getKeyCode()) {
		}
	}
	void keyPressed(KeyEvent e) {
        if (gh.consol.isActive())
			//Any only active in consol
			switch(e.getKeyCode()) {
				case KeyEvent.VK_UP -> gh.consol.arrowUp();
                case KeyEvent.VK_DOWN -> gh.consol.arrowDown();
			}
		else switch(e.getKeyCode()) {
			//Any only active out of consol
			case 'W' -> WPressed();
		}
		//Any always active
		switch(e.getKeyCode()) {
			case KeyEvent.SHIFT_DOWN_MASK -> isShiftDown = true;
			case KeyEvent.CTRL_DOWN_MASK -> isCtrlDown = true;
			case KeyEvent.VK_ENTER -> enterPressed(e);
		}
	}
	void keyReleased(KeyEvent e) {
        
        switch(e.getKeyCode()) {
			case KeyEvent.SHIFT_DOWN_MASK -> isShiftDown = false;
			case KeyEvent.CTRL_DOWN_MASK -> isCtrlDown = false;
		}
    }

	//spesific inputs ----------------------------------------
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
	private void enterPressed(KeyEvent e) {
		e.consume();
		if (e.isControlDown()) { 
			gh.toggleConsol();
			return;
		}
		if(gh.consol.isActive()) gh.consol.command(gh.consol.getText());
	}
	private void WPressed() {

		System.out.println("[w]");
	}
	private void MMBReleased() {

	}
	private void RMBReleased() {

	}
	

	//Other Methods
	public void logMousePos(MouseEvent e) {
		if (mouseLog == null) {
			mouseLog = new Marker(new Point(0,0), Marker.COORDINATES, true);
			gh.markers.add(mouseLog);
		}
		mouseLog.moveTo(e.getPoint());
		gh.repaint();
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
