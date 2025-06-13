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

// Main input/output handler for a hex-based game or tactical map application
// Extends MouseAdapter to handle mouse events and provides keyboard input handling
public class IOHandler extends MouseAdapter {
	// Currently selected hexagon tile
	protected Hexagon currentHexagon;
	// Reference to the graphics handler for rendering operations
	protected final GraphicsHandler gh;
	// Generic counter variable
	protected int count = 0;
	// Current tool/interaction mode (drag, measure, line, area, cone, etc.)
	protected int mode  = 0;
	// Marker for displaying mouse position in debug mode
	protected Marker mouseLog;

	// Clipboard for copy/paste operations of entities
	private ArrayList<Entity> clipBoard = new ArrayList<>();

	// Keyboard modifier key states
	public boolean isRShiftDown = false;
	public boolean isShiftDown  = false;
	public boolean isCtrlDown   = false;
	public boolean isAltDown    = false;

	// Mouse and keyboard button states
	protected boolean LMBDown;
	protected boolean ADown;
	protected boolean DDown;
	protected boolean SDown;
	protected boolean WDown;

	// Game state flags
	protected boolean hasSelectedEntity;
	protected boolean gameMaster  = false;
	// Whether mouse input is active (vs keyboard navigation)
	protected boolean mouseActive = true;

	// Current mouse position
	public Point mousePos;

	// Constructor initializes the handler with a graphics handler reference
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
	// Handle mouse press events - delegates to specific button handlers
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

	// Handle mouse release events - updates button states and mode-specific cleanup
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

	// Handle mouse movement - updates mouse position and current tile
	@Override
	public void mouseMoved(MouseEvent e) {
		if (gh.debugMode) logMousePos(e);
		mousePos = e.getPoint();

		if(mouseActive) updateMouseTile();

		// Update measurement display in measure mode
		if(mode == Tool.LENGTH_MODE) {
			updateTotalLength();
		}
	}

	// Handle mouse dragging - supports tile selection and drag operations
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePos = e.getPoint();
		if (gh.debugMode) logMousePos(e);
		
		currentHexagon = gh.findClosestHexagon(e.getPoint());
		
		// Multi-select tiles while holding Ctrl and dragging
		if (isCtrlDown && !gh.selectedTiles.contains(currentHexagon) && LMBDown) {
			gh.addSelectedTile(currentHexagon);
			selectEntity();
		}
		switch (mode)
		{
			case Tool.DRAG_MODE -> gh.drag(e);
		}
		
	}

	// Handle mouse wheel events for zooming and tool-specific adjustments
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		
		// Check if mouse is over console and scroll console instead
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
			// Reserved for future Ctrl+wheel functionality
		} else if(isShiftDown) {
			switch (mode) 
			{
				// Adjust cone angle in cone attack mode
				case Tool.CONE_MODE	-> {
					gh.coneAttack.changeAngle(notches);
				}
			}
		} else {
			// Default zoom behavior
			gh.zoom(notches, e.getPoint());
		}
	}

	// Handle key typed events (currently unused)
	void keyTyped(KeyEvent e) {
		switch(e.getKeyCode()) {
			
		}
	}

	// Handle key press events - supports both console and game input
	void keyPressed(KeyEvent e) {
		// Console-specific input handling
		if (gh.consol.isActive())
		//Any only active in consol
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP   -> gh.consol.consol.arrowUp();
			case KeyEvent.VK_DOWN -> gh.consol.consol.arrowDown();
			case KeyEvent.VK_TAB  -> gh.consol.consol.getAutoComplete();
			default -> gh.consol.consol.resetSuggestions();
		}
		// Game input handling when console is not active
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
			// Arrow key navigation with zoom functionality on RShift
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
		// Global key bindings active regardless of console state
		//Any always active
		switch(e.getKeyCode()) {
			// Track shift key states and update entity preview
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
			// Enter key - toggle console or simulate mouse click
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
				// Simulate mouse click at current tile position
				if(!mouseActive) {
					mousePos = (gh.tileUnderMouse == null) ? new Point(gh.getWidth()/2, gh.getHeight()/2) : Calc.toPoint(gh.tileUnderMouse.getCenter());
					if(isRShiftDown) RMBPressed();
					else LMBPressed();
				}
			}

			// Font and UI size controls
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
			// 'A' key - Select all entities or area tool
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
			// 'C' key - Copy entities or cone tool
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
			// 'V' key - Paste entities or line tool
			case 'V' -> {
				if (isCtrlDown && !clipBoard.isEmpty()) {
					// Calculate relative positioning for pasted entities
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
			// 'S' key - Length/measurement tool
			case 'S' -> { 
				if(isAltDown) {
					selectTool(Tool.LENGTH_MODE);
				}
			}
			// 'X' key - Drag tool
			case 'X' -> { 
				if(isAltDown) {
					selectTool(Tool.DRAG_MODE);
				}
			}
			// Period key - Quick console access
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

	// Handle key release events - reset button states and clear previews
	void keyReleased(KeyEvent e) {
		// Console-specific key release handling (currently unused)
		if (gh.consol.isActive())
		//Any only active in consol
		switch(e.getKeyCode()) {

		}
		// Game key release handling
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
		// Global key release handling
		//Any Always active
		switch(e.getKeyCode()) {
			// Clear shift states and entity previews
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

	// Handle right mouse button press - creates tools based on current mode
	private void RMBPressed() {
		switch (mode) 
		{
			case Tool.DRAG_MODE 	-> {if(mouseActive) gh.dragStart = mousePos;}
			case Tool.LENGTH_MODE 	-> {
				// Create new measurement or finish existing one
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

	// Handle left mouse button press - tool selection and tile/entity selection
	private void LMBPressed() {

		LMBDown = true;
		// Check if clicking on a tool in the toolbox
		for (Tool t : gh.toolbox.getTools()) {
			if(t.getHitbox().contains(mousePos)) {
				selectTool(t);
			}
		}
		// Clear selections unless holding modifier keys
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

	// Select and activate a tool, cleaning up previous tool states
	private void selectTool(Tool t) {
		gh.toolbox.selectedTool = t;
		mode = t.getToolMode();
		// Clean up measurement tool if switching away
		if (mode != Tool.LENGTH_MODE && !gh.measure.isEmpty()) {
			if(gh.measure.getLast().getFinishedPoint() == null) {
				gh.measure.remove(gh.measure.size()-1);
			}
			updateTotalLength();
			gh.totalLength.moveTo(new Point(gh.toolbox.getFrameSize(), 20));
		}

		// Clean up attack tools when switching modes
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

	// Select tool by mode ID - finds matching tool in toolbox
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

	// Move the tile cursor in hexagonal directions using keyboard
	private void moveTileUnderMouse(int i) {
		if(gh.tileUnderMouse == null) {
			gh.tileUnderMouse = gh.findClosestHexagon(new Point(gh.getWidth()/2, gh.getHeight()/2));
		}
		Hexagon tile = gh.tileUnderMouse;
		try {
			gh.tileUnderMouse = AStar.getNeighbor(tile, i, gh);
		} catch (Exception e) {
			// If can't move to neighbor, pan the view instead
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

	// Create or update mouse position marker for debugging
	public void logMousePos(MouseEvent e) {
		if (mouseLog == null) {
			mouseLog = new Marker(new Point(0,0), Marker.COORDINATES, true);
			gh.markers.add(mouseLog);
		}
		mouseLog.moveTo(e.getPoint());
		gh.repaint();
	}

	// Select entity at current hexagon - toggles selection if already selected
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

	// Select entity without deselecting if already selected
	private void selectEntityNoDeselection() {
		// Find entities occupying the current hexagon
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
		
		// Clear previous range display
		if (hasSelectedEntity && !gh.entityRangeTiles.isEmpty()) {
			gh.entityRangeTiles.clear();
		}

		// Calculate and display movement range for selected character
		if (hasSelectedEntity && !gh.selectedEntityTiles.isEmpty()) {
			Entity selectedEntity = gh.selectEntity(gh.selectedEntityTiles.getFirst());
			if (selectedEntity instanceof Character character) {
				ArrayList<Hexagon> rangeTiles = AStar.range(character.getTile(), character.getSpeed(), (Character) selectedEntity, gh);
				for (Hexagon centerTile : rangeTiles) {
					if (centerTile == null) continue;
					// Handle large characters that occupy multiple tiles
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

	// Handle tile selection and character movement
	public void selectTile() {
		
		gh.path.clear();
		// Toggle tile selection if already selected
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

		// Move character if conditions are met (Shift+click in range or GM mode)
		if ((selectedEntity instanceof Character && isShiftDown
			&& gh.entityRangeTiles.contains(currentHexagon))
			|| gameMaster) {
			gh.gm.moveCharacter(currentHexagon, (entities.Character) selectedEntity);

			// Clear selections after movement
			hasSelectedEntity = false;
			gh.selectedEntityTiles.clear();
			gh.entityRangeTiles.clear();
			gh.selectedTiles.clear();

			// Reselect entity at new position
			currentHexagon = gh.findClosestHexagon(mousePos);
			selectEntity();
		}
	}

	// Toggle game master mode for unrestricted movement
	protected void toggleGameMaster() {
		gameMaster = !gameMaster;
	}

	// Update the hexagon under mouse cursor and related UI elements
	public void updateMouseTile() {
		Hexagon h = gh.findClosestHexagon(mousePos);
		if (h == null) return;
		if(!h.equals(currentHexagon) || currentHexagon == null)
		{
			currentHexagon = h;
			gh.drawTileUnderMouse(currentHexagon);

			// Update entity preview when holding Shift
			if(isShiftDown && gh.selectedEntityTiles != null) {
				gh.entityPreviewTiles.clear();
				gh.addEntityPreviewTiles(this);
			}
		}
	}

	// Calculate and display total length of all measurements
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
		gh.totalLength.setVisible(length > 0); // hides the length if 0;
	}

	// Get current mouse active state (mouse vs keyboard control)
	public boolean isMouseActive() {
		return mouseActive;
	}
}