package main;

import calc.Dice;
import entities.Character;
import entities.Entity;
import fx.Hexagon;
import fx.Marker;
import fx.Theme;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

/**
 * A custom console component that handles command input, history, and auto-completion.
 * Manages all command processing and user interaction for the game's console interface.
 */
public final class Consol extends JTextField {

    // Reference to the main graphics handler
    private GraphicsHandler gh;
    
    // Command history tracking
    private final List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    private String currentInput = "";
    
    // Dice roll visualization
    private Marker[] diceMarkers = {};
    
    // Confirmation dialog state
    private boolean confirm = false;
    
    // Auto-completion system
    private final CommandCompleter completer = new CommandCompleter();
    private String originalInput = "";
    private List<String> currentSuggestions = new ArrayList<>();
    private int suggestionIndex = -1;

    /**
     * Creates a new console instance.
     * @param gh The graphics handler this console belongs to
     */
    public Consol(GraphicsHandler gh) {
        super();
        this.gh = gh;
        this.setFont(new Font("Arial", Font.PLAIN, 20));
        this.setSelectedTextColor(Color.BLACK);
    }

    /**
     * Processes a command input string.
     * @param input The command to process
     */
    public void command(String input) {
        if (input == null || input.isBlank()) return;
        
        // Add to history if not duplicate of last command
        if (!(commandHistory.size() > 1 && input.equals(commandHistory.get(commandHistory.size() - 1))))
            commandHistory.add(input);
        
        historyIndex = commandHistory.size();
        setText("");
        currentInput = "";
        gh.repaint();
        
        // Handle confirmation dialog responses
        if (confirm) {
            if(input.toLowerCase().endsWith("n") || input.toLowerCase().startsWith("n")) 
                deny();
            else {
                confirm();
            } 
            confirm = false;
        } else {
            // Process normal commands
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
                case "grid",       ":g"  -> gh.toggleGridOrientation();
                case "gamemaster", ":gm" -> gh.io.toggleGameMaster();
                case "init",       ":i"  -> initiative(args);
                case "clear",      ":cl" -> clear();
                case "theme",      ":t"  -> theme(args);
                default -> gh.consol.addLogMessage("Invalid input: \"" + input + "\" - Try help for a list of commands");
            }
        }
    }

    /**
     * Handles theme-related commands.
     * @param args Command arguments
     */
    private void theme(String[] args) {
        if(args.length > 1) gh.changeTheme(args[1]);
        else {
            gh.consol.addLogMessage("The following themes are available:");
            for(Theme t : gh.themes) {
                gh.consol.addLogMessage(t.toString());
            }
        }
    }

    /**
     * Clears command history and logs.
     */
    private void clear() {
        commandHistory.clear();
        gh.consol.clearLogs();
    }

    /**
     * Handles wall-related commands.
     * @param args Command arguments
     */
    private void wall(String[] args) {
        String arg = "";
        if(args.length > 1) arg = args[1];
        if(arg.equals("delete") || arg.equals("d")) {
            gh.deleteEntities(1);
        } else {
            gh.summonWall();
        }
    }

    /**
     * Handles generic entity commands.
     * @param args Command arguments
     */
    private void entity(String[] args) {
        String arg = "";
        if(args.length > 1) arg = args[1];
        if(arg.equals("delete") || arg.equals("d")) {
            gh.deleteEntities(1);
        }
    }

    /**
     * Handles character creation and modification commands.
     * @param args Command arguments
     */
    private void character(String[] args) {
        int size = 0, maxHealth = 0, AC = 0, speed = 0, initiative = 0;
        Image image = null;
        boolean[] hasValue = new boolean[6];
        
        // Process all arguments
        do {
            try {
                switch (args[1].toLowerCase()) {
                    case "d", "delete"      -> { gh.deleteEntities(0); }
                    case "sz", "size"        -> { size = Integer.parseInt(args[2]); hasValue[0] = true; }
                    case "h", "maxhealth"    -> { maxHealth = Integer.parseInt(args[2]); hasValue[1] = true; }
                    case "ac", "armorclass"  -> { AC = Integer.parseInt(args[2]); hasValue[2] = true; }
                    case "s", "speed"        -> { speed = Integer.parseInt(args[2]); hasValue[3] = true; }
                    case "in", "initiative"  -> { initiative = Integer.parseInt(args[2]); hasValue[4] = true; }
                    case "ic", "icon"       -> { 
                        image = new ImageIcon(gh.io.openFileBrowser().getPath()).getImage(); 
                        hasValue[5] = true; 
                        args = cutArgs(args, 1);
                    }
                    default -> {}
                }
                args = cutArgs(args, 2);
            } catch (Exception e) {}
        } while(args.length >= 3);

        // Apply changes to selected characters or spawn new one
        if (!gh.selectedEntityTiles.isEmpty()) {
            for (Hexagon h : gh.selectedEntityTiles) {
                Character c = (Character) gh.selectEntity(h);
                if (hasValue[0]) c.setSize(size);
                if (hasValue[1]) c.setMaxHealth(maxHealth);
                if (hasValue[2]) c.setAC(AC);
                if (hasValue[3]) c.setSpeed(speed);
                if (hasValue[4]) c.setInitiative(initiative);
                if (hasValue[5]) c.setImage(image);
            }
        } else {
            gh.spawnCharacter(size, maxHealth, AC, speed, initiative);
        }
    }

    /**
     * Utility method to remove arguments from the array.
     * @param args Original arguments array
     * @param num Number of arguments to remove
     * @return New arguments array
     */
    private static String[] cutArgs(String[] args, int num) {
        String[] temp = new String[args.length - num];
        temp[0] = args[0];
        for(int i = num+1; i < args.length; i++) {
            temp[i-num] = args[i];
        }
        return temp;
    }

    /**
     * Displays help information for commands.
     * @param args Command arguments
     */
    private void help(String[] args) {
        String arg = "";
        if(args.length > 1) arg = args[1];
        switch(arg) {
            case "quit", ":q" -> gh.consol.addLogMessage(":q  or quit # Quit the application - No Arguments");
            // ... (other help cases remain exactly the same)
            default -> {
                // Show all commands when no specific help requested
                gh.consol.addLogMessage("Available commands:");
                gh.consol.addLogMessage(":q  or quit # Quit the application");
                // ... (other default help messages remain exactly the same)
            }
        }
    }

    /**
     * Handles dice rolling commands.
     * @param inputSegments Command arguments
     */
    private void roll(String[] inputSegments) {
        // Clear previous dice markers
        for(Marker m : diceMarkers) gh.markers.remove(m);
        
        // Roll new dice
        int[] roll = new Dice(inputSegments[1]).roll();
        diceMarkers = new Marker[roll.length];

        // Create visual markers for each die result
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
        
        // Create marker for total result
        Marker m = new Marker(roll[0], null, Marker.DICERESULT, false);
        gh.markers.add(m);
        diceMarkers[0] = m;
    }

    /**
     * Handles initiative tracking commands.
     * @param args Command arguments
     */
    private void initiative(String[] args) {
        if (args == null || args.length < 2) {
            gh.gm.initiative(gh.entities);
        } else {
            switch(args[1].toLowerCase()) {
                case "add", "a" -> {
                    // Add selected entities to initiative
                    ArrayList<Entity> selected = new ArrayList<>();
                    for (Hexagon h : gh.selectedEntityTiles) {
                        if (h != null && gh.selectEntity(h) != null && !selected.contains(gh.selectEntity(h))) {
                            selected.add(gh.selectEntity(h));
                        }
                    }
                    if (!selected.isEmpty()) gh.gm.initiative(selected);
                }
                case "remove", "r" -> {
                    // Remove selected entities from initiative
                    if (!gh.selectedEntityTiles.isEmpty()) {
                        ArrayList<Entity> entitiesToRemove = new ArrayList<>();
                        for (Hexagon h : gh.selectedEntityTiles) {
                            Entity entity = gh.selectEntity(h);
                            if (entity != null && !entitiesToRemove.contains(entity)) {
                                entitiesToRemove.add(entity);
                            }
                        }
                        for (Entity entity : entitiesToRemove) {
                            gh.gm.removeFromInitiative(entity);
                        }
                    }
                }
                case "clear", "c" -> gh.gm.clearInitiative();
                case "show", "s" -> gh.gm.toggleShowInitiative();
                case "next", "n" -> { if (!gh.gm.init.isEmpty()) gh.gm.nextTurn(); }
                default -> {}
            }
        }
    }

    /**
     * Handles auto-completion of commands.
     */
    public void getAutoComplete() {
        if (suggestionIndex == -1) {
            originalInput = getText();
            
            // Special case: Show all arguments when input ends with space
            if (originalInput.endsWith(" ")) {
                currentSuggestions = completer.getSuggestions(originalInput);
                
                if (!currentSuggestions.isEmpty() && currentSuggestions.size() > 1) {
                    gh.consol.addLogMessage("Possible arguments:");
                    for (String suggestion : currentSuggestions) {
                        gh.consol.addLogMessage("- " + suggestion);
                    }
                }
                
                suggestionIndex = 0;
                return;
            }
            
            // Normal case: Complete command or partial argument
            currentSuggestions = completer.getSuggestions(originalInput);
            
            // Filter out exact matches
            String lastWord = originalInput.contains(" ") ? 
                originalInput.substring(originalInput.lastIndexOf(' ') + 1) : 
                originalInput;
            currentSuggestions.removeIf(suggestion -> suggestion.equals(lastWord));
            
            // Show available completions if multiple options
            if (!currentSuggestions.isEmpty() && currentSuggestions.size() > 1) {
                gh.consol.addLogMessage("Possible completions:");
                for (String suggestion : currentSuggestions) {
                    gh.consol.addLogMessage("- " + suggestion);
                }
            }
            
            suggestionIndex = 0;
        } else {
            // Cycle through suggestions on subsequent calls
            suggestionIndex = (suggestionIndex + 1) % currentSuggestions.size();
        }
        
        // Apply current suggestion
        if (!currentSuggestions.isEmpty()) {
            String baseInput = originalInput.contains(" ") ? 
                originalInput.substring(0, originalInput.lastIndexOf(' ') + 1) : 
                "";
            setText(baseInput + currentSuggestions.get(suggestionIndex));
            setCaretPosition(getText().length());
        }
    }

    /**
     * Resets the auto-completion state.
     */
    public void resetSuggestions() {
        originalInput = "";
        if (!currentSuggestions.isEmpty()) currentSuggestions = new ArrayList<>();
        suggestionIndex = -1;
    }

    /**
     * Updates the graphics handler reference.
     * @param gh The new graphics handler
     */
    public void setGraphicsHandler(GraphicsHandler gh) {
        this.gh = gh;
    }

    /**
     * Navigates command history upwards.
     */
    public void arrowUp() {
        if (!commandHistory.isEmpty()) {
            if(historyIndex == commandHistory.size()) {
                currentInput = getText();
            }
            if(historyIndex > 0) {
                historyIndex--;
                setText(commandHistory.get(historyIndex));
                setCaretPosition(getText().length());
            }
        }
    }

    /**
     * Navigates command history downwards.
     */
    public void arrowDown() {
        if (historyIndex < commandHistory.size() - 1) {
            historyIndex++;
            setText(commandHistory.get(historyIndex));
        } else if (historyIndex == commandHistory.size() - 1) {
            historyIndex = commandHistory.size();
            setText(currentInput);
        }
        setCaretPosition(getText().length());
    }

    /**
     * Displays a confirmation prompt.
     * @param s The confirmation message
     */
    public void displayConfirmText(String s) {
        setText(s);
        confirm = true;
    }

    /**
     * Confirms the current action.
     */
    private void confirm() {
        gh.waiter.provideAnswer(true);
    }

    /**
     * Denies the current action.
     */
    private void deny() {
        gh.waiter.provideAnswer(false); 
    }

    /**
     * Sets the current input text.
     * @param currentInput The input text to set
     */
    public void setCurrentInput(String currentInput) {
        this.currentInput = currentInput;
        setText(currentInput);
    }

    /**
     * Gets the current input text.
     * @return The current input text
     */
    public String getCurrentInput() {
        return currentInput;
    }
}