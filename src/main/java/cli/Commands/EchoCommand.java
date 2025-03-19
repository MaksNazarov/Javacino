package main.java.cli.Commands;

import java.util.List;

public class EchoCommand implements Command {
    @Override
    public int execute(List<String> args) {
        System.out.println(String.join(" ", args));
        return 0;
    }
}