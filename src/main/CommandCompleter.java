package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides command completion suggestions for a command-line interface.
 * Maintains a map of commands and their valid arguments for auto-completion.
 */
public class CommandCompleter {
    // Maps commands to their possible arguments (null means no arguments)
    private final Map<String, List<String>> commandArguments;

    /**
     * Initializes the command completer and sets up all known commands.
     */
    public CommandCompleter() {
        commandArguments = new HashMap<>();
        initializeCommands();
    }

    /**
     * Populates the command-argument map with all supported commands and their arguments.
     */
    private void initializeCommands() {

        // Define commands and their possible arguments
        commandArguments.put("quit", null);
        commandArguments.put("background", null);
        commandArguments.put("debug", null);
        commandArguments.put("character", Arrays.asList("delete", "size", "maxhealth", "armorclass", "speed", "initiative", "icon"));
        commandArguments.put("wall", Arrays.asList("delete"));
        commandArguments.put("entity", Arrays.asList("delete"));
        commandArguments.put("help", Arrays.asList("quit", "background", "debug", "character", "wall", "entity", "help", "roll", "grid", "gamemaster", "init", "clear", "theme"));
        commandArguments.put("roll", null);
        commandArguments.put("grid", null);
        commandArguments.put("gamemaster", null);
        commandArguments.put("init", Arrays.asList("add", "remove", "clear", "show", "next"));
        commandArguments.put("clear", null);
        commandArguments.put("theme", Arrays.asList("purple", "red", "green", "yellow", "light", "dark", "black"));
    }

    /**
     * Generates completion suggestions based on the current input.
     * @param input The current command line input
     * @return List of possible completions (empty if none)
     */
    public List<String> getSuggestions(String input) {
        // Return empty list for empty input
        if (input.isEmpty()) {
            return new ArrayList<>();
        }

        // Handle case where user has entered a command and pressed space
        if (input.endsWith(" ")) {
            String[] parts = input.trim().split("\\s+");
            String command = parts[0];

            // Return all possible arguments for this command
            if (commandArguments.containsKey(command)) {
                return new ArrayList<>(commandArguments.get(command));
            }
            return new ArrayList<>();
        }
        
        // Split input into parts
        String[] parts = input.split("\\s+");

        // If input has multiple parts, suggest matching arguments
        if (parts.length > 1) {
            String command = parts[0];
            String argPrefix = parts[parts.length - 1];

            // Filter arguments that start with the current partial input
            if (commandArguments.containsKey(command)) {
                return commandArguments.get(command).stream()
                    .filter(arg -> arg.startsWith(argPrefix))
                    .collect(Collectors.toCollection(ArrayList::new));
            }
        } else {
            // Single word input - suggest matching commands
            String prefix = parts[0];

            return commandArguments.keySet().stream()
                .filter(cmd -> cmd.startsWith(prefix))
                .collect(Collectors.toCollection(ArrayList::new));
        }
        return new ArrayList<>();
    }
}