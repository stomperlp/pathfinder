package main;
import fx.*;
import entities.Entity;
import entities.Character;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class GraphicsHandler extends JFrame{

    protected final IOHandler io;
    protected JPanel backgroundPanel;
    protected JPanel gridPanel;
    protected JPanel contentPanel; 
    protected JPanel fxPanel;
    protected Consol consol;
    protected Image backgroundImage;
    protected int hexSize = 40; // Initial zoom
    protected int thickness = 2; // Hexagon Line thickness
    protected int backgroundRows = 20; //Number of rows the image takes up
    protected int backgroundCols = 20; //Number of columns the image takes up
    protected Point backgroundCenter; 
    int drawWidth, drawHeight;

    protected Point dragStart = null;
    Point gridOffset = new Point(0, 0);
    protected ArrayList<Hexagon> hexlist = new ArrayList<>();
    protected ArrayList<Marker> markers = new ArrayList<>();
    protected ArrayList<Entity> entities = new ArrayList<>();
    protected Hexagon tileUnderMouse;
    protected Hexagon selectedTile;
    protected Hexagon selectedEntityTile;
    protected int zoomFactor = 1;
    protected boolean debugMode = false;
    protected GameHandler gm;

    
    private void inputListener()
    {
        addMouseListener(new MouseAdapter() { 
			public void mousePressed(MouseEvent e) 
			{ 
				io.mousePressed(e);
			} 

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
                    backgroundPanel 
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
                io.mouseDragged(e);
            }
        });
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
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
            }
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

        setResizable(true);

        contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (Entity c : entities) {
                    if (c instanceof Character){
                    }
                    if(c.getTile() == null) {
                        g2d.fillOval((int)c.getLocation().getX()-hexSize/10, (int)c.getLocation().getY()-hexSize/10, hexSize/10, hexSize/10);
                        continue;
                    }
                    Hexagon h = c.getTile();
                    c.debugUpdate();
                    boolean gridPointFound = false;
                    for(Hexagon hex : hexlist) {
                        if (h.getGridPoint().equals(hex.getGridPoint())) {
                            c.setTile(hex);
                            c.setLocation(hex.getCenter());
                            gridPointFound = true;
                            break;
                        }
                    }
                    if(!gridPointFound) continue;
                    g2d.drawImage(
                        c.getImage(), 
                        (int) c.getLocation().getX()-(int)(Math.sqrt(3)*hexSize/2), 
                        (int) c.getLocation().getY()-(int)(Math.sqrt(3)*hexSize/2), 
                        (int)(c.getDrawSize()*hexSize), 
                        (int)(c.getDrawSize()*hexSize), this
                    );
                }

            }
        };
        contentPanel.setOpaque(false);
        gridPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                //stops grid from generation when zoomed out too much
                if(hexSize < 15) return;

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

                int firstVisibleCol = (int) Math.floor(( -gridOffset.x - hexWidth) / (Math.sqrt(3) * hexWidth))-2;
                int lastVisibleCol = (int) Math.ceil((getWidth() - gridOffset.x + hexWidth) / (Math.sqrt(3)*hexWidth))+2;
                
                int firstVisibleRow = (int) Math.floor(( -gridOffset.y - hexHeight) * 7/3 / hexHeight);
                int lastVisibleRow = (int) Math.ceil((getHeight() - gridOffset.y + hexHeight) * 7/3 / hexHeight)+2;
                
                // Draw the grid
                for (int row = firstVisibleRow; row <= lastVisibleRow; row++) {
                    for (int col = firstVisibleCol; col <= lastVisibleCol; col++) {
                        
                        double centerX = gridOffset.x + col * hexWidth * Math.sqrt(3);
                        double centerY = gridOffset.y + row * hexHeight * 0.43; // No clue why 0.43 but don't touch it
                        
                        // Offset every other row
                        if (row % 2 != 0) {
                            centerX += hexWidth * Math.sqrt(3)/2;
                        }
                        Point2D p = new Point2D.Double(centerX, centerY);
                        Hexagon hex = new Hexagon(p, hexSize, new Point(row, col));
        
                        g2d.setColor(Color.BLACK);
                        g2d.draw(hex.getShape());
                        hexlist.add(hex);
                    }
                }
                for(Hexagon hex : hexlist) {
                    if (selectedTile == null) break;
                    if (selectedTile.getGridPoint().equals(hex.getGridPoint())) {
                        selectedTile = hex;
                        break;
                    }
                }
                for(Hexagon hex : hexlist) {
                    if (selectedEntityTile == null) break;
                    if (selectedEntityTile.getGridPoint().equals(hex.getGridPoint())) {
                        selectedEntityTile = hex;
                        break;
                    }
                }
                if (tileUnderMouse != null && dragStart == null) {
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.BLACK);
                    g2d.draw(tileUnderMouse.getShape());
                                    }
                if (selectedTile != null && !selectedTile.equals(selectedEntityTile)) {
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.RED);
                    g2d.draw(selectedTile.getShape());
                }
                if (selectedEntityTile != null) {
                    g2d.setStroke(new BasicStroke(thickness+2));
                    g2d.setColor(Color.BLUE);
                    g2d.draw(selectedEntityTile.getShape());
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
                            g2d.setColor(Color.BLACK);
                            g2d.setFont(new Font("Arial", Font.BOLD, 18));
                            g2d.drawString(m.getText(), (int)m.getPoint().getX(),(int) m.getPoint().getY());
                        }
                        case Marker.DICE -> {
                            int digits = m.getText().length();
                            g2d.setColor(Color.RED);
                            g2d.fillRoundRect((int)m.getPoint().getX(), (int)m.getPoint().getY(), 16 +16*digits, 16+16*digits, digits*2, digits*2);

                            g2d.setColor(Color.BLACK);
                            g2d.setFont(new Font("Arial", Font.BOLD, 32));
                            g2d.drawString(m.getText(), (int)m.getPoint().getX() + 8, (int)m.getPoint().getY() + 20 + 8*digits);
                        }
                        case Marker.DICERESULT -> {
                            g2d.setColor(Color.BLACK);
                            g2d.setFont(new Font("Arial", Font.BOLD, 50));
                            g2d.drawString(m.getText(), getWidth()/2, getHeight()/2);
                        }
                    }
                }
                for (Hexagon h : hexlist) {
                    if (!debugMode) break;
                    g2d.setColor(Color.RED);
                    g2d.fillOval((int)h.getCenter().getX()-hexSize/10, (int)h.getCenter().getY()-hexSize/10, hexSize/10, hexSize/10);
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, hexSize/3));
                    String gridPoint = "[" + h.getGridPoint().x + "|" + h.getGridPoint().y + "]";
                    int digits = gridPoint.length();
                    g2d.drawString(
                        gridPoint, 
                        (int)h.getCenter().getX()-hexSize/3 - 2*digits, 
                        (int)h.getCenter().getY()+hexSize/5
                    );

                }
            }
        };
        fxPanel.setOpaque(false);
        backgroundPanel = new JPanel(new BorderLayout()) {
        @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {

                    double imageRatio = (double) backgroundImage.getWidth(null) / backgroundImage.getHeight(null);
                    double formatRatio = (double) backgroundCols / backgroundRows;
                    
                    drawHeight = (int) 2 * hexSize * backgroundCols;
                    drawWidth = (int) (drawHeight * imageRatio * formatRatio);

                    int x = (int) gridOffset.getX();
                    int y = (int) gridOffset.getY();

                    int centerX = backgroundImage.getWidth(this)/2;
                    int centerY = backgroundImage.getHeight(this)/2;

                    backgroundCenter = new Point(centerX, centerY);

                    g.drawImage(backgroundImage, x, y, drawWidth, drawHeight, this);
                }
            }
        };
        consol = new Consol();
        consol.setGraphicsHandler(this);
        
        backgroundPanel.add(contentPanel);
        contentPanel.add(gridPanel);
        backgroundPanel.add(consol, BorderLayout.SOUTH);
        gridPanel.add(fxPanel);
        add(backgroundPanel);
        
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
    public void drawSelectedTile(Hexagon hex) {
        selectedTile = hex;
        repaint();
    }
    public void drawSelectedEntityTile(Hexagon hex) {
        selectedEntityTile = hex;
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

        if (gridOffset.x > getWidth()) {
            gridOffset.setLocation(getWidth(), gridOffset.y);
        }
        // Bound to the right
        if (-gridOffset.x > drawWidth) {
            gridOffset.setLocation(-drawWidth, gridOffset.y);
        }
        // Bound at the top
        if (gridOffset.y > getHeight()) {
            gridOffset.setLocation(gridOffset.x, getHeight());
        }
        //Bound at the bottom
        if (-gridOffset.y > drawHeight) {
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
        if ((notches < 0 && hexSize < 200) || (notches > 0 && hexSize > 10)) {
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
    public ArrayList<Hexagon> getHexlist() {
        return hexlist;
    }
    public void createCharacter() {
        Hexagon tile;
        // tile the character spawns on
        if (selectedTile != null) tile = selectedTile;
        else if (tileUnderMouse != null) tile = tileUnderMouse;
        else tile = gm.findClosestHexagon(new Point2D.Double(getWidth()/2, getHeight()/2));
        
        if (tileUnderMouse != null) {
            Character c = new Character(
                this,
                new ImageIcon(io.openFileBrowser().getPath()).getImage(),
                tile, 
                tile.getCenter(),
                0, 0, 0, 0, 0, Character.NORMAL
            );
            entities.add(c);
        } else {
            System.err.println("No valid hexagon found for character placement!");
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
}