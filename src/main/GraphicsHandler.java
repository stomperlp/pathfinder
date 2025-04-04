package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GraphicsHandler extends JFrame{
    
    IOHandler iOHandler;
    private JLabel label;
    private JPanel backgroundPanel;
    private Image backgroundImage;
    private int hexSize = 40; // Size of each hexagon
    private int rows;     // Number of rows
    private int cols;     // Number of columns
    private int thickness = 2; // Line thickness
    ArrayList<Polygon> hexlist = new ArrayList();

    public void start()
    {
        System.err.println("geht doch du bastard");
    }
    private void mouse() 
    {
        addMouseListener(new MouseAdapter() 
		{ 
			public void mouseClicked(MouseEvent e) 
			{ 
				iOHandler.mouseEvent(e);
			} 
		});
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                iOHandler.mouseMoved(e);
            }
        });
    }

    public GraphicsHandler() 
    {
        iOHandler = new IOHandler();
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
                

                rows = (int)(getHeight()*2/hexHeight)+4;
                cols = (int)(getWidth()/hexWidth)+1;

                // Calculate starting position to center the grid
                //int startX = (int) (getWidth() / 2 - (cols * hexWidth) / 2); 
                //int startY = (int) (getHeight() / 2 - (rows * hexHeight * 0.75) / 2);
                
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        // Calculate hexagon center position
                        double x =  col * hexWidth * 0.85*2;
                        double y =  row * hexHeight * 0.444;
                        
                        // Offset every other row
                        if (row % 2 == 1) {
                            x += hexWidth*0.85;
                        }
                        
                        drawHexagon(g2d, x, y);
                    }
                }
            }
        };
        contentPanel.setOpaque(false);
        //contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Scale image to fit panel while maintaining aspect ratio
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    
                    double imageRatio = (double) backgroundImage.getWidth(null) / backgroundImage.getHeight(null);
                    double panelRatio = (double) panelWidth / panelHeight;
                    
                    int drawWidth, drawHeight;
                    
                    if (panelRatio > imageRatio) {
                        drawHeight = panelHeight;
                        drawWidth = (int) (drawHeight * imageRatio);
                    } else {
                        drawWidth = panelWidth;
                        drawHeight = (int) (drawWidth / imageRatio);
                    }
                    
                    int x = (panelWidth - drawWidth) / 2;
                    int y = (panelHeight - drawHeight) / 2;
                    
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

    private void drawHexagon(Graphics2D g2d, double centerX, double centerY) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            double x = centerX + hexSize * Math.cos(angle);
            double y = centerY + hexSize * Math.sin(angle);
            hexagon.addPoint((int) x, (int) y);
        }
        this.hexlist.add(hexagon);
        
        g2d.setColor(Color.BLACK);
        g2d.draw(hexagon);
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

}
