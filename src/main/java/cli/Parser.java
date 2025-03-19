package main.java.cli;

import java.util.Arrays;
import java.util.List;

public class Parser {
    public List<String> parse(String input) {
        // Simplified parsing, does not handle quotes or special characters
        return Arrays.asList(input.split("\\s+"));
    }
}
