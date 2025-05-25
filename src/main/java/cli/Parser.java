package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
    private final ShellContext shellContext;

    public Parser(ShellContext shellContext) {
        this.shellContext = shellContext;
    }

    /// Parse input string into list of lists of format (command, command args...), separated by pipes
    public List<List<String>> parse(String input) {
        return splitIntoCommands(input).stream()
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .map(this::substituteVariables)
                .map(this::splitArguments)
                .collect(Collectors.toList());
    }

    private String substituteVariables(String command) {
        return shellContext.substituteVariables(command);
    }

    /// Split input into command strings by pipes NOT inside quotes.
    private List<String> splitIntoCommands(String input) {
        List<String> commands = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        Stack<Character> currentQuotes = new Stack<>(); // holds all open quotes in order

        for (char c : input.toCharArray()) {
            if (c == '"' || c == '\'') {
                if (!currentQuotes.empty() && currentQuotes.peek() == c) { // close the quote
                    currentQuotes.pop();
                    current.append(c);
                } else if (currentQuotes.isEmpty()) { // open a new quote
                    currentQuotes.add(c);
                    current.append(c);
                } else { // non-quote char
                    current.append(c);
                }
            } else if (c == '|' && currentQuotes.empty()) {
                commands.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (!currentQuotes.empty()) {
            throw new IllegalStateException("Mismatched quotes");
        }

        if (!current.isEmpty()) {
            commands.add(current.toString().trim());
        }

        return commands;
    }

    /// Split command into arguments with respect to quotes.
    private List<String> splitArguments(String command) {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        char currentQuote = 0; // holds last open quote type, or '0' for no quotes

        for (char c : command.toCharArray()) {
            if (c == '"' || c == '\'') {
                if (currentQuote == c) { // close the quote
                    currentQuote = 0;
                } else if (currentQuote == 0) { // open a new quote
                    currentQuote = c;
                } else {
                    currentArg.append(c);
                }
            } else if (c == ' ' && currentQuote == 0) {
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