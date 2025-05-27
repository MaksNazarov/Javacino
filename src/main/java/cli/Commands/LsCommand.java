package cli.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Callable;

@Command(
        name = "ls",
        description = "List directory contents"
)
public class LsCommand implements Callable<String> {
    @Parameters(index = "0", arity = "0..1", description = "Directory to list")
    String directory;

    private String result;

    @Override
    public String call() {
        try {
            File targetDir;
            if (directory == null) {
                targetDir = new File(System.getProperty("user.dir"));
            } else {
                targetDir = new File(directory);
            }

            if (!targetDir.exists()) {
                result = "ls: cannot access '" + directory + "': No such file or directory";
                System.err.println(result);
                return "";
            }

            if (!targetDir.isDirectory()) {
                result = "ls: cannot access '" + directory + "': Not a directory";
                System.err.println(result);
                return "";
            }

            File[] files = targetDir.listFiles();
            if (files == null) {
                result = "ls: cannot read directory '" + directory + "'";
                System.err.println(result);
                return "";
            }

            Arrays.sort(files, Comparator.comparing(File::getName));

            StringBuilder output = new StringBuilder();
            for (File file : files) {
                if (!output.isEmpty()) {
                    output.append("\n");
                }
                output.append(file.getName());
            }
            result = output.toString();
            return result;

        } catch (Exception e) {
            result = "ls: " + e.getMessage();
            System.err.println(result);
            return "";
        }
    }

    public String getExecutionResult() {
        return result;
    }
} 