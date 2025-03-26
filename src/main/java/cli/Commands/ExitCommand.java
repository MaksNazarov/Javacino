package cli.Commands;

import picocli.CommandLine.Command;

@Command (
    name = "exit", 
    description = "Exits the shell"
)
public class ExitCommand implements Runnable {
    @Override
    public void run() {
        System.exit(0);
    }
}