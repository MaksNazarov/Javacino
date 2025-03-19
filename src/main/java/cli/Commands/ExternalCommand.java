package main.java.cli.Commands;

import java.io.IOException;
import java.util.List;

public class ExternalCommand implements Command {
    @Override
    public int execute(List<String> args) {
        ProcessBuilder pb = new ProcessBuilder(args);
        try {
            Process process = pb.start();
            process.waitFor();
            return process.exitValue();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage());
            return 1;
        }
    }
}
