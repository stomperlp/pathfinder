package main;

import calc.*;
import entities.Character;
import entities.Entity;
import entities.Wall;
import fx.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class GraphicsHandler extends JFrame {

    public final Color DARK_PRIMARY    = new Color(0x0D1219);
    public final Color DARK_SECONDARY  = new Color(0x626972);
    public final Color LIGHT_PRIMARY   = new Color(0xD0D0D0);
    public final Color LIGHT_SECONDARY = new Color(0x000000);

    protected final IOHandler io;
    protected GameHandler gm;
    protected AnswerWaiter waiter = new AnswerWaiter();

    protected JPanel backgroundPanel;
    protected JPanel gridPanel;
    protected JPanel contentPanel;
    protected JPanel fxPanel;
    
    protected Toolbox toolbox;
    protected Consol consol;
    protected Image  backgroundImage;

    
    public    int hexSize        = 40; // Initial size
    protected int thickness      = 2;  // Hexagon Line thickness
    protected int backgroundRows = 20; // Number of rows the image takes up
    protected int backgroundCols = 20; // Number of columns the image takes up

    protected Point backgroundCenter;
    int drawWidth, drawHeight;

    protected Point dragStart = null;
    Point gridOffset = new Point(0, 0);
    
    public TwoKeyMap<Integer, Integer, Hexagon> hexlist = new TwoKeyMap<>();
    
    public ArrayList<Measure> measure             = new ArrayList<>();
    public ArrayList<Marker>  markers             = new ArrayList<>();
    public ArrayList<Entity>  entities            = new ArrayList<>();
    public ArrayList<Hexagon> selectedTiles       = new ArrayList<>();
    public ArrayList<Hexagon> selectedEntityTiles = new ArrayList<>();
    public ArrayList<Hexagon> entityRangeTiles    = new ArrayList<>();

    public Hexagon tileUnderMouse;
    
    protected int zoomFactor = 1;
    protected boolean debugMode = false; // :d or debug to change
    protected boolean darkMode = true; //Starts on Light :dm or darkmode to change
    public static boolean isFlat = true;


    private void inputListener()
    {
        addMouseListener(new MouseAdapter() { 

            @Override
			public void mousePressed(MouseEvent e) 
			{
				io.mousePressed(e);
			}

            @Override
            public void mouseReleased(MouseEvent e) 
            {
                io.mouseReleased(e);
            }
		});
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point translatedPoint = SwingUtilities.convertPoint(
                    e.getComponent(), 
                    e.getPoint(), 
                    contentPanel 
                );
                io.mouseMoved(new MouseEvent(
                    backgroundPanel, 
                    e.getID(), 
                    e.getWhen(), 
                    e.getModifiersEx(), 
                    translatedPoint.x, 
                    translatedPoint.y, 
                    e.getClickCount(), 
                    e.isPopupTrigger()
                ));
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                Point translatedPoint = SwingUtilities.convertPoint(
                    e.getComponent(), 
                    e.getPoint(), 
                    backgroundPanel 
                );
                io.mouseDragged(new MouseEvent(
                    backgroundPanel, 
                    e.getID(), 
                    e.getWhen(), 
                    e.getModifiersEx(), 
                    translatedPoint.x, 
                    translatedPoint.y, 
                    e.getClickCount(), 
                    e.isPopupTrigger()
                ));
            }
        });
        
        addMouseWheelListener((MouseWheelEvent e) -> {
            Point translatedPoint = SwingUtilities.convertPoint(
                    e.getComponent(),
                    e.getPoint(),
                    backgroundPanel
            );
            io.mouseWheelMoved(new MouseWheelEvent(
                    backgroundPanel,
                    e.getID(),
                    e.getWhen(),
                    e.getModifiersEx(),
                    translatedPoint.x,
                    translatedPoint.y,
                    e.getClickCount(),
                    e.isPopupTrigger(),
                    e.getScrollType(),
                    e.getScrollAmount(), 
                    e.getWheelRotation()
            ));
        });
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event instanceof KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    io.keyPressed(e);
                }
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    io.keyTyped(e);
                }
                if (e.getID() == KeyEvent.KEY_RELEASED) {
                    io.keyReleased(e);
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }

    public GraphicsHandler() 
    {
        io = new IOHandler(this);
        gm = new GameHandler(this);
        gm.run();
        setTitle("Bitti bitti 15 punkte");
        setDefaultCloseOperation(0);
        setSize(600, 400); // Initial size
        setBackground(Color.GRAY);
        setResizable(true);

        contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g.drawImage(new ImageIcon("./assets/UI/toolIcons/move.png").getImage(), 200, 200, 100, 100, this);
                for (Entity e : entities) {

                    if(e.getTile() == null) {
                        g2d.fillOval((int) e.getLocation().getX() - hexSize/10, (int) e.getLocation().getY() - hexSize/10, 
                            hexSize/10, hexSize/10);
                        continue;
                    }
                    e.debugUpdate();

                    Hexagon h = e.getTile();
                    h = hexlist.get(h.getGridPoint().x, h.getGridPoint().y);
                    
                    if(h != null) {
                        e.setTile(h);
                    } else continue;
                    

                    if (e instanceof Character c){
                        double x = switch (c.getSize()) {
                            case Character.LARGE      -> h.getCenter().getX();
                            case Character.GARGANTUAN -> h.getCenter().getX() - ((e.getDrawSize()-2) * hexSize/2);
                            default                   -> h.getCenter().getX() - (e.getDrawSize() * hexSize/2);
                        };
                        double y = switch (e.getDrawSize()) {
                            default -> h.getCenter().getY() - (e.getDrawSize() * hexSize/2);
                        };
                        e.setLocation(new Point2D.Double(x,y));
                        g2d.drawImage(
                            e.getImage(), 
                            (int) (e.getLocation().getX()), 
                            (int) (e.getLocation().getY()), 
                            (int) (e.getDrawSize() * hexSize), 
                            (int) (e.getDrawSize() * hexSize), this
                        );
                    }
                    else {
                        e.setLocation(new Point2D.Double( 
                            h.getCenter().getX() - (hexSize), 
                            h.getCenter().getY() - (Math.sqrt(3) * hexSize/2)
                        ));
                        g2d.drawImage(
                            e.getImage(), 
                            (int) (e.getLocation().getX()), 
                            (int) (e.getLocation().getY()), 
                            (int) (e.getDrawSize() * hexSize*2/Math.sqrt(3)), 
                            (int) (e.getDrawSize() * hexSize), this
                        );
                    }

                }

            }
        };
        contentPanel.setOpaque(false);
        gridPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                //stops grid from generation when zoomed out too much

                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Grid Transparency
                g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
                g2d.setStroke(new BasicStroke(thickness));
                
                hexlist.clear();
                // Calculate hexagon geometry
                double hexWidth = Math.sqrt(3) * hexSize;
                double hexHeight = 2 * hexSize;
                double ColDistance = (isFlat ? hexWidth  : hexHeight);
                double RowDistance = (isFlat ? hexHeight : hexWidth);

                int firstVisibleCol = -5 + (int) Math.floor((           - gridOffset.x - ColDistance) * 2 / Math.sqrt(3) / ColDistance);
                int lastVisibleCol  =  5 + (int) Math.ceil((getWidth()  - gridOffset.x - ColDistance) * 2 / Math.sqrt(3) / ColDistance);
                int firstVisibleRow = -5 + (int) Math.floor((           - gridOffset.y - RowDistance) * 1.16 / RowDistance);
                int lastVisibleRow  =  5 + (int) Math.ceil((getHeight() - gridOffset.y - RowDistance) * 1.16 / RowDistance);
                
                // Draw the grid
                for (    int row = firstVisibleRow; row <= lastVisibleRow; row++) {
                    for (int col = firstVisibleCol; col <= lastVisibleCol; col++) {
                        double centerX;
                        double centerY;

                        if(isFlat){
                            centerX = gridOffset.x + col * hexWidth  * Math.sqrt(3)/2;
                            centerY = gridOffset.y + row * hexHeight * Math.sqrt(3)/2;
                            // Offset every other row
                            if (col % 2 != 0) {
                                centerY += hexWidth/2;
                            }
                        } else {
                            centerX = gridOffset.x + col * hexHeight * Math.sqrt(3)/2;
                            centerY = gridOffset.y + row * hexWidth  * Math.sqrt(3)/2;
                            // Offset every other row
                            if (row % 2 != 0) {
                                centerX += hexWidth/2;
                            }
                        }

                        Point2D p   = new Point2D.Double(centerX, centerY);
                        Hexagon hex = new Hexagon(p, hexSize, new Point(row, col), isFlat);
                        
                        g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                        g2d.draw(hex.getShape());
                        hexlist.put(row,col,hex);
                    }
                }

                for (Hexagon h : selectedTiles){
                    if (h == null) continue;
                    selectedTiles.set(selectedTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y));
                }
                for (Hexagon h : selectedEntityTiles){
                    if (h == null) continue;
                    selectedEntityTiles.set(selectedEntityTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y)); 
                }
                for (Hexagon h : entityRangeTiles){
                    if (h == null) continue;
                    entityRangeTiles.set(entityRangeTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y)); 
                }
                if (tileUnderMouse != null && dragStart == null) {

                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                    g2d.draw(tileUnderMouse.getShape());
                }
                for (Hexagon h : selectedTiles){
                    if (h == null) continue;
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.RED);
                    g2d.draw(h.getShape());
                    
                }
                for (Hexagon h : selectedEntityTiles){
                    if (h == null) continue;
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.BLUE);
                    try {
                        for (Hexagon tile : selectEntity(h).getOccupiedTiles()) {
                            g2d.draw(tile.getShape());
                        }
                    } catch (Exception e) {
                    }
                }
                for (Hexagon h : entityRangeTiles){
                    if (h == null) continue;
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.GREEN);
                    g2d.draw(h.getShape());
                }
                
            }
        };
        gridPanel.setOpaque(false);
        fxPanel = new JPanel(new BorderLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (Marker m: markers) {
                    if (m.isDebugMarker() != debugMode) continue;
                    
                    switch (m.getPurpose()) {
                        case Marker.COORDINATES -> {

                            g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                            g2d.setFont(new Font("Arial", Font.BOLD, 18));
                            g2d.drawString(m.getText(), (int) m.getPoint().getX(), (int) m.getPoint().getY());
                        }
                        case Marker.DICE -> {

                            int digits = m.getText().length();
                            g2d.setColor(Color.RED);
                            g2d.fillRoundRect((int) m.getPoint().getX(), (int) m.getPoint().getY(),
                                16 * digits + 16, 16 * digits + 16, digits * 2, digits * 2);

                            g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                            g2d.setFont(new Font("Arial", Font.BOLD, 32));
                            g2d.drawString(m.getText(), (int) m.getPoint().getX() + 8, (int) m.getPoint().getY() + 20 + 8*digits);
                        }
                        case Marker.DICERESULT -> {
                            g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                            g2d.setFont(new Font("Arial", Font.BOLD, 50));
                            g2d.drawString(m.getText(), getWidth()/2, getHeight()/2);
                        }
                    }
                }
                for (Hexagon h : hexlist.values()) {
                    if (!debugMode) break;

                    g2d.setColor(Color.RED);
                    g2d.fillOval( (int) h.getCenter().getX() - hexSize/10, (int) h.getCenter().getY() - hexSize/10, 
                        hexSize/10, hexSize/10);
                    
                    g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                    g2d.setFont(new Font("Arial", Font.BOLD, hexSize/3));

                    String gridPoint = "[" + h.getGridPoint().x + "|" + h.getGridPoint().y + "]";
                    int digits = gridPoint.length();
                    g2d.drawString(
                        gridPoint, 
                        (int) h.getCenter().getX() - hexSize/3 - 2*digits, 
                        (int) h.getCenter().getY() + hexSize/5
                    );
                }

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(thickness*5));

                for(Measure m : measure) {
                    Line2D line = m.getLine();
                    if(line == null) continue;
                    g2d.draw(line);

                    g2d.setFont(new Font("Arial", Font.BOLD, hexSize/3));
                    Point lineCenter = new Point(
                        (int) (line.getX1() + (line.getX2()-line.getX1())/2),
                        (int) (line.getY1() + (line.getY2()-line.getY1())/2)
                    );
                    g2d.drawString(run() + "ft", lineCenter.x, lineCenter.y);
                }
            }
        };
        fxPanel.setOpaque(false);
        backgroundPanel = new JPanel(new BorderLayout()) {
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (backgroundImage != null) {
                    
                    double imageRatio  = (double) backgroundImage.getWidth(null) / backgroundImage.getHeight(null);
                    double formatRatio = (double) backgroundCols / backgroundRows;
                    
                    drawHeight = (int) (hexSize * backgroundCols * 2);
                    drawWidth  = (int) (drawHeight * imageRatio * formatRatio);
                    
                    int x = (int) gridOffset.getX();
                    int y = (int) gridOffset.getY();
                    
                    int centerX = backgroundImage.getWidth(this)/2;
                    int centerY = backgroundImage.getHeight(this)/2;
                    
                    backgroundCenter = new Point(centerX, centerY);
                    
                    g.drawImage(backgroundImage, x, y, drawWidth, drawHeight, this);
                }
            }
        };
        toolbox = new Toolbox();
        toolbox.setOpaque(true);
        
        consol = new Consol(this);
        
        
        
        backgroundPanel.add(contentPanel);
        contentPanel.add(gridPanel);
        gridPanel.add(fxPanel);
        add(consol, BorderLayout.SOUTH);
        fxPanel.add(toolbox, BorderLayout.WEST);
        add(backgroundPanel);


        toggleDarkMode();
        
        // Center the window on screen
        setLocationRelativeTo(null);
        setBackgroundImage();
        inputListener();

	}

    public void toggleConsol() {
        consol.setVisible(!consol.isVisible());
        consol.toggleActive();
        
        if(!consol.isActive()) {

            backgroundPanel.remove(consol);
            backgroundPanel.requestFocusInWindow();

        } else {

            backgroundPanel.add(consol, BorderLayout.SOUTH);
            consol.requestFocusInWindow();
        }
        consol.setText("");
        revalidate();
        repaint();
    }
    public final void setBackgroundImage() {
        File file = io.openFileBrowser();
        
        if (file != null) {
            this.backgroundImage = new ImageIcon(file.getPath()).getImage();
            this.backgroundPanel.repaint();
        }
    }
    public void drawTileUnderMouse(Hexagon hex) {
        tileUnderMouse = hex;
        repaint();
    }
    public void addSelectedTile(Hexagon hex) {
        selectedTiles.add(hex);
        repaint();
    }
    public void addSelectedEntityTile(Hexagon hex) {
        selectedEntityTiles.add(hex);
        repaint();
    }
    public void addEntityRangeTile(Hexagon hex) {
        entityRangeTiles.add(hex);
        repaint();
    }
    public void drag(MouseEvent e) {
        if (dragStart != null) {
            // Calculate drag distance
            int dx = e.getX() - dragStart.x;
            int dy = e.getY() - dragStart.y;
            
            // Update grid offset
            gridOffset(dx, dy);
            outOfBoundsCorrection();
            // Update drag start point for next movement
            dragStart = e.getPoint();
            repaint();
        }
    }    
    private void outOfBoundsCorrection(){
        // Bound to the left
        if(backgroundImage == null) return;

        if ( gridOffset.x > getWidth())  {
             gridOffset.setLocation(getWidth(),    gridOffset.y);
        }
        // Bound to the right
        if (-gridOffset.x > drawWidth)   {
             gridOffset.setLocation(-drawWidth,    gridOffset.y);
        }
        // Bound at the top
        if ( gridOffset.y > getHeight()) {
             gridOffset.setLocation(gridOffset.x,  getHeight());
        }
        //Bound at the bottom
        if (-gridOffset.y > drawHeight)  {
             gridOffset.setLocation(gridOffset.x, -drawHeight);
        }
    }
    public void toggleDebugMode() {
        debugMode = !debugMode;
    }
    public void zoom(int notches, Point mousePoint) {
        // Store previous values
        int prevHexSize = hexSize;
        double prevZoom = (double)prevHexSize / 40;  // baseHexSize should be your initial hex size
        
        // Calculate new hex size with constraints
        if (  (notches < 0 && hexSize < 200) 
           || (notches > 0 && hexSize > 15)
        ){
            hexSize -= notches * Math.max((hexSize * zoomFactor / 20), 1);

        } else {
            return;  // Don't zoom beyond min/max
        }
        
        // Calculate zoom factors
        double newZoom = (double)hexSize / 40;
        
        if (mousePoint != null) {
            // Convert mouse point to world coordinates
            double worldX = (mousePoint.x - gridOffset.x) / prevZoom;
            double worldY = (mousePoint.y - gridOffset.y) / prevZoom;
            
            // Calculate new offset to keep mouse position stable
            gridOffset.x = mousePoint.x - (int)(worldX * newZoom);
            gridOffset.y = mousePoint.y - (int)(worldY * newZoom);
        }


        thickness = Math.max(1, hexSize / 30);
        tileUnderMouse = null;
        outOfBoundsCorrection();
        repaint();
    } 

    public void setHexSize(int size) {
        this.hexSize = size;
        repaint();
    }
    public void setThickness(int thickness) {
        this.thickness = thickness;
        repaint();
    }
    public TwoKeyMap<Integer,Integer,Hexagon> getHexlist() {
        return hexlist;
    }
    public void spawnCharacter(int size, int maxhealth, int AC, int speed, int initiative) {
        ArrayList<Hexagon> tiles = new ArrayList<>();
        // tile the character spawns on
        if (selectedTiles.size() == 1) tiles.add(selectedTiles.getFirst());
        else if (selectedTiles.size() > 1) {
        
            new Thread(() -> {
                try {
                    consol.displayConfirmText("are you sure you want to create [" + selectedTiles.size() + "] Characters: (Y/N)" );
                    boolean confirm = (boolean)waiter.waitForAnswer();
                    if (confirm) {
                        for (Hexagon tile : selectedTiles) {
                            tiles.add(tile);
                        }
                        spawnCharacterAt(size, maxhealth, AC, speed, initiative, tiles);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            return;
        }
        else if (tileUnderMouse != null) {

            tiles.add(tileUnderMouse);
        }
        else {
            tiles.add(gm.findClosestHexagon(
                new Point2D.Double(getWidth()/2, getHeight()/2)
            ));
        }

        spawnCharacterAt(size, maxhealth, AC, speed, initiative, tiles);
    }
    public void spawnCharacterAt(int size, int maxhealth, int AC, int speed, int initiative, ArrayList<Hexagon> tiles) {
        Image image = new ImageIcon(io.openFileBrowser().getPath()).getImage();
         
        for(Hexagon tile : tiles) {
            try {
                Character c = new Character(
                    this,
                    image,
                    tile, 
                    tile.getCenter(),
                    size, maxhealth, AC, speed, initiative
                );
                entities.add(c);
            } catch (Exception e) {
                System.err.println("Character summon canceled");
            }
        }
    }
    public void addMarker(Marker m) {
        markers.add(m);
    }
    public void gridOffset(int x, int y) {
        gridOffset.translate(x, y);
        tileUnderMouse = null;
    }
    public Entity selectEntity(Hexagon hex) {

        for(Entity e : entities) {
            if (e.getTile().getGridPoint().equals(hex.getGridPoint())) {
                return e;
            }
        }
        return null;
    }

    public void deleteEntities() {
        ArrayList<Entity> delEntites = new ArrayList<>();
        for(Entity e : entities) {
            for(Hexagon tile : selectedEntityTiles) {
                if (e.getTile().getGridPoint().equals(tile.getGridPoint())) {
                    markers.remove(e.getMarker());
                    delEntites.add(e);
                }
            }
        }
        for (Entity e : delEntites) {
            entities.remove(e);
        }
    }
    public void toggleDarkMode() {
        darkMode = !darkMode;
        
        setBackground                  (darkMode ? DARK_PRIMARY   : LIGHT_PRIMARY);
        contentPanel.setBackground     (darkMode ? DARK_PRIMARY   : LIGHT_PRIMARY);
        gridPanel.setBackground        (darkMode ? DARK_PRIMARY   : LIGHT_PRIMARY);
        consol.setBackground           (darkMode ? DARK_PRIMARY   : LIGHT_PRIMARY);
        backgroundPanel.setBackground  (darkMode ? DARK_PRIMARY   : LIGHT_PRIMARY);
        consol.setSelectedTextColor    (darkMode ? DARK_PRIMARY   : LIGHT_PRIMARY);
        consol.setForeground           (darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
        consol.setSelectionColor       (darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
        consol.setBorder(new LineBorder(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY, 1));
        toolbox.setBackground(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
        toolbox.setBorder(new LineBorder(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY, 1));
    }

    public void summonWall() {
        Image image = new ImageIcon(io.openFileBrowser().getPath()).getImage();
         
        for(Hexagon tile : selectedTiles) {
            try {
                Wall w = new Wall(
                    this,
                    image,
                    tile, 
                    tile.getCenter()
                );
                entities.add(w);
            } catch (Exception e) {
                System.err.println("Wall summon canceled");
            }
        }
    }

    public void summonEntity() {
        Image image = new ImageIcon(io.openFileBrowser().getPath()).getImage();
         
        for(Hexagon tile : selectedTiles) {
            try {
                Entity w = new Entity(
                    this,
                    image,
                    tile, 
                    tile.getCenter()
                );
                entities.add(w);
            } catch (Exception e) {
                System.err.println("Entity summon canceled");
            }
        }
    }

    public void toggleGridOrientation() {
        isFlat = !isFlat;
        gridOffset = new Point((int) (gridOffset.x),
                               (int) (gridOffset.y));
        repaint();
    }
}