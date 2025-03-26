package cli.Commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command (
    name = "wc", 
    description = "Counts lines, words, and characters in input"
)
public class WcCommand implements Runnable {
    @Parameters(description = "File to analyze", arity="0..*")
    private File file;
    
    @Override
    public void run() {
        try {
            BufferedReader reader = getReader();
            int lines = 0;
            int words = 0;
            int chars = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                words += line.split("\\s+").length;
                chars += line.length() + 1;
            }

            System.out.printf("%d lines, %d words, %d chars%n", lines, words, chars);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private BufferedReader getReader() throws IOException {
        if (file != null)
            return new BufferedReader(new FileReader(file));
        return new BufferedReader(new InputStreamReader(System.in));
    }
}