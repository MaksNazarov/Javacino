package main.java.cli.Commands;

import java.util.List;

public class ExitCommand implements Command {
    @Override
    public int execute(List<String> args) {
        System.exit(0);
        return 0;
    }
}