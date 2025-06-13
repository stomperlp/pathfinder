package main;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * A panel that displays console output and handles user input.
 * Manages the visual display of command history and provides scrolling functionality.
 */
public class ConsolPanel extends JPanel {
    // Reference to the main console input component
    protected final Consol consol;
    
    // Log display components
    private final ArrayList<JLabel> logLabels = new ArrayList<>();
    private final ArrayList<String> logs = new ArrayList<>();
    
    // Graphics handler reference
    private final GraphicsHandler gh;
    
    // Display state variables
    private int scrollLog = 0;       // Current scroll position
    private boolean active = true;   // Whether panel is active/visible
    private boolean showLog = true;  // Whether to show log messages
    
    // Panel containing log messages
    private final JPanel logPanel = new JPanel();

    /**
     * Creates a new console panel.
     * @param gh The graphics handler this console belongs to
     */
    public ConsolPanel(GraphicsHandler gh) {
        super(new BorderLayout());

        this.gh = gh;
        consol = new Consol(gh);

        // Set up log panel layout and styling
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Initialize with 5 empty labels for log display
        for (int i = 0; i < 5; i++) {
            JLabel label = new JLabel(" ");
            logLabels.add(label);
            logPanel.add(label);
        }
        
        // Add components to main panel
        add(logPanel, BorderLayout.CENTER);
        add(consol, BorderLayout.SOUTH);
        
        // Set initial font size
        setFontSize(16);
    }

    /**
     * Custom painting of the panel.
     * @param g The Graphics object to paint with
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Set transparency level
        g2d.setComposite(AlphaComposite.SrcOver.derive(1f));
    }

    /**
     * Adds a new message to the log display.
     * @param message The message to add
     */
    public void addLogMessage(String message) {
        logs.add(message);
        scrollLog(1);  // Auto-scroll to show new message
        logUpdate();
    }

    /**
     * Scrolls the log display by specified amount.
     * @param notches Number of lines to scroll (positive = down, negative = up)
     */
    public void scrollLog(int notches) {
        scrollLog += notches;
        
        // Enforce scroll boundaries
        if(scrollLog < 0) {
            scrollLog = 0;
        }
        if(scrollLog > logs.size() - logLabels.size()) {
           scrollLog = logs.size() - logLabels.size();
        }
        
        logUpdate();
    }

    /**
     * Updates the visible log messages based on current scroll position.
     */
    private void logUpdate() {
        int i = scrollLog;
        for(JLabel l : logLabels) {
            try {
                l.setText(logs.get(i));
            } catch (Exception e) {
                l.setText(" ");  // Show blank if no message available
            }
            i++;
        }
        repaint();
    }

    /**
     * Checks if the panel is currently active.
     * @return true if the panel is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Toggles the panel's active/visible state.
     */
    public void toggleActive() {
        active = !active;
        setVisible(active);
    }

    /**
     * Sets the font size for all text in the console.
     * @param size The new font size in points
     */
    public void setFontSize(int size) {
        Font font = new Font("Arial", Font.PLAIN, size);
        this.setFont(font);
        
        // Update font for all child components
        for (JLabel label : logLabels) {
            label.setFont(font);
        }
        consol.setFont(font);
        
        // Calculate and set preferred size based on font size
        int height = 1 + size * 8 / 5 * (showLog ? logLabels.size() + 1 : 1);
        this.setPreferredSize(new Dimension(200, height));
    }

    /**
     * Changes the font size by specified delta.
     * @param delta Amount to change font size by (positive = larger, negative = smaller)
     */
    public void changeFontSize(int delta) {
        int newSize = getFont().getSize() + delta;
        setFontSize(newSize);
    }

    /**
     * Toggles visibility of the log message display.
     */
    public void toggleLog() {
        showLog = !showLog;
        logPanel.setVisible(showLog);
        setFontSize(getFont().getSize());  // Recalculate size
        gh.updateTheme();
        revalidate();
        repaint();
    }

    /**
     * Sets the text selection color for the console input.
     * @param c The color to use for selected text
     */
    public void setSelectedTextColor(Color c) {
        consol.setSelectedTextColor(c);
    }

    /**
     * Sets the selection highlight color for the console input.
     * @param c The color to use for selection background
     */
    public void setSelectionColor(Color c) {
        consol.setSelectionColor(c);
    }
    
    /**
     * Gets the underlying console component.
     * @return The Consol instance
     */
    public Consol getconsol() {
        return consol;
    }

    /**
     * Changes the foreground color for all components.
     * @param c The new foreground color
     */
    public void changeForeground(Color c) {
        super.setForeground(c);
        consol.setForeground(c);
        logPanel.setForeground(c);
        for(JLabel l : logLabels) {
            l.setForeground(c);
        }
    }

    /**
     * Changes the background color for all components.
     * @param c The new background color
     */
    public void changeBackground(Color c) {
        super.setBackground(c);
        consol.setBackground(c);
        for(JLabel l : logLabels) {
            l.setBackground(c);
        }
    }

    /**
     * Sets the background color for the log panel with transparency.
     * @param c The base background color (will be made semi-transparent)
     */
    public void setLogBackground(Color c) {
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 220);
        logPanel.setBackground(c);
    }

    /**
     * Clears all log messages.
     */
    public void clearLogs() {
        logs.clear();
        logUpdate();
    }

    /**
     * Changes the border style for the panel and console.
     * @param b The new border to use
     */
    public void changeBorder(LineBorder b) {
        super.setBorder(b);
        consol.setBorder(b);
    }
}