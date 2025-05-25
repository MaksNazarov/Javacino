package cli;

import java.util.HashMap;
import java.util.Map;

public class ShellContext {
    private final Map<String, String> shellVariables = new HashMap<>();

    /// Substitute $VAR or ${VAR} with their values, respecting quotes
    public String substituteVariables(String input) {
        StringBuilder result = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (c == '\'') {
                inSingleQuote = !inSingleQuote;
                result.append(c);
                i++;
            } else if (c == '"') {
                inDoubleQuote = !inDoubleQuote;
                result.append(c);
                i++;
            } else if (c == '$' && !inSingleQuote) {
                // handle escaped $$
                if (i + 1 < input.length() && input.charAt(i + 1) == '$') {
                    result.append('$');
                    i += 2;
                    continue;
                }

                // extract variable name
                i++;
                boolean useBraces = false;
                int start = i;
                if (input.charAt(start) == '{') {
                    useBraces = true;
                    start++;
                }

                int end = start;
                while (end < input.length() && Character.isJavaIdentifierPart(input.charAt(end))) {
                    end++;
                }

                String varName = input.substring(start, end);
                if (useBraces) {
                    int braceEnd = end;
                    while (braceEnd < input.length() && input.charAt(braceEnd) != '}') {
                        braceEnd++;
                    }
                    varName = input.substring(start, end);
                    i = braceEnd + 1;
                } else {
                    i = end;
                }

                String value = getVariable(varName);
                if (value != null) { // value found either in context or in system vars
                    result.append(value);
                } else { // fallback to literal if not found
                    result.append("$").append(useBraces ? "{" : "").append(varName).append(useBraces ? "}" : ""); // TODO: refactor
                }
            } else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }

    public void setVariable(String name, String value) {
        shellVariables.put(name, value);
    }

    public String getVariable(String name) {
        return shellVariables.getOrDefault(name, System.getenv(name));
    }
}
