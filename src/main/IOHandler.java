package main;
import calc.AStar;
import calc.Calc;
import entities.Character;
import entities.Entity;
import fx.*;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import tools.Area;
import tools.Cone;
import tools.Line;
import tools.Measure;
import tools.Tool;

public class IOHandler extends MouseAdapter {
	
	protected Hexagon currentHexagon;
	protected final GraphicsHandler gh;
	protected int count = 0;
	protected int mode  = 0;
	protected Marker mouseLog;
	
	private ArrayList<Entity> clipBoard = new ArrayList<>();
	
	public boolean isRShiftDown = false;
	public boolean isShiftDown  = false;
	public boolean isCtrlDown   = false;
	public boolean isAltDown    = false;
	
	protected boolean LMBDown;
	protected boolean ADown;
	protected boolean DDown;
	protected boolean SDown;
	protected boolean WDown;
	
	protected boolean hasSelectedEntity;
	protected boolean gameMaster  = false;
	protected boolean mouseActive = true;
	
	public Point mousePos;

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
		mouseActive = true;
		switch (e.getButton()) 
		{
			case MouseEvent.BUTTON1 -> LMBPressed();
			case MouseEvent.BUTTON2 -> {}
			case MouseEvent.BUTTON3 -> RMBPressed();
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

		if(mouseActive) updateMouseTile();

		if(mode == Tool.LENGTH_MODE) {
			updateTotalLength();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePos = e.getPoint();
		if (gh.debugMode) logMousePos(e);
		
		currentHexagon = gh.findClosestHexagon(e.getPoint());
		
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
		
		Point consolPos = SwingUtilities.convertPoint(
			e.getComponent(), 
			mousePos, 
			gh.consol
		);
		if(gh.consol.contains(consolPos)) {
			gh.consol.scrollLog(notches);
			return;
		}

		if(isCtrlDown) {

		} else if(isShiftDown) {
			switch (mode) 
			{
				case Tool.CONE_MODE	-> {
					gh.coneAttack.changeAngle(notches);
				}
			}
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
			case KeyEvent.VK_UP   -> gh.consol.consol.arrowUp();
			case KeyEvent.VK_DOWN -> gh.consol.consol.arrowDown();
			case KeyEvent.VK_TAB  -> gh.consol.consol.getAutoComplete();
			default -> gh.consol.consol.resetSuggestions();
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
			case KeyEvent.VK_LEFT 	-> {
				if(isRShiftDown) moveTileUnderMouse(5);
				else moveTileUnderMouse(6);
			}
			case KeyEvent.VK_UP 	-> {
				if(isRShiftDown && gh.tileUnderMouse != null) gh.zoom(-1, gh.tileUnderMouse.getCenter());
				else if(isRShiftDown) gh.zoom(-1, new Point(gh.getWidth()/2, gh.getHeight()/2));
				else moveTileUnderMouse(1);
			}
			case KeyEvent.VK_RIGHT 	-> {
				if(isRShiftDown) moveTileUnderMouse(2);
				else moveTileUnderMouse(3);
			}
			case KeyEvent.VK_DOWN 	-> {
				if(isRShiftDown && gh.tileUnderMouse != null) gh.zoom(1, gh.tileUnderMouse.getCenter());
				else if(isRShiftDown) gh.zoom(1, new Point(gh.getWidth()/2, gh.getHeight()/2));
				else moveTileUnderMouse(4);
			}
		}
		//Any always active
		switch(e.getKeyCode()) {
			case KeyEvent.VK_SHIFT   -> {
				if(e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
					isShiftDown = true;
					if(gh.selectedEntityTiles != null && !gh.selectedEntityTiles.isEmpty()) {
						gh.entityPreviewTiles.clear();
						gh.addEntityPreviewTiles(this);
					}
				} else {
					isRShiftDown = true;
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
				if (isAltDown) {
					gh.consol.toggleLog();
					return;
				}
				if(gh.consol.isActive()) {
					gh.consol.consol.command(gh.consol.consol.getText());
					return;
				}
				if(!mouseActive) {
					mousePos = (gh.tileUnderMouse == null) ? new Point(gh.getWidth()/2, gh.getHeight()/2) : Calc.toPoint(gh.tileUnderMouse.getCenter());
					if(isRShiftDown) RMBPressed();
					else LMBPressed();
				}
			}

			case KeyEvent.VK_PLUS -> {
				if(isCtrlDown) {
					gh.consol.changeFontSize(1);
					gh.toolbox.changeSize(1);
					gh.revalidate();
					gh.repaint();
				}
			}
			case '-' -> {
				if(isCtrlDown) {
					gh.consol.changeFontSize(-1);
					gh.toolbox.changeSize(-1);
					gh.revalidate();
					gh.repaint();
				}
			}
			case 'A' -> {
				if(isCtrlDown) {
					for(Entity en : gh.entities) {
						if(en.getTile() == null) continue;
						currentHexagon = en.getTile();
						if(gh.selectedEntityTiles.contains(currentHexagon)) continue;

						selectEntityNoDeselection();
					}
				}
				else if(isAltDown) {
					selectTool(Tool.AREA_MODE);
				}
			}
			case 'C' -> {
				if(isCtrlDown) {
					clipBoard.clear();
					for(Hexagon tile : gh.selectedEntityTiles) {
						Entity en = gh.selectEntity(tile);
						if(clipBoard.contains(en)) continue;
						try {
							Entity newEn = (Entity) en.clone();
							clipBoard.add(newEn);
						} catch (Exception x) {
						}
					}
				}
				else if(isAltDown) {
					selectTool(Tool.CONE_MODE);
				}
			}
			case 'V' -> {
				if (isCtrlDown && !clipBoard.isEmpty()) {
					Point copyPoint  = clipBoard.getFirst().getTile().getGridPoint();
					Point pastePoint = gh.tileUnderMouse.getGridPoint();
					int[] pivot 	 = Calc.toCubeCoordinate(pastePoint.x - copyPoint.x, pastePoint.y - copyPoint.y);
					for(Entity en : clipBoard) {
						int[] cp = Calc.toCubeCoordinate(en.getTile().getGridPoint().x, en.getTile().getGridPoint().y);
						Point p = Calc.toPoint(new int[]{pivot[0] + cp[0], pivot[1] + cp[1], pivot[2] + cp[2]});
						
						en.setTile(gh.hexlist.get(p.x,p.y));
						try {
							gh.entities.add(en.clone());
						} catch (Exception x) {
						}
					}
					gh.repaint();
					e.consume();
				}
				else if(isAltDown) {
					selectTool(Tool.LINE_MODE);
				}
			}
			case 'S' -> { 
				if(isAltDown) {
					selectTool(Tool.LENGTH_MODE);
				}
			}
			case 'X' -> { 
				if(isAltDown) {
					selectTool(Tool.DRAG_MODE);
				}
			}
			case KeyEvent.VK_PERIOD -> {
				if(isShiftDown) {
					e.consume();
					boolean wasVisible = gh.consol.isVisible();
					gh.setConsolVisibility(true);
					gh.consol.consol.setText(wasVisible ? "" : ":");
					gh.consol.consol.setCaretPosition(wasVisible ? 0 : 1);
				}
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
				if(e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
					isShiftDown = false;
					gh.entityPreviewTiles.clear();
					gh.repaint();
				} else {
					isRShiftDown = false;
				}
			}
			case KeyEvent.VK_CONTROL -> isCtrlDown = false;
			case KeyEvent.VK_ALT 	 -> isAltDown  = false;

		}
	}

	private void RMBPressed() {
		switch (mode) 
		{
			case Tool.DRAG_MODE 	-> {if(mouseActive) gh.dragStart = mousePos;}
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
			case Tool.AREA_MODE	-> {
				gh.areaAttack = new Area(gh);
			}
			case Tool.CONE_MODE	-> {
				gh.coneAttack = new Cone(gh);
			}
		}
	}
	
	private void LMBPressed() {

		LMBDown = true;
		for (Tool t : gh.toolbox.getTools()) {
			if(t.getHitbox().contains(mousePos)) {
				selectTool(t);
			}
		}
		if (!isCtrlDown && !isShiftDown) {
			if (!isShiftDown)
				gh.selectedEntityTiles.clear();
			gh.selectedTiles.clear();
		}
		currentHexagon = gh.findClosestHexagon(mousePos);
		selectTile();
		if(!isShiftDown) 
			selectEntity();
	}
	//Other Methods ----------------------------------------------

	private void selectTool(Tool t) {
		gh.toolbox.selectedTool = t;
		mode = t.getToolMode();
		if (mode != Tool.LENGTH_MODE && !gh.measure.isEmpty()) {
			if(gh.measure.getLast().getFinishedPoint() == null) {
				gh.measure.remove(gh.measure.size()-1);
			}
			updateTotalLength();
			gh.totalLength.moveTo(new Point(gh.toolbox.getFrameSize(), 20));
		}

		if (mode != Tool.LINE_MODE) {
			gh.lineAttack = null;
		}
		if (mode != Tool.AREA_MODE) {
			gh.areaAttack = null;
		}
		if (mode != Tool.CONE_MODE) {
			gh.coneAttack = null;
		}
		gh.attackTiles.clear();
		gh.repaint();
	}

	private void selectTool(int t) {
		if(gh.toolbox.getTools().get(t).getToolMode() == t) {
			selectTool(gh.toolbox.getTools().get(t));
			return;
		} 
		for(Tool tool : gh.toolbox.getTools()) {
			if(tool.getToolMode() == t) {
				selectTool(tool);
				return;
			} 
		}
	}
	private void moveTileUnderMouse(int i) {
		if(gh.tileUnderMouse == null) {
			gh.tileUnderMouse = gh.findClosestHexagon(new Point(gh.getWidth()/2, gh.getHeight()/2));
		}
		Hexagon tile = gh.tileUnderMouse;
		try {
			gh.tileUnderMouse = AStar.getNeighbor(tile, i, gh);
		} catch (Exception e) {
			switch (i) {
				case 1 	-> gh.gridOffset(0, gh.hexSize*-2);
				case 2 	-> gh.gridOffset(gh.hexSize*2, -gh.hexSize);
				case 3 	-> gh.gridOffset(gh.hexSize*2, gh.hexSize);
				case 4 	-> gh.gridOffset(0, gh.hexSize*2);
				case 5 	-> gh.gridOffset(gh.hexSize*-2, gh.hexSize);
				case 6 	-> gh.gridOffset(gh.hexSize*-2, -gh.hexSize);
				default -> {}
			}
            
		}
		mouseActive = false;
		gh.repaint();
	}
	public void logMousePos(MouseEvent e) {
		if (mouseLog == null) {
			mouseLog = new Marker(new Point(0,0), Marker.COORDINATES, true);
			gh.markers.add(mouseLog);
		}
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

		selectEntityNoDeselection();
	}
	private void selectEntityNoDeselection() {
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
				ArrayList<Hexagon> rangeTiles = AStar.range(character.getTile(), character.getSpeed(), (Character) selectedEntity, gh);
				for (Hexagon centerTile : rangeTiles) {
					if (centerTile == null) continue;
					if (character.getSize() > 0) {
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
		
		gh.path.clear();
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
		if(gh.selectedEntityTiles != null && !isShiftDown)
			gh.addSelectedTile(currentHexagon);

		if (gh.selectedEntityTiles.isEmpty()) return;

		Entity selectedEntity = gh.selectEntity(gh.selectedEntityTiles.getFirst());

		if ((selectedEntity instanceof Character && isShiftDown
			&& gh.entityRangeTiles.contains(currentHexagon))
			|| gameMaster) {
			gh.gm.moveCharacter(currentHexagon, (entities.Character) selectedEntity);

			hasSelectedEntity = false;
			gh.selectedEntityTiles.clear();
			gh.entityRangeTiles.clear();
			gh.selectedTiles.clear();

			currentHexagon = gh.findClosestHexagon(mousePos);
			selectEntity();
		}
	}
	protected void toggleGameMaster() {
		gameMaster = !gameMaster;
	}
	public void updateMouseTile() {
		Hexagon h = gh.findClosestHexagon(mousePos);
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
	public void updateTotalLength() {
		int length = 0;
		for(Measure m : gh.measure) {
			length += m.length();
		}
		if (gh.totalLength == null) {
			gh.totalLength = new Marker(mousePos, Marker.STAT, false);
			gh.totalLength.setSuffix("ft");
			
		}
		gh.totalLength.setStat(length);
		gh.totalLength.moveTo(mousePos);
		gh.totalLength.setVisible(length > 0);
	}
	public boolean isMouseActive() {
		return mouseActive;
	}
}
