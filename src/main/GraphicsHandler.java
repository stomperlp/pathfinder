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
import tools.Area;
import tools.Cone;
import tools.Line;
import tools.Measure;

// Main graphics handler class that extends JFrame to create the game window
// This class manages the hexagonal grid display, entities, and user interactions
    
public final class GraphicsHandler extends JFrame {
    // Color scheme constants for dark and light themes
    public final Color DARK_PRIMARY    = new Color(0x0D1219);
    public final Color DARK_SECONDARY  = new Color(0x626972);
    public final Color LIGHT_PRIMARY   = new Color(0xD0D0D0);
    public final Color LIGHT_SECONDARY = new Color(0x000000);

    // Handler references for input/output and game logic
    public final IOHandler io;
    public GameHandler gm;
    protected AnswerWaiter waiter = new AnswerWaiter();

    // UI panel components organized in layers
    protected JPanel backgroundPanel;  // Bottom layer for background image
    protected JPanel contentPanel;     // Middle layer for entities
    protected JPanel gridPanel;       // Grid layer for hexagonal grid
    protected JPanel fxPanel;         // Top layer for effects and UI elements

    // Additional UI components
    protected Image  backgroundImage;
    protected Toolbox toolbox;
    public ConsolPanel consol;

    // Grid configuration parameters
    public    int hexSize        = 40; // Initial size
    protected int thickness      = 2;  // Hexagon Line thickness
    protected int backgroundRows = 20; // Number of rows the image takes up
    protected int backgroundCols = 20; // Number of columns the image takes up

    // Grid positioning and dragging variables
    protected Point backgroundCenter;
    int drawWidth, drawHeight;
    protected Point dragStart = null;
    Point gridOffset = new Point(0, 0);

    // Data structure to store hexagon grid using two-key mapping (row, column)
    public TwoKeyMap<Integer, Integer, Hexagon> hexlist = new TwoKeyMap<>();

    // Collections for managing various game elements
    public ArrayList<Theme>   themes              = new ArrayList<>();
    public ArrayList<Measure> measure             = new ArrayList<>();
    public ArrayList<Marker>  markers             = new ArrayList<>();
    public ArrayList<Entity>  entities            = new ArrayList<>();
    public ArrayList<Hexagon> selectedTiles       = new ArrayList<>();
    public ArrayList<Hexagon> selectedEntityTiles = new ArrayList<>();
    public ArrayList<Hexagon> entityRangeTiles    = new ArrayList<>();
    public ArrayList<Hexagon> entityPreviewTiles  = new ArrayList<>();
    public ArrayList<Hexagon> attackTiles         = new ArrayList<>();
    public ArrayList<Hexagon> path                = new ArrayList<>();

    // Current state variables
    public Hexagon tileUnderMouse;
    public Theme   currentTheme; 
    public Marker  totalLength;
    public Line    lineAttack;
    public Area    areaAttack;
    public Cone    coneAttack;

    // Configuration flags and settings
    public static boolean isFlat = true;
    protected boolean darkMode = true; //Starts on Light :dm or darkmode to change
    public boolean debugMode = false; // :d or debug to change
    protected int zoomFactor = 1;

    // Sets up mouse and keyboard event listeners for user interaction
    private void inputListener()
    {
        // Mouse click handlers
        addMouseListener(new MouseAdapter() { 

            @Override
            public void mousePressed(MouseEvent e) {
                io.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                io.mouseReleased(e);
            }
        });
        
        // Mouse movement handlers with coordinate translation to background panel
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                // Convert mouse coordinates from component to background panel coordinates
                Point translatedPoint = SwingUtilities.convertPoint(
                    e.getComponent(),
                    e.getPoint(),
                    contentPanel
                );
                // Create new mouse event with translated coordinates
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
                // Handle mouse dragging with coordinate translation
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
        
        // Mouse wheel listener for zooming functionality
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
        
        // Global keyboard event listener using AWT event system
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

    // Constructor - initializes the main window and all components
    public GraphicsHandler() 
    {
        // Initialize core handlers
        io = new IOHandler(this);
        gm = new GameHandler(this);
        gm.run();
        Marker.gh = this;
        
        // Basic window setup
        setTitle("Bitti bitti 15 Punkte");
        setDefaultCloseOperation(0);
        setSize(600, 400); // Initial size
        setBackground(Color.GRAY);
        setResizable(true);

        // Content panel - handles entity rendering on top of grid
        contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Render all entities in the game
                for (Entity e : entities) {

                    // Handle entities without assigned tiles
                    if(e.getTile() == null) {
                        g2d.fillOval((int) e.getLocation().getX() - hexSize/10, (int) e.getLocation().getY() - hexSize/10, 
                            hexSize/10, hexSize/10);
                        continue;
                    }
                    e.debugUpdate();

                    // Update entity tile reference from current hexagon list
                    Hexagon h = e.getTile();
                    h = hexlist.get(h.getGridPoint().x, h.getGridPoint().y);

                    if(h != null) {
                        e.setTile(h);
                    } else continue;

                    // Special rendering logic for Character entities
                    if (e instanceof Character c){
                        // Calculate position based on character size
                        double x = switch (c.getSize()) {
                            case Character.LARGE      -> h.getCenter().getX();
                            case Character.GARGANTUAN -> h.getCenter().getX() - ((e.getDrawSize()-2) * hexSize/2);
                            default                   -> h.getCenter().getX() - ( e.getDrawSize()    * hexSize/2);
                        };
                        double y = switch (e.getDrawSize()) {
                            default -> h.getCenter().getY() - (e.getDrawSize() * hexSize/2);
                        };
                        e.setLocation(new Point2D.Double(x,y));
                        // Draw character image
                        g2d.drawImage(
                            e.getImage(), 
                            (int) (e.getLocation().getX()), 
                            (int) (e.getLocation().getY()), 
                            (int) (e.getDrawSize() * hexSize), 
                            (int) (e.getDrawSize() * hexSize), this
                        );
                    }
                    else {
                        // Render non-character entities (like walls)
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
        
        // Grid panel - renders the hexagonal grid
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
                // Calculate hexagon geometry based on orientation (flat vs pointy top)
                double hexWidth  = hexSize * Math.sqrt(3);
                double hexHeight = hexSize * 2;
                double ColDistance = (isFlat ? hexWidth  : hexHeight);
                double RowDistance = (isFlat ? hexHeight : hexWidth);

                // Calculate visible grid range to optimize rendering
                int firstVisibleCol = -2 + (int) Math.floor((           - gridOffset.x - ColDistance) * 2 / Math.sqrt(3) / ColDistance);
                int lastVisibleCol  =  2 + (int) Math.ceil((getWidth()  - gridOffset.x - ColDistance) * 2 / Math.sqrt(3) / ColDistance);
                int firstVisibleRow = -2 + (int) Math.floor((           - gridOffset.y - RowDistance) * 1.16 / RowDistance);
                int lastVisibleRow  =  2 + (int) Math.ceil((getHeight() - gridOffset.y - RowDistance) * 1.16 / RowDistance);
                
                // Generate and draw hexagon grid within visible range
                for (    int row = firstVisibleRow; row <= lastVisibleRow; row++) {
                    for (int col = firstVisibleCol; col <= lastVisibleCol; col++) {
                        double centerX;
                        double centerY;

                        // Calculate hexagon center position based on grid orientation
                        if(isFlat){
                            centerX = gridOffset.x + col * hexWidth  * Math.sqrt(3)/2;
                            centerY = gridOffset.y + row * hexHeight * Math.sqrt(3)/2;
                            // Offset every other row for proper hexagon tessellation
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

                        // Create hexagon object and add to grid
                        Point2D p   = new Point2D.Double(centerX, centerY);
                        Hexagon hex = new Hexagon(p, hexSize, new Point(row, col), isFlat);
                        
                        g2d.setColor(currentTheme.getSecondary());
                        g2d.draw(hex.getShape());
                        hexlist.put(row,col,hex);
                    }
                }
                
                // Update hexagon references in all tile collections after grid regeneration
                for (Hexagon h : selectedTiles) {
                    if (h == null) continue;
                    selectedTiles.set(selectedTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y));
                }
                for (Hexagon h : selectedEntityTiles) {
                    if (h == null) continue;
                    selectedEntityTiles.set(selectedEntityTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y)); 
                }
                for (Hexagon h : entityRangeTiles) {
                    if (h == null) continue;
                    entityRangeTiles.set(entityRangeTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y)); 
                }
                for (Hexagon h : entityPreviewTiles) {
                    if (h == null) continue;
                    entityPreviewTiles.set(entityPreviewTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y)); 
                }
                for (Hexagon h : attackTiles){
                    if (h == null) continue;
                    attackTiles.set(attackTiles.indexOf(h),hexlist.get(h.getGridPoint().x, h.getGridPoint().y)); 
                }
                for (Hexagon h : path) {
                    if (h == null) continue;
                    path.set(path.indexOf(h), hexlist.get(h.getGridPoint().x, h.getGridPoint().y));
                }

                // Highlight tile under mouse cursor
                if (tileUnderMouse != null && dragStart == null) {

                    g2d.setStroke(new BasicStroke(thickness + (io.mouseActive ? 2 : 4)));
                    g2d.setColor(currentTheme.getSecondary());
                    g2d.draw(tileUnderMouse.getShape());
                }
                
                // Draw selected tiles with red outline
                for (Hexagon h : selectedTiles) {
                    if (h == null) continue;
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.RED);
                    g2d.draw(h.getShape());
                    
                }
                
                // Draw selected entity tiles with blue outline
                for (Hexagon h : selectedEntityTiles) {
                    if (h == null) continue;
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.BLUE);
                    try {
                        // Draw all tiles occupied by the entity
                        for (Hexagon tile : selectEntity(h).getOccupiedTiles()) {
                            g2d.draw(tile.getShape());
                        }
                    } catch (Exception e) {
                    }
                }
                
                // Draw entity range tiles with green outline
                for (Hexagon h : entityRangeTiles) {
                    if (h == null) continue;
                    if (!entityPreviewTiles.contains(h)) {
                        g2d.setStroke(new BasicStroke(thickness+2));
                        g2d.setColor(Color.GREEN);
                        g2d.draw(h.getShape());
                    }
                }
                
                // Draw attack tiles with magenta outline
                for (Hexagon h : attackTiles) {
                    if (h == null) continue;
                    if (!entityPreviewTiles.contains(h)) {
                        g2d.setStroke(new BasicStroke(thickness+2));
                        g2d.setColor(Color.MAGENTA);
                        g2d.draw(h.getShape());
                    }
                }
                
                // Draw entity preview tiles with different colors based on validity
                if (entityRangeTiles.contains(tileUnderMouse)) {
                    g2d.setColor(new Color(0x48CAE4));
                } else {
                    g2d.setColor(new Color(0xFB5607));
                }
                for (Hexagon h : entityPreviewTiles) {
                    if (h == null) continue;
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.draw(h.getShape());
                }
            }
        };
        gridPanel.setOpaque(false);
        
        // FX panel - renders special effects, markers, and UI overlays
        fxPanel = new JPanel(new BorderLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Add total length marker if it exists
                if (totalLength != null) markers.add(totalLength);

                // Render all markers with different styles based on their purpose
                for (Marker m: markers) {
                    // Skip debug markers when not in debug mode (except stats)
                    if (m.isDebugMarker() && !debugMode && !(m.getPurpose() == Marker.STAT && m.getAttachedEntity() != null)) continue;
                    if (!m.isVisible()) continue;
                    m.update();
                    
                    // Render markers based on their type
                    switch (m.getPurpose()) {
                        case Marker.COORDINATES -> {
                            // Coordinate display markers
                            g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                            g2d.setFont(new Font("Arial", Font.BOLD, 18));
                            g2d.drawString(m.getText(), (int) m.getPoint().getX(), (int) m.getPoint().getY());
                        }
                        case Marker.STAT        -> {
                            // Entity stat markers
                            g2d.setColor(m.getColor() != null ? m.getColor() : (darkMode ? DARK_SECONDARY : LIGHT_SECONDARY));
                            g2d.setFont(new Font("Arial", Font.BOLD, 18));
                            g2d.drawString(m.getText(), (int) m.getPoint().getX(), (int) m.getPoint().getY());
                        }
                        case Marker.DICE        -> {
                            // Dice roll markers with red background
                            int digits = m.getText().length();
                            g2d.setColor(Color.RED);
                            g2d.fillRoundRect((int) m.getPoint().getX(), (int) m.getPoint().getY(),
                                16 * digits + 16, 16 * digits + 16, digits * 2, digits * 2);

                            g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                            g2d.setFont(new Font("Arial", Font.BOLD, 32));
                            g2d.drawString(m.getText(), (int) m.getPoint().getX() + 8, (int) m.getPoint().getY() + 20 + 8*digits);
                        }
                        case Marker.DICERESULT  -> {
                            // Large centered dice result display
                            g2d.setColor(darkMode ? DARK_SECONDARY : LIGHT_SECONDARY);
                            g2d.setFont(new Font("Arial", Font.BOLD, 50));
                            g2d.drawString(m.getText(), getWidth()/2, getHeight()/2);
                        }
                    }
                }
                
                // Debug mode: render hexagon centers and grid coordinates
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

                g2d.setStroke(new BasicStroke(thickness*5));
                
                // Render measurement lines
                for(Measure m : measure) {
                    g2d.setColor(Color.RED);
                    Line2D line = m.getLine();
                    if(line == null) continue;
                    g2d.draw(line);

                    // Draw distance labels
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, hexSize/2));
                    Point lineCenter = new Point(
                        (int) (line.getX1() + (line.getX2()-line.getX1())/2),
                        (int) (line.getY1() + (line.getY2()-line.getY1())/2)
                    );
                    g2d.drawString(
                        m.length() + "ft",
                        lineCenter.x, lineCenter.y
                    );
                }
                
                // Render line attacks
                if (lineAttack != null) {
                    g2d.setColor(Color.RED);
                    Line2D line = lineAttack.getLine();
                    if(line != null){ 
                        g2d.draw(line);

                        g2d.setColor(Color.BLACK);
                        g2d.setFont(new Font("Arial", Font.BOLD, hexSize/2));
                        Point lineCenter = new Point(
                            (int) (line.getX1() + (line.getX2()-line.getX1())/2),
                            (int) (line.getY1() + (line.getY2()-line.getY1())/2)
                        );
                        lineAttack.getAttackedCharacters();
                        g2d.drawString(
                            lineAttack.length() + "ft",
                            lineCenter.x, lineCenter.y
                        );
                    }
                }
                
                // Render area attacks as circles
                if (areaAttack != null) {
                    g2d.setColor(Color.RED);

                    Point2D origin = hexlist.get(areaAttack.getOrigin().x, areaAttack.getOrigin().y).getCenter();

                    areaAttack.getAttackedCharacters();

                    g2d.setStroke(new BasicStroke(thickness*2));

                    g2d.drawOval((int)(origin.getX() - areaAttack.getRadius()), 
                                (int)(origin.getY() - areaAttack.getRadius()), 
                                (int) areaAttack.getRadius() * 2, 
                                (int) areaAttack.getRadius() * 2);

                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, hexSize/2));
                    g2d.drawString(
                        (int)(areaAttack.getRadiusInTiles()*5) + "ft",
                        (int)(origin.getX()),
                        (int)(origin.getY())
                    );
                }

                // Render cone attacks as arc segments
                if (coneAttack != null) {
                    double radius = coneAttack.getRadius();
                    if(radius > hexSize) {
                        g2d.setColor(Color.RED);
                        
                        Point2D origin = hexlist.get(coneAttack.getOrigin().x, coneAttack.getOrigin().y).getCenter();
                        double startAngle = coneAttack.getStartAngle();
                        double endAngle = startAngle + coneAttack.getAngle();

                        g2d.setStroke(new BasicStroke(thickness*2));
                        
                        // Draw outer arc
                        g2d.drawArc( 
                            (int)(origin.getX() - radius), 
                            (int)(origin.getY() - radius), 
                            (int) radius * 2, 
                            (int) radius * 2,
                            (int) coneAttack.getStartAngle(),
                            (int) coneAttack.getAngle()
                        );
                        // Draw inner arc
                        g2d.drawArc( 
                            (int)(origin.getX() - hexSize/2), 
                            (int)(origin.getY() - hexSize/2), 
                            (int) hexSize/2 * 2,
                            (int) hexSize/2 * 2, 
                            (int) coneAttack.getStartAngle(),
                            (int) coneAttack.getAngle()
                        );
                        // Draw cone edges
                        g2d.drawLine(
                            (int)(origin.getX() + Math.cos(startAngle * -Math.PI/180) * hexSize/2), 
                            (int)(origin.getY() + Math.sin(startAngle * -Math.PI/180) * hexSize/2), 
                            (int)(origin.getX() + Math.cos(startAngle * -Math.PI/180) * radius), 
                            (int)(origin.getY() + Math.sin(startAngle * -Math.PI/180) * radius)
                        );
                        
                        g2d.drawLine(
                            (int)(origin.getX() + Math.cos(endAngle * -Math.PI/180) * hexSize/2), 
                            (int)(origin.getY() + Math.sin(endAngle * -Math.PI/180) * hexSize/2), 
                            (int)(origin.getX() + Math.cos(endAngle * -Math.PI/180) * radius), 
                            (int)(origin.getY() + Math.sin(endAngle * -Math.PI/180) * radius)
                        );
                        
                        g2d.setColor(Color.BLACK);
                        g2d.setFont(new Font("Arial", Font.BOLD, hexSize/2));
                        g2d.drawString(
                            (int)(coneAttack.getRadiusInTiles()*5) + "ft",
                            (int)(origin.getX()),
                            (int)(origin.getY())
                        );
                        coneAttack.getAttackedCharacters();
                    }
                }

                // Render movement paths as connected lines
                if (path != null && !path.isEmpty()) {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(thickness*3));
                    for (int i = 0; i < path.size() - 1; i++) {
                        Hexagon start = path.get(i);
                        Hexagon end   = path.get(i + 1);
                        if (start == null || end == null) continue;
                        g2d.drawLine(
                            (int) start.getCenter().getX(), 
                            (int) start.getCenter().getY(), 
                            (int)   end.getCenter().getX(), 
                            (int)   end.getCenter().getY()
                        );
                    }
                }
            }
        };
        fxPanel.setOpaque(false);
        
        // Background panel - renders background images
        backgroundPanel = new JPanel(new BorderLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (backgroundImage != null) {

                    // Calculate proper scaling for background image
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
        
        // Initialize UI components
        consol = new ConsolPanel(this);
        consol.setOpaque(false);

        toolbox = new Toolbox();
        toolbox.setOpaque(true);

        // Assemble the layered panel structure
        backgroundPanel.add(contentPanel);
        contentPanel.add(gridPanel);
        gridPanel.add(fxPanel);
        fxPanel.add(toolbox, BorderLayout.WEST);
        add(backgroundPanel);
        fxPanel.add(consol, BorderLayout.SOUTH);
        
        // Center window on screen
        setLocationRelativeTo(null);

        // Initialize event handlers and themes
        inputListener();
        initializeThemes();

        changeTheme("light");
    }

    // Initialize predefined color themes for the application
    private void initializeThemes() {
        themes.add(new Theme("Purple",  new Color(0x4e0a56), new Color(0xda80ff)));
        themes.add(new Theme("Red",     new Color(0x101820), new Color(0xff0000)));
        themes.add(new Theme("Green",   new Color(0x101820), new Color(0x1a7a4c)));
        themes.add(new Theme("Yellow",  new Color(0xFEE715), new Color(0x101820)));
        themes.add(new Theme("Light",   new Color(0xD0D0D0), new Color(0x000000)));
        themes.add(new Theme("Dark",    new Color(0x0D1219), new Color(0x626972)));
        themes.add(new Theme("Black",   new Color(0x000000), new Color(0x1c2022)));
    }

    // Toggle console visibility and focus
    public void toggleConsol() {
        consol.setVisible(!consol.isVisible());
        consol.toggleActive();

        if(!consol.isActive()) {
            backgroundPanel.requestFocusInWindow();
        } else {
            consol.consol.requestFocusInWindow();
        }
        revalidate();
        repaint();
    }

    // Set console visibility to specific state
    public void setConsolVisibility(boolean isVisible) {
        if(consol.isVisible() != isVisible) toggleConsol();
    }

    // Open file browser to select background image
    public final void setBackgroundImage() {
        File file = io.openFileBrowser();

        if (file != null) {
            this.backgroundImage = new ImageIcon(file.getPath()).getImage();
            this.backgroundPanel.repaint();
        }
    }

    // Update the tile currently under mouse cursor and trigger repaint
    public void drawTileUnderMouse(Hexagon hex) {
        tileUnderMouse = hex;
        repaint();
    }

    // Add a tile to the selected tiles collection
    public void addSelectedTile(Hexagon hex) {
        selectedTiles.add(hex);
        repaint();
    }

    // Add a tile to the selected entity tiles collection
    public void addSelectedEntityTile(Hexagon hex) {
        selectedEntityTiles.add(hex);
        repaint();
    }

    // Add a tile to the entity range tiles collection
    public void addEntityRangeTile(Hexagon hex) {
        entityRangeTiles.add(hex);
        repaint();
    }

    // Add a tile to the entity preview tiles collection
    public void addEntityPreviewTile(Hexagon hex) {
        entityPreviewTiles.add(hex);
        repaint();
    }

    // Handle mouse dragging for panning the grid
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

    // Prevent the grid from being dragged beyond reasonable bounds
    void outOfBoundsCorrection() {
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

    // Toggle debug mode display
    public void toggleDebugMode() {
        debugMode = !debugMode;
    }

    // Handle zooming functionality with mouse wheel
    public void zoom(int notches, Point2D mousePoint) {
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

        // Maintain zoom center at mouse position
        if (mousePoint != null) {
            // Convert mouse point to world coordinates
            double worldX = (mousePoint.getX() - gridOffset.x) / prevZoom;
            double worldY = (mousePoint.getY() - gridOffset.y) / prevZoom;

            // Calculate new offset to keep mouse position stable
            gridOffset.x = (int) mousePoint.getX() - (int)(worldX * newZoom);
            gridOffset.y = (int) mousePoint.getY() - (int)(worldY * newZoom);
        }

        // Update line thickness based on zoom level
        thickness = Math.max(1, hexSize / 30);
        
        // Update tile under mouse reference after zoom
        if (tileUnderMouse != null) {
            Hexagon newTile = hexlist.get(tileUnderMouse.getGridPoint().x, tileUnderMouse.getGridPoint().y);
            if (newTile != null) {
                tileUnderMouse = newTile;
            } else {
                tileUnderMouse = null;
            }
        }

        outOfBoundsCorrection();
        repaint();
    }

    // Set hexagon size and trigger repaint
    public void setHexSize(int size) {
        this.hexSize = size;
        repaint();
    }

    // Set line thickness and trigger repaint
    public void setThickness(int thickness) {
        this.thickness = thickness;
        repaint();
    }

    // Get the current hexagon list
    public TwoKeyMap<Integer,Integer,Hexagon> getHexlist() {
        return hexlist;
    }

    // Spawn character entities on selected tiles or under mouse
    public void spawnCharacter(int size, int maxhealth, int AC, int speed, int initiative) {
        ArrayList<Hexagon> tiles = new ArrayList<>();
        // tile the character spawns on
        if      (selectedTiles.size() == 1) tiles.add(selectedTiles.getFirst());
        else if (selectedTiles.size() > 1 ) {
            // Confirm multiple character creation
            new Thread(() -> {
                try {
                    consol.consol.displayConfirmText("are you sure you want to create [" + selectedTiles.size() + "] Characters: (Y/N)" );
                    boolean confirm = (boolean) waiter.waitForAnswer();
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
            // Default to center of screen
            tiles.add(findClosestHexagon(
                new Point2D.Double(getWidth()/2, getHeight()/2)
            ));
        }

        spawnCharacterAt(size, maxhealth, AC, speed, initiative, tiles);
    }

    // Create character entities at specific tiles with image selection
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

    // Add a marker to the markers collection
    public void addMarker(Marker m) {
        markers.add(m);
    }

    // Translate grid offset by specified amounts
    public void gridOffset(int x, int y) {
        gridOffset.translate(x, y);
        tileUnderMouse = null;
    }

    // Find entity located at specific hexagon
    public Entity selectEntity(Hexagon hex) {

        for(Entity e : entities) {
            if (e.getTile().getGridPoint().equals(hex.getGridPoint())) {
                return e;
            }
        }
        return null;
    }

    // Delete entities of specified type from the game
    public void deleteEntities(int t) {
        ArrayList<Entity> delEntites = new ArrayList<>();
        Class<?> type = switch (t) {
            case 0  -> Character.class;
            case 1  -> Wall.class;
            default -> Entity.class;
        };
        for(Entity e : entities) {
            if(type.isInstance(e)) {
                Point p = e.getTile().getGridPoint();
                hexlist.get(p.x, p.y);
                markers.remove(e.getMarker());
                delEntites.add(e);
            }
        }
        for (Entity e : delEntites) {
            entities.remove(e);
        }
    }

    // Change the current color theme
    public void changeTheme(String s) {
        Theme theme = null;
        for(Theme t : themes) {
            if(t.getName().equals(s.toLowerCase())) {
                theme = t;
                break;
            }
        }
        if(theme == null) {
            consol.addLogMessage("There is no such theme as \"" + s + "\"");
            return;
        }
        currentTheme = theme;
        updateTheme();
    }

    // Apply current theme colors to all UI components
    public void updateTheme() {
        setBackground                      (currentTheme.getPrimary());
        contentPanel.setBackground         (currentTheme.getPrimary());
        gridPanel.setBackground            (currentTheme.getPrimary());
        consol.changeBackground            (currentTheme.getPrimary());
        backgroundPanel.setBackground      (currentTheme.getPrimary());
        consol.setSelectedTextColor        (currentTheme.getPrimary());
        consol.setLogBackground            (currentTheme.getPrimary());
        consol.changeForeground            (currentTheme.getSecondary());
        consol.setSelectionColor           (currentTheme.getSecondary());
        toolbox.setBackground              (currentTheme.getSecondary());
        consol.changeBorder(new LineBorder (currentTheme.getSecondary(), 1));
        toolbox.setBorder  (new LineBorder (currentTheme.getSecondary(), 1));
    }

    // Create wall entities on selected tiles with image selection
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

    // Toggle between flat-top and pointy-top hexagon orientations
    public void toggleGridOrientation() {
        isFlat = !isFlat;
        gridOffset = new Point((int) (gridOffset.x),
                            (int) (gridOffset.y));
        repaint();
    }

    // Update entity preview tiles based on current selection and mouse position
    public void addEntityPreviewTiles(IOHandler IO) {
        if (!selectedEntityTiles.isEmpty() && selectedEntityTiles != null &&
            selectEntity(selectedEntityTiles.get(0)) instanceof Character) {
            if (Character.getOccupiedTiles(tileUnderMouse, selectEntity(selectedEntityTiles.get(0)).getSize(), this) != null) {
                for (Hexagon hex : Character.getOccupiedTiles(tileUnderMouse, selectEntity(selectedEntityTiles.get(0)).getSize(), this)) {
                    if (hex != null && !entityPreviewTiles.contains(hex)) {
                        addEntityPreviewTile(hex);
                        // Calculate movement path if within range
                        if(entityRangeTiles.contains(hex))
                            path = AStar.run(selectedEntityTiles.get(0), hex, this, false);
                    }
                }
            }
        }
    }

    // Get current grid orientation (flat vs pointy top)
    public static boolean isFlat() {
        return isFlat;
    }

    // Find the hexagon closest to a given point
    public Hexagon findClosestHexagon(Point2D point) {
        Hexagon closest = null;
        double minDist = hexSize-1;
        for (Hexagon hex : hexlist.values()) {
            double dist = point.distance(hex.getCenter());
            if (dist < minDist) {
                minDist = dist;
                closest = hex;
            }
        }
        return closest;
    }
}