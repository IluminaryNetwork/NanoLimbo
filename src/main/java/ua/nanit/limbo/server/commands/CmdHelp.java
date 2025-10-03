package ua.nanit.limbo.server.commands;

import lombok.AllArgsConstructor;
import ua.nanit.limbo.server.Command;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

import java.util.Map;

@AllArgsConstructor
public class CmdHelp implements Command {

    private final LimboServer server;

    @Override
    public void execute() {
        Map<String, Command> commands = server.getCommandManager().getCommands();

        Log.info("Available commands:");

        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            Log.info("%s - %s", entry.getKey(), entry.getValue().description());
        }
    }

    @Override
    public String description() {
        return "Show this message";
    }
}
