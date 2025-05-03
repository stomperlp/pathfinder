package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GraphicsHandler extends JFrame{
    
    private final IOHandler io;
    protected JLabel label;
    protected JPanel backgroundPanel;
    protected Image backgroundImage;
    protected  int hexSize = 40; // Size of each hexagon
    protected int thickness = 2; // Line thickness
    protected int backgroundRows = 20; //Number of rows the image takes up
    protected int backgroundCols = 20; //Number of columns the image takes up
    protected Point backgroundCenter; 

    protected Point dragStart = null;
    private Point gridOffset = new Point(0, 0);
    protected ArrayList<Path2D> hexlist = new ArrayList<>();
    protected Path2D selectedHex;
    private int zoomFactor = 1;

    public void start()
    {

    }
    private void mouse() 
    {
        addMouseListener(new MouseAdapter() 
		{ 
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
                io.mouseWheelMoved(e);
            }
        });
    }

    public GraphicsHandler() 
    {
        io = new IOHandler(this);
        setTitle("bitti bitti 15 punkte");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400); // Initial size

        setResizable(true);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);
        JPanel GridPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                if(hexSize < 15) return;
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
                // Set line thickness
                g2d.setStroke(new BasicStroke(thickness));
                
                // Calculate hexagon geometry
                double hexWidth = Math.sqrt(3) * hexSize;
                double hexHeight = 2 * hexSize;

                hexlist.clear();
                int firstVisibleCol = (int) Math.floor(( -gridOffset.x - hexWidth) / (2 * hexWidth));
                int lastVisibleCol = (int) Math.ceil((getWidth() - gridOffset.x + hexWidth) / hexWidth);
                
                int firstVisibleRow = (int) Math.floor(( -gridOffset.y - hexHeight) * 2 / hexHeight);
                int lastVisibleRow = (int) Math.ceil((getHeight() - gridOffset.y + hexHeight) * 2 / hexHeight);
                
                // Draw the grid
                for (int row = firstVisibleRow; row <= lastVisibleRow; row++) {
                    for (int col = firstVisibleCol; col <= lastVisibleCol; col++) {
                        
                        double centerX = gridOffset.x + col * hexWidth * Math.sqrt(3);
                        double centerY = gridOffset.y + row * hexHeight * 0.43;
                        
                        // Offset every other row
                        if (row % 2 != 0) {
                            centerX += hexWidth * Math.sqrt(3)/2;
                        }
                        
                        drawHexagon(g2d, centerX, centerY, Color.BLACK);
                    }
                }

                if (selectedHex != null && dragStart == null) {
                    g2d = (Graphics2D) g.create();
                    try {
                        g2d.setStroke(new BasicStroke(thickness+2));
                        g2d.setColor(Color.RED);
                        g2d.draw(selectedHex);
                    } finally {
                        g2d.dispose();
                    }
                }
            }
        };
        GridPanel.setOpaque(false);
        //contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    
                    double imageRatio = (double) backgroundImage.getWidth(null) / backgroundImage.getHeight(null);
                    double formatRatio = (double) backgroundCols / backgroundRows;
                    
                    int drawWidth, drawHeight;
                    
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
        //backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        label = new JLabel("This window is resizable!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        contentPanel.add(label, BorderLayout.CENTER);
        
        // Add a component that shows the current window size
        JLabel sizeLabel = new JLabel("Current size: " + getWidth() + " x " + getHeight());
        sizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        //sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        contentPanel.add(sizeLabel, BorderLayout.SOUTH);
        
        // Add component listener to update size label when resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                sizeLabel.setText("Current size: " + getWidth() + " x " + getHeight());
            }
        });
        backgroundPanel.add(contentPanel);
        backgroundPanel.add(GridPanel);
        add(backgroundPanel);
        
        // Center the window on screen
        setLocationRelativeTo(null);
        setBackgroundImage();
        mouse();

	}
    public final void setBackgroundImage() {
        this.backgroundImage = new ImageIcon(openFileBrowser().getPath()).getImage();
        this.backgroundPanel.repaint();
    }

    private File openFileBrowser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a File");


        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "PNG Images (*.png)", "png");
        fileChooser.setFileFilter(filter);
        
        // Disable the "All files" option
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(this);


        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            label.setText("Selected file: " + selectedFile.getAbsolutePath());
            return selectedFile;
        } 
        else {
            label.setText("File selection cancelled");
        }

        return null;
    }
    private void drawHexagon(Graphics2D g2d, double centerX, double centerY, Color color) {
        Path2D hex = new Path2D.Double();
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            double x = centerX + hexSize * Math.cos(angle);
            double y = centerY + hexSize * Math.sin(angle);
            if (i == 0) {
                hex.moveTo(x, y);
            } else {
                hex.lineTo(x, y);
            }
        }
        hex.closePath();
        
        this.hexlist.add(hex);
        
        g2d.setColor(color);
        g2d.draw(hex);
    }

    public void drawSelectedTile(Path2D hex) {
        selectedHex = hex;
        repaint();
    }
    
    public void drag(MouseEvent e) {
        if (dragStart != null) {
            // Calculate drag distance
            int dx = e.getX() - dragStart.x;
            int dy = e.getY() - dragStart.y;
            
            // Update grid offset
            gridOffset.translate(dx, dy);
            outOfBoundsCorrection(dx, dy);
            // Update drag start point for next movement
            dragStart = e.getPoint();
            System.out.println(gridOffset);
            repaint();
        }
    }    
    private void outOfBoundsCorrection(int dx, int dy){

        Point upLeftCorner = new Point(0,0);
        Point upRightCorner = new Point(getWidth(),0);
        Point downLeftCorner = new Point(0,getHeight());
        Point downRightCorner = new Point(getWidth(),getHeight());

        if (gridOffset.x > getWidth()) {
            gridOffset.setLocation(getWidth(), gridOffset.y);
        }
        if (-gridOffset.x > backgroundImage.getWidth(this)) {
            gridOffset.setLocation(-backgroundImage.getWidth(this), gridOffset.y);
        }
        if (gridOffset.y > getHeight()) {
            gridOffset.setLocation(gridOffset.x, getHeight());
        }
        if (-gridOffset.y > backgroundImage.getHeight(this)) {
            gridOffset.setLocation(gridOffset.x, -backgroundImage.getHeight(this));
        }
    }
    public void zoom(int notches) {
        if ((notches < 0 && hexSize < 200) || (notches > 0 && hexSize > 10)) {
            hexSize -= notches*Math.max((hexSize*zoomFactor/20),1);
        } 
        thickness = hexSize/30;
		selectedHex = null;
        System.out.println(hexSize + "|" + (hexSize/20));
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
    public ArrayList<Path2D> getHexlist() {
        return hexlist;
    }
}
