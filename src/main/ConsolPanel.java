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

public class ConsolPanel extends JPanel {
    protected final Consol consol;
    private final ArrayList<JLabel> logLabels = new ArrayList<>();
    private final ArrayList<String> logs      = new ArrayList<>();
    private final GraphicsHandler gh;
    private int scrollLog = 0;
    private boolean active = true;
    private boolean showLog = true;
    private final JPanel logPanel = new JPanel();

    public ConsolPanel(GraphicsHandler gh) {
        super(new BorderLayout());
        
        this.gh = gh;
        consol = new Consol(gh);
        
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Initialize with 5 empty labels
        for (int i = 0; i < 5; i++) {
            JLabel label = new JLabel(" ");
            logLabels.add(label);
            logPanel.add(label);
        }
        // Add components to main panel
        add(logPanel, BorderLayout.CENTER);
        add(consol, BorderLayout.SOUTH);
        
        setFontSize(16);
    }
    @Override
    protected void paintComponent(Graphics g) {

            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Grid Transparency
            g2d.setComposite(AlphaComposite.SrcOver.derive(1f));
    }

    public void addLogMessage(String message) {
        // Shift existing messages up
        logs.add(message);
        scrollLog(1);
        logUpdate();
    }

    public void scrollLog(int notches) {
        scrollLog += notches;
        logUpdate();
        if(scrollLog < 0) {
            scrollLog = 0;
        }
        if(scrollLog > logs.size() - logLabels.size()) {
            scrollLog = logs.size() - logLabels.size();
        }
    }

    private void logUpdate() {
        int i = scrollLog;
        for(JLabel l : logLabels) {
            try {
                l.setText(logs.get(i));
            } catch (Exception e) {
                l.setText(" ");
            }
            i++;
        }
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        active = !active;
        setVisible(active);
    }

    public void setFontSize(int size) {
        Font font = new Font("Arial", Font.PLAIN, size);
        this.setFont(font);
        
        // Set font for all components
        for (JLabel label : logLabels) {
            label.setFont(font);
        }
        consol.setFont(font);
        
        // Calculate preferred size
        int height = 1 + size * 8 / 5 * (showLog ? logLabels.size() + 1 : 1);
        this.setPreferredSize(new Dimension(200, height));
    }

    public void changeFontSize(int delta) {
        int newSize = getFont().getSize() + delta;
        setFontSize(newSize);
    }

    public void toggleLog() {
        showLog = !showLog;
        logPanel.setVisible(showLog);
        setFontSize(getFont().getSize());
        gh.updateTheme();
        revalidate();
        repaint();
    }

    public void setSelectedTextColor(Color c) {
        consol.setSelectedTextColor(c);
    }

    public void setSelectionColor(Color c) {
        consol.setSelectionColor(c);
    }
    
    public Consol getconsol() {
        return consol;
    }
    public void changeForeground(Color c) {
        super.setForeground(c);
        consol.setForeground(c);
        logPanel.setForeground(c);
        for(JLabel l : logLabels) {
            l.setForeground(c);
        }
    }
    public void changeBackground(Color c) {
        super.setBackground(c);
        consol.setBackground(c);
        for(JLabel l : logLabels) {
            l.setBackground(c);
        }
        
    }
    public void setLogBackground(Color c) {
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 220);
        logPanel.setBackground(c);

    }
    public void clearLogs() {
        logs.clear();
        logUpdate();
    }

    public void changeBorder(LineBorder b) {
        super.setBorder(b);
        consol.setBorder(b);
    }
}