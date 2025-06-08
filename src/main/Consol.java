package main;

import calc.Dice;
import entities.Character;
import fx.Hexagon;
import fx.Marker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;

public class Consol extends JTextField {
    private boolean Active = true;
    private GraphicsHandler gh;
    private final List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    private String currentInput = "";
    private Marker[] diceMarkers = {};
    private boolean confirm = false;
    
    public Consol(GraphicsHandler gh) {
        super();
        this.gh = gh;
        this.setPreferredSize(new Dimension(200, 32));
        this.setFont(new Font("Arial", Font.PLAIN, 20));
        this.setSelectedTextColor(Color.BLACK);
    }
    public void command(String input) {
        commandHistory.add(input);
        historyIndex = commandHistory.size();
        setText("");
        currentInput = "";
        gh.repaint();
        if (confirm) {

            if(input.toLowerCase().endsWith("y") 
            || input.toLowerCase().startsWith("y")) 
                confirm();
            if(input.toLowerCase().endsWith("n") 
            || input.toLowerCase().startsWith("n")) 
                deny();
            confirm = false;

        } else {
            String[] args = input.split(" ");

            switch(args[0].toLowerCase()) {
                case "quit",       ":q"  -> System.exit(0);
                case "background", ":b"  -> gh.setBackgroundImage();
                case "debug",      ":d"  -> gh.toggleDebugMode();
                case "character",  ":c"  -> character(args);
                case "wall",       ":w"  -> gh.summonWall();
                case "entity",     ":e"  -> gh.summonEntity();
                case "help",       ":h"  -> help(args[1]);
                case "roll",       ":r"  -> roll(args);
                case "darkmode",   ":dm" -> gh.toggleDarkMode();
                case "grid",       ":g"  -> gh.toggleGridOrientation();
                case "gamemaster", ":gm" -> gh.io.toggleGameMaster();
                case "init",       ":i"  -> initiative(args);
                default -> {}
            }
        }
    }
    private void character(String[] args) {
        int size       = 0; // hasValue[0]
        int maxHealth  = 0; // hasValue[1]
        int AC         = 0; // hasValue[2]
        int speed      = 0; // hasValue[3]
        int initiative = 0; // hasValue[4]
        boolean[] hasValue = new boolean[5];
        //runs until all arguments are consumed
        do{
            try {
                switch (args[1].toLowerCase()) {
                    case "d",   "delete"      -> {gh.deleteEntities();                                        }
                    case "s",   "size"        -> {size        = Integer.parseInt(args[2]); hasValue[0] = true;}
                    case "h",   "maxhealth"   -> {maxHealth   = Integer.parseInt(args[2]); hasValue[1] = true;}
                    case "ac",  "armorclass"  -> {AC          = Integer.parseInt(args[2]); hasValue[2] = true;}
                    case "sp",  "speed"       -> {speed       = Integer.parseInt(args[2]); hasValue[3] = true;}
                    case "i",   "initiative"  -> {initiative  = Integer.parseInt(args[2]); hasValue[4] = true;}
                    default -> {}
                }
                args = cutArgs(args);
            } 
            catch (Exception e) {}
            
        } while(args.length >= 3);

        if (!gh.selectedEntityTiles.isEmpty()) {
            for (Hexagon h : gh.selectedEntityTiles){
                Character c = (Character) gh.selectEntity(h);
                if (hasValue[0]) c.setSize(size);
                if (hasValue[1]) c.setMaxHealth(maxHealth);
                if (hasValue[2]) c.setAC(AC);
                if (hasValue[3]) c.setSpeed(speed);
                if (hasValue[4]) c.setInitiative(initiative);
            }
            
        } else {
            gh.spawnCharacter(size, maxHealth, AC, speed, initiative);
        }
    }
    private String[] cutArgs(String[] args) {
        
        String[] temp = new String[args.length - 2];
        temp[0] = args[0];
        
        for(int i = 3; i < args.length; i++) {
            temp[i-2] = args[i];
        }
        return temp;
    }
    private void help(String arg) {
         switch(arg) {
                case "quit",        ":q" -> {}
                case "background",  ":b" -> {}
                case "debug",       ":d" -> {}
                case "character",   ":c" -> {}
                case "roll",        ":r" -> {}
                default -> {}
            }

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
    private void initiative (String[] args) {
        if (args == null) {
            gh.gm.initiative(gh.entities);
        }
        else {
            switch(args[1].toLowerCase()) {
                case "add",     "a" -> {gh.gm.initiative(gh.selectEntity(gh.selectedEntityTiles.get(0)));}
                case "remove",  "r" -> {gh.gm.removeFromInitiative(gh.selectEntity(gh.selectedEntityTiles.get(0)));}
                case "clear",   "c" -> {gh.gm.init.clear();}
                case "show",    "s" -> {gh.gm.showInitiative();}
                default -> {}
            }
        }
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
    public void setFontSize(int s){
        this.setPreferredSize(new Dimension(200, s*8/5));
        this.setFont(new Font("Arial", Font.PLAIN, s));
    }
    public void displayConfirmText(String s) {
        setText(s);
        confirm = true;
    }
    private void confirm() {
        gh.waiter.provideAnswer(true);
    }
    private void deny() {
        gh.waiter.provideAnswer(false); 
    }
}
