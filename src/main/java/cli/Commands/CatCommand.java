package cli.Commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.Callable;

import picocli.CommandLine.Parameters;

public class CatCommand implements Callable<String> {
    @Parameters(description = "File for content to print")
    File file;

    @Override
    public String call() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null)
                content.append(line).append(System.lineSeparator());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "";
        }
        return content.toString();
    }
}