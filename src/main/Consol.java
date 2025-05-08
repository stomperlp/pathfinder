package main;

import javax.swing.JTextField;

public class Consol extends JTextField {
    private boolean Active = true;
    private GraphicsHandler gh;
    
    public Consol() {
        super();
        
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
