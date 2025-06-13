package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandCompleter {
    private final Map<String, List<String>> commandArguments;

    public CommandCompleter() {
        commandArguments = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        commandArguments.put("quit",null);
        commandArguments.put("background",null);
        commandArguments.put("debug",null);
        commandArguments.put("character", Arrays.asList("delete", "size", "maxhealth", "armorclass", "speed", "initiative", "icon"));
        commandArguments.put("wall",      Arrays.asList("delete"));
        commandArguments.put("entity",    Arrays.asList("delete"));
        commandArguments.put("help",      Arrays.asList("quit", "background", "debug", "character", "wall", "entity", "help", "roll", "grid", "gamemaster", "init", "clear", "theme"));
        commandArguments.put("roll",null);
        commandArguments.put("grid",null);
        commandArguments.put("gamemaster",null);
        commandArguments.put("init",      Arrays.asList("add", "remove", "clear", "show", "next"));
        commandArguments.put("clear",null);
        commandArguments.put("theme",     Arrays.asList("purple", "red", "green", "yellow", "light", "dark", "black"));
    }

    public List<String> getSuggestions(String input) {
        if (input.isEmpty()) {
            return new ArrayList<>();
        }

        if (input.endsWith(" ")) {
            String[] parts = input.trim().split("\\s+");
            String command = parts[0];

            if (commandArguments.containsKey(command)) {
                return new ArrayList<>(commandArguments.get(command));
            }
            return new ArrayList<>();
        }
        
        String[] parts = input.split("\\s+");

        if (parts.length > 1) {
            String command = parts[0];
            String argPrefix = parts[parts.length - 1];

            if (commandArguments.containsKey(command)) {
                return commandArguments.get(command).stream()
                    .filter(arg -> arg.startsWith(argPrefix))
                    .collect(Collectors.toCollection(ArrayList::new));
            }
        } else {
            String prefix = parts[0];

            return commandArguments.keySet().stream()
                .filter(cmd -> cmd.startsWith(prefix))
                .collect(Collectors.toCollection(ArrayList::new));
        }
        return new ArrayList<>();
    }
}
