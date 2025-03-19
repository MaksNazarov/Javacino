package main.java.cli.Commands;

import java.util.List;

public class PwdCommand implements Command {
    @Override
    public int execute(List<String> args) {
        System.out.println(System.getProperty("user.dir"));
        return 0;
    }
}
