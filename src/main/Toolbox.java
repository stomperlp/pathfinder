package main;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import tools.Tool;

public class Toolbox extends JPanel{

    private int scroll;
    private ArrayList<Tool> tools = new ArrayList<>();
    protected Tool selectedTool;

    public Toolbox() {
        super();
        this.setPreferredSize(new Dimension(32, 200));
        try {
            initializeTools();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        setBackground(Color.BLACK);
        g2d.setComposite(AlphaComposite.SrcOver.derive(1f));
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        int height = 0;
        for (Tool tool : getTools()) {
            g2d.drawImage(tool.getIcon(), 1, height*32, 30, 30, this);
            height++;
        }

        if(selectedTool != null) {
            
            g2d.draw(selectedTool.getHitbox());
        }

    }
    public void initializeTools() throws IOException {
        //generate all tools
        //drag tool
        tools.add(new Tool(
            ImageIO.read(new File("pathfinder/src/resources/images/toolIcons/move.png")),
            Tool.DRAG_MODE,
            0
        ));
        //length tool
        tools.add(new Tool(
            ImageIO.read(new File("pathfinder/src/resources/images/toolIcons/move.png")),
            Tool.LENGTH_MODE,
            1
        ));
    }
    public ArrayList<Tool> getTools() {
        return tools;
    }
    public void setTools(ArrayList<Tool> tools) {
        this.tools = tools;
    }
}
