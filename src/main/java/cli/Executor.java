package cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import cli.Commands.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
    name = "",
    subcommands = {
        EchoCommand.class,
        ExitCommand.class,
        PwdCommand.class,
        WcCommand.class,
        GrepCommand.class,
        CdCommand.class,
        LsCommand.class,
        CommandLine.HelpCommand.class
    }
)
public class Executor {
    private final CommandLine cmd = new CommandLine(this);
    private final Map<String, String> globalVars = new HashMap<>();

    public void executeQuery(List<List<String>> command_queries) {
        String result = null;
        if (!command_queries.isEmpty()) { // while all queries not processed
            List<String> args;
            for (int i = 0; i < command_queries.size(); i++) {
                args = new ArrayList<>(command_queries.get(i));
                if (isSetStatement(args)) {
                    runSetStatement(args);
                } else if (cmd.getSubcommands().getOrDefault(args.getFirst(), null) == null) {
                    result = (result == null) ? "" : result;
                    result = executeExternalCommand(args, result);
                } else {
                    if (i > 0 && result != null) args.add(result);
                    result = executeCommand(args.toArray(String[]::new));
                }
            }
            // result printing
            if (result != null && !result.isEmpty())
                System.out.println(result);
        }
    }

    private void runSetStatement(List<String> args) {
        assert isSetStatement(args);
        globalVars.put(args.get(0), args.get(2));
    }

    private String executeCommand(String[] args) {
        String[] substituted_args = substituteGlobalVars(args);
        int error_code = cmd.execute(substituted_args);
        if (error_code > 1) {
            System.out.println("Command finished with error code: " + error_code);
            return "";
        }
        return cmd.getSubcommands().get(args[0]).getExecutionResult();
    }

    private boolean isSetStatement(List<String> args) {
        return (args.size() >= 3 && Objects.equals(args.get(1), "="));
    }

    /// Replaces #NAME-like strings with values of saved global vars if there are any.
    private String[] substituteGlobalVars(String[] args) {
        String[] updatedArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.startsWith("#")) {
                String globalVarName = arg.substring(1);
                updatedArgs[i] = globalVars.getOrDefault(globalVarName, arg);
            } else {
                updatedArgs[i] = arg;
            }
        }
        return updatedArgs;
    }

    private String executeExternalCommand(List<String> args, String input) {
        try {
            ProcessBuilder pb = new ProcessBuilder(args);
            Process process = pb.start();
            
            if (input != null) {
                try (OutputStream os = process.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                    writer.write(input);
                    writer.flush();
                }
            }
            
            StringBuilder output = new StringBuilder();
            try (InputStream is = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!output.isEmpty()) {
                        output.append(System.lineSeparator());
                    }
                    output.append(line);
                }
            }

            // giving extra time for external command to execute
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroy();
                System.err.println("Command timed out");
                return "";
            }
            
            if (process.exitValue() != 0) {
                System.err.println("External command failed with exit code: " + process.exitValue());
                return "";
            }
            
            return output.toString();
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing external command: " + e.getMessage());
            return "";
        }
    }
}