package main.java.cli.Commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CatCommand implements Command {
    @Override
    public int execute(List<String> args) {
        if (args.isEmpty()) {
            System.err.println("cat: missing file operand");
            return 1;
        }
        for (String file : args) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println("cat: " + e.getMessage());
                return 1;
            }
        }
        return 0;
    }
}