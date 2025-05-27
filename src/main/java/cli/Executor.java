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
        CommandLine.HelpCommand.class
    }
)
public class Executor {
    private final CommandLine cmd = new CommandLine(this);
    private final ShellContext shellContext;

    public Executor(ShellContext shellContext) {
        this.shellContext = shellContext;
    }

    public void executeQuery(List<List<String>> commandQueries) {
        String result = null;
        if (!commandQueries.isEmpty()) { // while all queries not processed
            for (int i = 0; i < commandQueries.size(); i++) {
                List<String> args = new ArrayList<>(commandQueries.get(i));
                if (isAssignment(args)) {
                    runAssignment(args);
                } else if (cmd.getSubcommands().getOrDefault(args.getFirst(), null) == null) {
                    result = (result == null) ? "" : result;
                    result = executeExternalCommand(args, result);
                } else {
                    if (i > 0 && result != null) args.add(result);
                    result = executeCommand(args.toArray(String[]::new));
                }
            }
            // result printing
            if (result != null && !result.isEmpty()) {
                System.out.println(result);
            }
        }
    }

    private void runAssignment(List<String> args) {
        shellContext.setVariable(args.get(0), args.get(2));
    }

    private String executeCommand(String[] args) {
        int errorCode = cmd.execute(args);
        if (errorCode > 1) {
            System.out.println("Command finished with error code: " + errorCode);
            return "";
        }
        return cmd.getSubcommands().get(args[0]).getExecutionResult();
    }

    private boolean isAssignment(List<String> args) {
        return args.size() >= 3 && args.get(1).equals("=");
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