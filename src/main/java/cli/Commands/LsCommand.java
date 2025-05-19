package cli.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

@Command(
        name = "ls",
        description = "List directory contents"
)
public class LsCommand implements Runnable {
    @Parameters(index = "0", arity = "0..1", description = "Directory to list")
    String directory;

    private String result;

    @Override
    public void run() {
        try {
            File targetDir;
            if (directory == null) {
                targetDir = new File(System.getProperty("user.dir"));
            } else {
                targetDir = new File(directory);
            }

            if (!targetDir.exists()) {
                result = "ls: cannot access '" + directory + "': No such file or directory";
                return;
            }

            if (!targetDir.isDirectory()) {
                result = "ls: cannot access '" + directory + "': Not a directory";
                return;
            }

            File[] files = targetDir.listFiles();
            if (files == null) {
                result = "ls: cannot read directory '" + directory + "'";
                return;
            }

            Arrays.sort(files, Comparator.comparing(File::getName));

            StringBuilder output = new StringBuilder();
            for (File file : files) {
                if (!output.isEmpty()) {
                    output.append("\n");
                }
                output.append(file.getName());
            }
            System.out.println(output);
            result = output.toString();
        } catch (Exception e) {
            result = "ls: " + e.getMessage();
            System.err.println(result);
        }
    }

    public String getExecutionResult() {
        return result;
    }
} 