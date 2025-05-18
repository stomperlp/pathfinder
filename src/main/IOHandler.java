package main;
import entities.Character;
import entities.Entity;
import fx.*;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import tools.AStar;


public class IOHandler extends MouseAdapter {
	
	private static final int DRAG_MODE 	 = 0;
	private static final int LENGTH_MODE = 1;
	private static final int HITBOX_MODE = 2;
	private static final int MARK_MODE   = 3;

	protected Hexagon currentHexagon;
	protected final GraphicsHandler gh;
	protected int count = 0;
	protected int mode  = 0;
	protected boolean isShiftDown = false;
	protected boolean isCtrlDown  = false;
	protected Marker mouseLog;

	protected boolean ADown;
	protected boolean DDown;
	protected boolean SDown;
	protected boolean WDown;

	private boolean hasSelectedEntity;
	private Point mousePos;
	private boolean LMBDown;


	public IOHandler(GraphicsHandler gh){
		this.gh = gh;
	}

	// File browser for opening images
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
    @Override
    public void mousePressed(MouseEvent e) 
	{
		switch (e.getButton()) 
		{
			case MouseEvent.BUTTON1 -> {
				LMBDown = true;
				if (!isCtrlDown && !isShiftDown){
					if (!isShiftDown)
						gh.selectedEntityTiles.clear();
					gh.selectedTiles.clear();
				}
				currentHexagon = gh.gm.findClosestHexagon(mousePos);
				selectEntity();
				selectTile();
			}
			case MouseEvent.BUTTON2 -> {}
			case MouseEvent.BUTTON3 -> {
				
				switch (mode) 
				{
					case DRAG_MODE -> gh.dragStart = mousePos;
				}
			}
			default -> {}
		}
	}

    @Override
	public void mouseReleased(MouseEvent e) 
	{
		switch (e.getButton()) 
		{
			case MouseEvent.BUTTON1 -> {
				LMBDown = false;
			}
			case MouseEvent.BUTTON2 -> {}
			case MouseEvent.BUTTON3 -> {
				switch (mode) 
				{
					case DRAG_MODE -> gh.dragStart = null;
				}
			}
			default -> {}
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (gh.debugMode) logMousePos(e);
		mousePos = e.getPoint();
		currentHexagon = gh.gm.findClosestHexagon(mousePos);
		gh.drawTileUnderMouse(currentHexagon);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePos = e.getPoint();
		if (gh.debugMode) logMousePos(e);
		
		currentHexagon = gh.gm.findClosestHexagon(e.getPoint());
		
		if (isCtrlDown && !gh.selectedTiles.contains(currentHexagon) && LMBDown) {
			gh.addSelectedTile(currentHexagon);
			selectEntity();
		}
		switch (mode) 
		{
			case DRAG_MODE -> gh.drag(e);
		}
		
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if(isCtrlDown) {
			int base = gh.consol.getFont().getSize();
			gh.consol.setFontSize(base-notches);
			
			gh.revalidate();
			gh.repaint();
		} else {
			gh.zoom(notches, e.getPoint());
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
			case 'W' -> {
				WDown = true;
			}
			case 'A' -> {
				ADown = true;
			}
			case 'S' -> {
				SDown = true;
			} 
			case 'D' -> {
				DDown = true;
			}
			case 'H' -> {
				if (gh.selectedTiles.get(0) != null) {
					Hexagon[] neighbors = AStar.getNeighbors(gh.selectedTiles.get(0), gh);
					if (neighbors != null) {
						for (Hexagon h : neighbors) {
							if (h != null) {
								System.out.println(h.getGridPoint());
							}
						}
					}
				} else {
					System.out.println("No tile selected.");
				}
			}
		}
		//Any always active
		switch(e.getKeyCode()) {
			case KeyEvent.VK_SHIFT 	 -> isShiftDown = true;
			case KeyEvent.VK_CONTROL -> isCtrlDown = true;
			case KeyEvent.VK_ENTER   -> {
				e.consume();
				if (isCtrlDown) { 
					gh.toggleConsol();
					return;
				}
				if(gh.consol.isActive()) gh.consol.command(gh.consol.getText());
			}
		}
	}
	void keyReleased(KeyEvent e) {
		if (gh.consol.isActive())
			//Any only active in consol
			switch(e.getKeyCode()) {
				
			}
		else switch(e.getKeyCode()) {
			//Any only active out of consol
			case 'W' -> {
				WDown = false;
			}
			case 'A' -> {
				ADown = false;
			}
			case 'S' -> {
				SDown = false;
			}
			case 'D' -> {
				DDown = false;       
			}
		}
		//Any Always active
		switch(e.getKeyCode()) {
			case KeyEvent.VK_SHIFT   -> isShiftDown = false;
			case KeyEvent.VK_CONTROL -> isCtrlDown  = false;
		}
    }
	//Other Methods ----------------------------------------------

	public void logMousePos(MouseEvent e) {
		if (mouseLog == null) {
			mouseLog = new Marker(new Point(0,0), Marker.COORDINATES, true);
			gh.markers.add(mouseLog);
		}
		mouseLog.moveTo(e.getPoint());
		gh.repaint();
	}
	public void selectEntity(){
		if(isShiftDown) return;
		if (!gh.selectedEntityTiles.isEmpty()) {
			for(Hexagon h : gh.selectedEntityTiles){
				if (currentHexagon.getGridPoint().equals(
					h.getGridPoint()
				)) {
					gh.selectedEntityTiles.remove(h);
					return;
				}
			}
		}
		for(Entity en : gh.entities) {
			for (Hexagon occTile : en.getOccupiedTiles()){

				if(currentHexagon.getGridPoint().equals(
					occTile.getGridPoint()
				)) {
					gh.addSelectedEntityTile(en.getTile());
					hasSelectedEntity = true;
				}
			}
		}
	}
	public void selectTile() {
		/*if (!gh.selectedEntityTiles.isEmpty() && !hasSelectedEntity) {

			for(Hexagon h : gh.selectedTiles){
				if (currentHexagon.getGridPoint().equals(
					h.getGridPoint()
				)) {
					gh.selectedTiles.remove(h);
					return;
				}
			}
			if (currentHexagon.getGridPoint().equals(
				gh.selectedEntityTile.getGridPoint()
			)) {
				gh.drawSelectedTile(null);
				return;
			}
		}*/
		if (!gh.selectedTiles.isEmpty()) {
			for(Hexagon h : gh.selectedTiles){
				if (currentHexagon.getGridPoint().equals(
					h.getGridPoint()
				)) {
					gh.selectedTiles.remove(h);
					return;
				} 
			}
		}
		gh.addSelectedTile(currentHexagon);

		if (gh.selectedEntityTiles.isEmpty()) return;

		Entity selectedEntity = gh.selectEntity(gh.selectedEntityTiles.getFirst());

		if (!currentHexagon.getGridPoint().equals(gh.selectedEntityTiles.getFirst().getGridPoint()) 
			&& selectedEntity instanceof Character
			&& isShiftDown
		) {
			gh.gm.moveCharacter(currentHexagon, (entities.Character)selectedEntity);
			
			AStar.run(gh.selectedEntityTiles.getFirst(), currentHexagon, gh);

		}
	}
}
