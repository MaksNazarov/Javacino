package cli.Commands;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command (
    name = "pwd", 
    description = "Prints current directory absolute path"
)
public class PwdCommand implements Callable<String> {
    @Override
    public String call() {
        return Paths.get("").toAbsolutePath().toString();
    }
}