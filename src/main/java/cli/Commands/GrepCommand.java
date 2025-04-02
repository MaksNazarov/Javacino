package cli.Commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command (
    name = "grep",
    description = "Finds given text in given files"
)
public class GrepCommand implements Callable<String> {

    @Option(names = {"-w"}, description = "Match only the whole word; ignore matching part of larger word.")
    private boolean onlyWords = false;

    @Option(names = {"-i"}, description = "Is case sensitive.")
    private boolean caseInsensitive = false;

    @Option(names = {"-A"}, description = "Amount of lines to show after a match.")
    private Integer lineCount = 1;

    @Parameters(description = "Arguments for grep command: pattern and list of places to search.")
    List<String> args;

    @Override
    public String call() throws IllegalArgumentException, IOException {
        if (args == null || args.size() < 2) {
            throw new IllegalArgumentException("Usage: grep [-w] [-i] [-A N] pattern files...");
        }

        String pattern = args.getFirst();
        List<String> filePaths = args.subList(1, args.size());

        int flags = caseInsensitive ? Pattern.CASE_INSENSITIVE : 0;
        Pattern compiledPattern = compilePattern(pattern, flags);

        StringBuilder result = new StringBuilder();

        for (String filePath : filePaths) {
            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path);

            List<String> matchedLines = processFile(lines, compiledPattern);
            if (!matchedLines.isEmpty()) {
                if (!result.isEmpty()) {
                    result.append(System.lineSeparator());
                }
                result.append(String.join(System.lineSeparator(), matchedLines)); // TODO: beautify? A numbered list?
            }
        }

        return result.toString();
    }

    private Pattern compilePattern(String pattern, int flags) {
        if (onlyWords) {
            return Pattern.compile("\\b" + pattern + "\\b", flags);
        } else {
            return Pattern.compile(pattern, flags);
        }
    }

    private List<String> processFile(List<String> lines, Pattern pattern) {
        List<String> outputLines = new ArrayList<>();
        int contextEndIndex = -1; // number of last line to be included in output

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i > contextEndIndex) { // previous context finished printing
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    outputLines.add(line);
                    contextEndIndex = Math.min(i + lineCount, lines.size() - 1);
                }
            } else { // still printing lines after a match
                outputLines.add(line);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) { // match while in open context window, possibly need to move end index
                    int newContextEndIndex = Math.min(i + lineCount, lines.size() - 1);
                    contextEndIndex = Math.min(contextEndIndex, newContextEndIndex);
                }
            }
        }

        return outputLines;
    }
}
