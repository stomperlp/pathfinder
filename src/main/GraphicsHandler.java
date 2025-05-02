package main;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import tools.*;

public class GraphicsHandler extends JFrame{
    
    private IOHandler io;
    private JLabel label;
    private JPanel backgroundPanel;
    private Image backgroundImage;
    private int hexSize = 40; // Size of each hexagon
    private int rows;     // Number of rows
    private int cols;     // Number of columns
    private int thickness = 2; // Line thickness
    private int backgroundHeight = 10; //Number of rows the image takes up
    private int backgroundWidth = 10; //Number of columns the image takes up
    
    protected Point dragStart = null;
    private Point gridOffset = new Point(0, 0);
    protected ArrayList<Polygon> hexlist = new ArrayList<>();
    protected Polygon selectedHex;

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
    }

    public GraphicsHandler() 
    {
        io = new IOHandler(this);
        setTitle("Resizable Window Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400); // Initial size

        setResizable(true);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);
        JPanel GridPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Set line thickness
                g2d.setStroke(new BasicStroke(thickness));
                
                // Calculate hexagon geometry
                double hexWidth = Math.sqrt(3) * hexSize;
                double hexHeight = 2 * hexSize;
                

                cols = (int)(getWidth()*0.75/hexWidth)+3;
                rows = (int)(getHeight()+1*2/hexHeight)+3;

                // Calculate starting position to center the grid
                double startX = -2*hexWidth + Calc.preciseMod(gridOffset.x, hexWidth * 1.75);
                double startY = -2*hexHeight + Calc.preciseMod(gridOffset.y, hexHeight * 0.85);

                hexlist.clear();
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        // Calculate hexagon center position
                        double x =  startX + col * hexWidth * 0.85*2;
                        double y =  startY + row * hexHeight * 0.444;
                        
                        // Offset every other row
                        if (row % 2 == 1) {
                            x += hexWidth*0.85;
                        }
                        
                        drawHexagon(g2d, x, y, Color.BLACK);
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
                    // Scale image to fit the grid
                    double hexWidth = Math.sqrt(3) * hexSize;
                    double hexHeight = 2 * hexSize;
                    
                    double imageRatio = (double) backgroundImage.getWidth(null) / backgroundImage.getHeight(null);
                    double formatRatio = (double) backgroundWidth / backgroundHeight;
                    
                    int drawWidth, drawHeight;
                    
                    drawHeight = (int) hexHeight * backgroundHeight;
                    drawWidth = (int) (drawHeight * imageRatio * formatRatio);
                    
                    int x = (int) ((hexWidth * backgroundWidth - drawWidth) / 2 + gridOffset.getX());
                    int y = (int) ((hexHeight * backgroundHeight - drawHeight) / 2 + gridOffset.getY());
                    
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

    public void drawHexagon(Graphics2D g2d, double centerX, double centerY, Color color) {
        
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            double x = centerX + hexSize * Math.cos(angle);
            double y = centerY + hexSize * Math.sin(angle);
            hexagon.addPoint((int) x, (int) y);
        }
        this.hexlist.add(hexagon);
        
        g2d.setColor(color);
        g2d.draw(hexagon);
    }

    public void drawSelectedTile(Polygon hex) {
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
            
            // Update drag start point for next movement
            dragStart = e.getPoint();
            System.out.println(gridOffset);
            repaint();
        }
    }    

    public void setHexSize(int size) {
        this.hexSize = size;
        repaint();
    }
    public void setRows(int rows) {
        this.rows = rows;
        repaint();
    }
    public void setCols(int cols) {
        this.cols = cols;
        repaint();
    } 
    public void setThickness(int thickness) {
        this.thickness = thickness;
        repaint();
    }
    public ArrayList<Polygon> getHexlist() {
        return hexlist;
    }
}
