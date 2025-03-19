package main.java.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.cli.Commands.*;

class Executor {
    private final Map<String, Command> commands = new HashMap<>();

    public Executor() {
        commands.put("echo", new EchoCommand());
        commands.put("cat", new CatCommand());
        commands.put("pwd", new PwdCommand());
        commands.put("exit", new ExitCommand());
    }

    public int execute(List<String> tokens) {
        if (tokens.isEmpty()) return 0;
        String commandName = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());
        Command command = commands.getOrDefault(commandName, new ExternalCommand());
        return command.execute(args);
    }
}