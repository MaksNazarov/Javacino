package cli;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    public List<List<String>> parse(String input) {
        return Arrays.stream(input.split("\\|"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> Arrays.asList(s.split(" ")))
            .collect(Collectors.toList());
    }
}


