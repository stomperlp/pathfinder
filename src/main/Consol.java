package main;

import calc.Dice;
import entities.Character;
import entities.Entity;
import fx.Hexagon;
import fx.Marker;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;

public final class Consol extends JTextField {
    private GraphicsHandler gh;
    private final List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    private String currentInput = "";
    private Marker[] diceMarkers = {};
    private boolean confirm = false;
    

    
    public Consol(GraphicsHandler gh) {
        super();
        this.gh = gh;
        this.setFont(new Font("Arial", Font.PLAIN, 20));
        this.setSelectedTextColor(Color.BLACK);
    }
    public void command(String input) {
        if (input == null || input.isBlank()) return;
        if (!(commandHistory.size() > 1 && input.equals(commandHistory.get(commandHistory.size() - 1))))
            commandHistory.add(input);
        historyIndex = commandHistory.size();
        setText("");
        currentInput = "";
        gh.repaint();
        if (confirm) {
            
            if(input.toLowerCase().endsWith("n") 
            || input.toLowerCase().startsWith("n")) 
                deny();
            else {
                confirm();
            } 
            confirm = false;

        } else {
            String[] args = input.split(" ");

            switch(args[0].toLowerCase()) {
                case "quit",       ":q"  -> System.exit(0);
                case "background", ":b"  -> gh.setBackgroundImage();
                case "debug",      ":d"  -> gh.toggleDebugMode();
                case "character",  ":c"  -> character(args);
                case "wall",       ":w"  -> wall(args);
                case "entity",     ":e"  -> entity(args);
                case "help",       ":h"  -> help(args);
                case "roll",       ":r"  -> roll(args);
                case "darkmode",   ":dm" -> gh.toggleDarkMode();
                case "grid",       ":g"  -> gh.toggleGridOrientation();
                case "gamemaster", ":gm" -> gh.io.toggleGameMaster();
                case "init",       ":i"  -> initiative(args);
                case "clear",      ":cl" -> clear();
                default                  -> gh.consol.addLogMessage("Invalid input: \"" + input + "\" - Try help for a list of commands");
            }
        }
    }
    private void clear() {
        commandHistory.clear();
        gh.consol.clearLogs();
    }
    private void wall(String[] args) {
        String arg = "";
        if(args.length > 1) arg = args[1];
        if(arg.equals("delete") || arg.equals("d")) {
            gh.deleteEntities(1);
        } else {
            gh.summonWall();
        }
    }
    private void entity(String[] args) {
        String arg = "";
        if(args.length > 1) arg = args[1];
        if(arg.equals("delete") || arg.equals("d")) {
            gh.deleteEntities(1);
        } else {
            gh.summonEntity();
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
                    case "d",   "delete"      -> {gh.deleteEntities(0);                                        }
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
    private void help(String[] args) {
        String arg = "";
        if(args.length > 1) arg = args[1];
        switch(arg) {
                case "quit",        ":q" -> {
                    gh.consol.addLogMessage(":q  or quit # Quit the application - No Arguments");
                }
                case "background",  ":b" -> {
                    gh.consol.addLogMessage(":b  or background # Set a background image - No Arguments");
                }
                case "debug",       ":d" -> {
                    gh.consol.addLogMessage(":d  or debug # Toggle debug mode - No Arguments");
                }
                case "character",  ":c"  -> {
                    gh.consol.addLogMessage(":c  or creature # Create or edit a character");
                    gh.consol.addLogMessage(" - d # Delete a Creature");
                    gh.consol.addLogMessage(" - size <int> # set the size of selected Characters");
                    gh.consol.addLogMessage(" - speed <int> # set the speed of selected Characters");
                    gh.consol.addLogMessage(" - armorclass <int> # set the Armor Class of selected Characters");
                    gh.consol.addLogMessage(" - maxHealth <int> # set the Max Health of selected Characters");
                }
                case "wall",       ":w"  -> {
                    gh.consol.addLogMessage(":w  or wall # Place a wall");
                    gh.consol.addLogMessage(" - d # Delete a wall");
                }
                case "entity",     ":e"  -> {
                    gh.consol.addLogMessage(":e  or entity # Place a generic entity (Please use :c or :w)");
                    gh.consol.addLogMessage(" - d # Delete a generic Entity");
                }
                case "help",       ":h"  -> {
                    gh.consol.addLogMessage(":h  or help # Show available commands");
                    gh.consol.addLogMessage(" - <command> # get additional information about a spesific command");
                }
                case "roll",       ":r"  -> {
                    gh.consol.addLogMessage(":r  or roll # Roll dice (e.g., roll 2d6)");
                    gh.consol.addLogMessage(" - <dice> # in the form _d_");
                }
                case "darkmode",   ":dm" -> {
                    gh.consol.addLogMessage(":dm or darkmode # Toggle dark/light mode - No Arguments");
                }
                case "grid",       ":g"  -> {
                    gh.consol.addLogMessage(":g  or grid # Change grid orientation (Instable)");
                    gh.consol.addLogMessage("this might cause errors.");
                }
                case "gamemaster", ":gm" -> {
                    gh.consol.addLogMessage(":gm or gamemaster # Toggle gamemaster mode - No Arguments");
                }
                case "init",       ":i"  -> {
                    gh.consol.addLogMessage(":i  or init # Manage initiative order");
                    gh.consol.addLogMessage("- add # add the selected Characters from the initiative order");
                    gh.consol.addLogMessage("- remove # remove the selected Characters from the initiative order");
                    gh.consol.addLogMessage("- clear # clear the initiative order");
                    gh.consol.addLogMessage("- show # toggles wether or not the initiative placing is shown at the Characters");
                }
                case "clear",      ":cl" -> {
                    gh.consol.addLogMessage(":cl or clear # Clear the command log and history - No Arguments");
                }
                default -> {
                    gh.consol.addLogMessage(":q  or quit # Quit the application");
                    gh.consol.addLogMessage(":b  or background # Set a background image ");
                    gh.consol.addLogMessage(":d  or debug # Toggle debug mode ");
                    gh.consol.addLogMessage(":dm or darkmode # Toggle dark/light mode");
                    gh.consol.addLogMessage(":c  or creature # Create or edit a character");
                    gh.consol.addLogMessage(":w  or wall # Place a wall");
                    gh.consol.addLogMessage(":e  or entity # Place a generic entity (Please use :c or :w)");
                    gh.consol.addLogMessage(":r  or roll # Roll dice");
                    gh.consol.addLogMessage(":g  or grid # Change grid orientation (Instable)");
                    gh.consol.addLogMessage(":gm or gamemaster # Toggle gamemaster mode");
                    gh.consol.addLogMessage(":i  or init # Manage initiative order");
                    gh.consol.addLogMessage(":h  or help # Show available commands");
                    gh.consol.addLogMessage(":cl or clear # Clear the command log and history");
                }
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
        if (args == null || args.length < 2) {
            gh.gm.initiative(gh.entities);
        }
        else {
            switch(args[1].toLowerCase()) {
                case "add",     "a" -> {
                    ArrayList<Entity> selected = new ArrayList<>();
                    for (Hexagon h : gh.selectedEntityTiles) {
                        if (h != null && gh.selectEntity(h) != null) {
                            if (!selected.contains(gh.selectEntity(h))) {
                                selected.add(gh.selectEntity(h));
                            }
                        }
                    }
                    if (!selected.isEmpty())
                        gh.gm.initiative(selected);
                }
                case "remove",  "r" -> {gh.gm.removeFromInitiative(gh.selectEntity(gh.selectedEntityTiles.get(0)));}
                case "clear",   "c" -> {gh.gm.init.clear();                                                              }
                case "show",    "s" -> {gh.gm.toggleShowInitiative();                                                    }
                case "next",    "n" -> {
                    if (gh.gm.init.isEmpty()) return;
                    gh.gm.nextTurn();
                }
                default -> {}
            }
        }
    }  
    public void setGraphicsHandler(GraphicsHandler gh) {
        this.gh = gh;
    }  
    public void arrowUp() {
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
    public void setCurrentInput(String currentInput) {
        this.currentInput = currentInput;
    }
}
