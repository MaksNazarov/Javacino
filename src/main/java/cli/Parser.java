package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
    /// Parse input string into list of lists of format (command, command args...)
    public List<List<String>> parse(String input) {
        return splitIntoCommands(input).stream()
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .map(this::splitArguments)
                .map(this::replaceEnvVars)
                .collect(Collectors.toList());
    }

    /// Split input into command strings by pipes NOT inside quotes.
    private List<String> splitIntoCommands(String input) {
        List<String> commands = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c); // include quotes in the command part
            } else if (c == '|' && !inQuotes) {
                commands.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            commands.add(current.toString().trim());
        }

        return commands;
    }

    /// Inplace env vars by their values
    private List<String> replaceEnvVars(List<String> args) {
        List<String> new_args = new ArrayList<>();
        for (var arg : args) {
            //regexp pattern for env variables
            Pattern envVarPattern = Pattern.compile("\\$(?:([a-zA-Z_][a-zA-Z0-9_]*)|\\{([^}]+)\\})");
            new_args.add(envVarPattern.matcher(arg).replaceAll(match -> {
                String varName = match.group(1) != null ? match.group(1) : match.group(2);
                String envValue = System.getenv(varName);
                return envValue != null ? envValue : match.group();
            }));
        }

        return new_args;
    }

    /// Split command into arguments with respect to quotes.
    private List<String> splitArguments(String command) {
        List<String> args = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentArg = new StringBuilder();

        for (char c : command.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (!currentArg.isEmpty()) {
                    args.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            } else {
                currentArg.append(c);
            }
        }

        // add last argument if needed
        if (!currentArg.isEmpty()) {
            args.add(currentArg.toString());
        }

        return args;
    }
}