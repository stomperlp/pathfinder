package main;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;

public class Consol extends JTextField {
    private boolean Active = true;
    private GraphicsHandler gh;
    private List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    private String currentInput = "";
    
    public Consol() {
        super();
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event instanceof KeyEvent e) {
                switch(e.getKeyCode())
                {
                    case KeyEvent.VK_UP -> 
                    {
                        if (!commandHistory.isEmpty())
                        {
                            if(historyIndex == commandHistory.size())
                            {
                                currentInput = getText();
                            }
                            if(historyIndex > 0) 
                            {
                                historyIndex--;
                                setText(commandHistory.get(historyIndex));
                                setCaretPosition(currentInput.length());
                            }
                        }
                    }
                    case KeyEvent.VK_DOWN -> 
                    {
                        if (historyIndex < commandHistory.size() - 1)
                        {
                            historyIndex++;
                            setText(commandHistory.get(historyIndex));
                        } 
                        else if (historyIndex == commandHistory.size() - 1) {
                            historyIndex = commandHistory.size();
                            setText(currentInput);
                        }
                        setCaretPosition(currentInput.length());
                    }
                    default -> 
                    {
                        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
                            String input = getText();
                            commandHistory.add(input);
                            historyIndex = commandHistory.size();
                            command(input);
                            setText("");
                            currentInput = "";
                            break;
                        }
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }
    public void command(String input) {
        switch(input) {
            case "quit", ":q" -> System.exit(0);
            case "background", ":b" -> gh.setBackgroundImage();
            case "creature", ":c" -> gh.createCreature();
            case "help", ":h" -> help();
            case "debug", ":bg" -> gh.toggleDebugMode();
            default -> {}
        }
    }
    private void help() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'help'");
    }
    public boolean isActive() {
        return Active;
    }
    public void setGraphicsHandler(GraphicsHandler gh) {
        this.gh = gh;
    }
    public void toggleActive() {
        Active = !Active;
    }
}
