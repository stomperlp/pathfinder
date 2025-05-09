package main;

import fx.Marker;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import tools.Dice;

public class Consol extends JTextField {
    private boolean Active = true;
    private GraphicsHandler gh;
    private List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    private String currentInput = "";
    private Marker[] diceMarkers = {};
    
    public Consol() {
        super();
        
    }
    public void command(String input) {
        String[] inputSegments = input.split(" ");
        switch(inputSegments[0]) {
            case "quit", ":q" -> System.exit(0);
            case "background", ":b" -> gh.setBackgroundImage();
            case "creature", ":c" -> gh.createCreature();
            case "help", ":h" -> help(inputSegments[1]);
            case "debug", ":d" -> gh.toggleDebugMode();
            case "roll", ":r" -> roll(inputSegments);
            default -> {}
        }
        commandHistory.add(input);
        historyIndex = commandHistory.size();
        setText("");
        currentInput = "";
    }
    private void help(String arg) {

    }
    private void roll(String[] inputSegments) {

        for(Marker m : diceMarkers) gh.markers.remove(m);
        int[] roll = new Dice(inputSegments[1]).roll();
        diceMarkers = new Marker[roll.length];

        for (int i = 1; i < roll.length; i++) {
            Marker m = new Marker(
                roll[i],
                new Point((int)((gh.getWidth()-50)*Math.random()-25), (int)((gh.getHeight()-50)*Math.random()-25)),
                Marker.DICE, 
                false
            );
            gh.markers.add(m);
            diceMarkers[i] = m;
        }
        Marker m = new Marker(
            roll[0],
            null,
            Marker.DICERESULT, 
            false
        );
        gh.markers.add(m);
        diceMarkers[0] = m;
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
    public void arrowUp() {
        if (!gh.consol.commandHistory.isEmpty())
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
    public void arrowDown() {
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
}
