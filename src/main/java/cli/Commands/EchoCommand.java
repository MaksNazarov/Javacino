package cli.Commands;

import java.util.List;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command (
    name = "echo", 
    description = "Prints given args"
)
public class EchoCommand implements Callable<String> {
    @Parameters(description = "Arguments for echo command")
    private List<String> args;
    
    @Override
    public String call() {
        return (!args.isEmpty()) ? String.join(" ", args) : "";
    }
}