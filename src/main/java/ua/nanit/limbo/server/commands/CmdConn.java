package ua.nanit.limbo.server.commands;

import lombok.AllArgsConstructor;
import ua.nanit.limbo.server.Command;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

@AllArgsConstructor
public class CmdConn implements Command {

    private final LimboServer server;

    @Override
    public void execute() {
        Log.info("Connections: %d", server.getConnections().getCount());
    }

    @Override
    public String description() {
        return "Display connections count";
    }
}
