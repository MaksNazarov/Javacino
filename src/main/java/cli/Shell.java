package cli;

import java.util.Scanner;


public class Shell {
    public static void main(String[] args) {
        final Executor executor = new Executor();
        final Parser parser = new Parser();
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(":-) ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty())
                    continue;
                try {
                    executor.executeQuery(parser.parse(input));
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}