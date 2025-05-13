package cli.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(
    name = "cd",
    description = "Change the current working directory"
)
public class CdCommand implements Runnable {
    @Parameters(index = "0", arity = "0..1", description = "Directory to change to")
    String directory;

    private String result;

    @Override
    public void run() {
        try {
            if (directory == null) {
                directory = System.getProperty("user.home");
            }

            Path newPath = Paths.get(directory).normalize();
            File newDir = newPath.toFile();

            if (!newDir.exists()) {
                result = "cd: no such directory: " + directory;
                return;
            }

            if (!newDir.isDirectory()) {
                result = "cd: not a directory: " + directory;
                return;
            }

            System.setProperty("user.dir", newDir.getAbsolutePath());
            result = "";
        } catch (Exception e) {
            result = "cd: " + e.getMessage();
        }
    }

    public String getExecutionResult() {
        return result;
    }
} 