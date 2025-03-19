package main.java.cli.Commands;

import java.util.List;

public interface Command {
    public int execute(List<String> args);
}
