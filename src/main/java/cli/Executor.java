package cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cli.Commands.EchoCommand;
import cli.Commands.ExitCommand;
import cli.Commands.PwdCommand;
import cli.Commands.WcCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
    name = "",
    subcommands = {
        EchoCommand.class,
        ExitCommand.class,
        PwdCommand.class,
        WcCommand.class,
        CommandLine.HelpCommand.class
    }
)
public class Executor {
    private final CommandLine cmd = new CommandLine(this);

    public void executeQuery(List<List<String>> command_queries) {
        String result = null;
        if (!command_queries.isEmpty()) {
            List<String> args;
            for (int i = 0; i < command_queries.size(); i++) {
                args = new ArrayList<>(command_queries.get(i));
                if (cmd.getSubcommands().getOrDefault(args.get(0), null) == null) {
                    result = (result == null) ? "" : result;
                    result = executeExternalCommand(args, result);
                } else {
                    if (i > 0 && result != null) args.add(result);
                    result = executeCommand(args.toArray(String[]::new));
                }
            }
            if (result != null && !result.isEmpty())
                System.out.println(result);
        }
    }

    private String executeCommand(String [] args) {
        Integer error_code = cmd.execute(args);
        if (error_code > 1) {
            System.out.println("Command finished with errorcode: " + error_code.toString());
            return "";
        }
        return cmd.getSubcommands().get(args[0]).getExecutionResult();
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