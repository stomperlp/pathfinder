package main;
import calc.AStar;
import entities.Character;
import entities.Entity;
import fx.*;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import tools.Tool;


public class IOHandler extends MouseAdapter {
	
	protected Hexagon currentHexagon;
	protected final GraphicsHandler gh;
	protected int count = 0;
	protected int mode  = 0;
	protected boolean isShiftDown = false;
	protected boolean isCtrlDown  = false;
	protected boolean isAltDown  = false;
	protected Marker mouseLog;

	protected boolean ADown;
	protected boolean DDown;
	protected boolean SDown;
	protected boolean WDown;

	private boolean hasSelectedEntity;
	public Point mousePos;
	private boolean LMBDown;
	protected boolean gameMaster;

	public IOHandler(GraphicsHandler gh) {
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
				for (Tool t : gh.toolbox.getTools()) {
					if(t.getHitbox().contains(mousePos)) {
						gh.toolbox.selectedTool = t;
						mode = t.getToolMode();
						if (mode != Tool.LENGTH_MODE) {
							if(gh.measure.getLast().getFinishedPoint() == null) {
								gh.measure.remove(gh.measure.size()-1);
							}
						}
						if (mode != Tool.LINE_MODE) {
							gh.lineAttack = null;
						}
						gh.repaint();
						return;
					}
				}
				if (!isCtrlDown && !isShiftDown) {
					if (!isShiftDown)
						gh.selectedEntityTiles.clear();
					gh.selectedTiles.clear();
				}
				currentHexagon = gh.gm.findClosestHexagon(mousePos);
				selectTile();
				if(!isShiftDown) 
					selectEntity();
			}
			case MouseEvent.BUTTON2 -> {}
			case MouseEvent.BUTTON3 -> {
				
				switch (mode) 
				{
					case Tool.DRAG_MODE 	-> gh.dragStart = mousePos;
					case Tool.LENGTH_MODE 	-> {
						if(!gh.measure.isEmpty()) {
							if(gh.measure.getLast().finish()) {
								//stops creation of new measure if double clicked.
								return;
							}
						}
						gh.measure.add(new Measure(gh));
					}
					case Tool.LINE_MODE	-> {
						gh.lineAttack = new Line(gh);
					}
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
					case Tool.DRAG_MODE -> gh.dragStart = null;
				}
			}
			default -> {}
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (gh.debugMode) logMousePos(e);
		mousePos = e.getPoint();

		updateMouseTile();

		if(mode == Tool.LENGTH_MODE) {
			int length = 0;
			for(Measure m : gh.measure) {
				length += m.length();
			}
			if (gh.totalLength == null) {
				gh.totalLength = new Marker(mousePos, Marker.STAT, false);
				System.out.println("g");
				
			}
			gh.totalLength.setStat(length);
			gh.totalLength.moveTo(mousePos);
		}
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
			case Tool.DRAG_MODE -> gh.drag(e);
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
			case KeyEvent.VK_UP   -> gh.consol.arrowUp();
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
				if (gh.selectedTiles.get(0) != null && !gh.selectedTiles.isEmpty()) {
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
			case KeyEvent.VK_SHIFT   -> {
				isShiftDown = true;
				if(gh.selectedEntityTiles != null && !gh.selectedEntityTiles.isEmpty()) {
					gh.entityPreviewTiles.clear();
					gh.addEntityPreviewTiles(this);
				}
			}
			case KeyEvent.VK_CONTROL -> isCtrlDown = true;
			case KeyEvent.VK_ALT 	 -> isAltDown  = true;
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
			case KeyEvent.VK_SHIFT   -> {
				isShiftDown = false;
				gh.entityPreviewTiles.clear();
				gh.repaint();
			}
			case KeyEvent.VK_CONTROL -> isCtrlDown = false;
		}
	}

	//Other Methods ----------------------------------------------

	public void logMousePos(MouseEvent e) {
		if (mouseLog == null) {
			mouseLog = new Marker(new Point(0,0), Marker.COORDINATES, true);
			gh.markers.add(mouseLog);
		}
		System.out.println(mousePos);
		mouseLog.moveTo(e.getPoint());
		gh.repaint();
	}
	public void selectEntity() {
		
		if (!gh.selectedEntityTiles.isEmpty()) {
			for(Hexagon h : gh.selectedEntityTiles) {
				if (currentHexagon.getGridPoint().equals(h.getGridPoint())) {
					gh.selectedEntityTiles.remove(h);
					return;
				}
			}
		}
		for(Entity en : gh.entities) {
			for (Hexagon occTile : en.getOccupiedTiles()) {

				if(currentHexagon.getGridPoint().equals(
					occTile.getGridPoint()
				)) {
					gh.addSelectedEntityTile(en.getTile());
					hasSelectedEntity = true;
				}
			}
		}
		if (hasSelectedEntity && !gh.entityRangeTiles.isEmpty()) {
			gh.entityRangeTiles.clear();
		}

		if (hasSelectedEntity && !gh.selectedEntityTiles.isEmpty()) {
			Entity selectedEntity = gh.selectEntity(gh.selectedEntityTiles.getFirst());
			if (selectedEntity instanceof Character character) {
				ArrayList<Hexagon> rangeTiles = AStar.range(character.getTile(), character.getSpeed(), gh);
				for (Hexagon centerTile : rangeTiles) {
					if (centerTile == null) continue;
					if(character.getSize() > 0) {
						ArrayList<Hexagon> wouldOccupyTiles = Character.getOccupiedTiles(centerTile, character.getSize(), gh);
						boolean positionIsValid = true;
						
						// Check every single tile the character would occupy
						for(Hexagon occupiedTile : wouldOccupyTiles) {
							if (occupiedTile == null) {
								positionIsValid = false;
								break;
							}
							
							// Check if there's any entity at this tile
							Entity blockingEntity = gh.selectEntity(occupiedTile);
							if (blockingEntity != null && !blockingEntity.equals(character)) {
								// This tile is blocked by another entity (wall, character, etc.)
								positionIsValid = false;
								break;
							}
						}
						
						// Only add this center position to range if ALL occupied tiles are free
						if (positionIsValid) {
							// Add only the center tile to the range display
							// (The character occupies multiple tiles, but we show range based on center)
							gh.addEntityRangeTile(centerTile);
						}
						
					} else {
						// For small characters (size 0): simple check
						Entity blockingEntity = gh.selectEntity(centerTile);
						if (blockingEntity == null || blockingEntity.equals(character)) {
							gh.addEntityRangeTile(centerTile);
						}
					}
				}
			}
		}
	}
	
	public void selectTile() {

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
		if(gh.selectedEntityTiles != null && !gh.selectedEntityTiles.isEmpty() && !isShiftDown)
			gh.addSelectedTile(currentHexagon);

		if (gh.selectedEntityTiles.isEmpty() || gh.selectedEntityTiles == null) return;

		Entity selectedEntity = gh.selectEntity(gh.selectedEntityTiles.getFirst());

		if ((selectedEntity instanceof Character && isShiftDown
			&& gh.entityRangeTiles.contains(currentHexagon))
			|| gameMaster) {
			gh.gm.moveCharacter(currentHexagon, (entities.Character) selectedEntity);

			hasSelectedEntity = false;
			gh.selectedEntityTiles.clear();
			gh.entityRangeTiles.clear();
			gh.selectedTiles.clear();

			currentHexagon = gh.gm.findClosestHexagon(mousePos);
			selectEntity();
			selectTile();
		}
	}
	protected void toggleGameMaster() {
		gameMaster = !gameMaster;
	}
	public void updateMouseTile() {
		Hexagon h = gh.gm.findClosestHexagon(mousePos);
		if (h == null) return;
		if(!h.equals(currentHexagon) || currentHexagon == null)
		{
			currentHexagon = h;
			gh.drawTileUnderMouse(currentHexagon);

			if(isShiftDown && gh.selectedEntityTiles != null) {
				gh.entityPreviewTiles.clear();
				gh.addEntityPreviewTiles(this);
			}
		}
	}
}
