package cli;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShellContext {
    private static final int MAX_SUBSTITUTION_ITERATIONS = 50; // max count of substitutions allowed; used to prevent recursion
    private static final Pattern VAR_PATTERN = Pattern.compile("\\$(?:([a-zA-Z_][a-zA-Z0-9_]*)|\\{([^{}]*)})");
    private final Map<String, String> shellVariables = new HashMap<>();

    /// Substitute $VAR or ${VAR} with their values, respecting single quotes and nested substitution
    public String substituteVariables(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        int iterations = 0;

        String current = input;
        String previous;

        // process until all variables are replaced, or too many iterations happened
        do {
            previous = current;
            current = substituteVariablesOnce(current);
            iterations++;

            if (iterations > MAX_SUBSTITUTION_ITERATIONS) {
                throw new IllegalStateException("Maximum substitution iterations (" + MAX_SUBSTITUTION_ITERATIONS + ") exceeded");
            }
        } while (!current.equals(previous));

        return current;
    }

    private String substituteVariablesOnce(String input) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = VAR_PATTERN.matcher(input);
        int lastEnd = 0;
        boolean inSingleQuotes = false;

        while (matcher.find()) {
            // check if we're inside single quotes
            String beforeMatch = input.substring(lastEnd, matcher.start());
            inSingleQuotes = isInSingleQuotes(beforeMatch, inSingleQuotes);

            // append the text before the match
            result.append(beforeMatch);

            if (!inSingleQuotes) {
                String varName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                String replacement = resolveVariable(varName);
                result.append(replacement);
            } else { // leave variable as it is if in single quotes
                result.append(matcher.group(0));
            }

            lastEnd = matcher.end();
        }

        // append the remaining part of the string
        result.append(input.substring(lastEnd));
        return result.toString();
    }

    /**
     * Checks if last text char is in single quotes given starting quote state.
     *
     * @param text         part of text to analyse, not necessary from the start
     * @param currentState if single quote is opened before text start
     * @return True if last text char is after an unclosed single quote
     */
    private boolean isInSingleQuotes(String text, boolean currentState) {
        int singleQuoteCount = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\'' && (i == 0 || text.charAt(i - 1) != '\\')) {
                singleQuoteCount++;
            }
        }
        return (singleQuoteCount % 2 == 1) != currentState;
    }

    /// Returns the value of the provided variable after all recursive variable substitutions, or system variable if there isn't one.
    private String resolveVariable(String varName) {
        if (varName == null || varName.isEmpty()) {
            return "";
        }

        String nestedResolved = substituteVariablesOnce(varName); // recursive replacement
        return shellVariables.getOrDefault(nestedResolved, System.getenv(varName));
    }

    /// Set the given variable's value to the provided one. Can "overwrite" system values.
    public void setVariable(String name, String value) {
        shellVariables.put(name, value);
    }
}
