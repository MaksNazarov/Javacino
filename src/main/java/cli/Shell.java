package main.java.cli;

import java.util.List;
import java.util.Scanner;

public class Shell {
    private final Parser parser = new Parser();
    private final Executor executor = new Executor();

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                List<String> tokens = parser.parse(input);
                executor.execute(tokens);
            }
        }
    }

    public static void main(String[] args) {
        new Shell().run();
    }
}