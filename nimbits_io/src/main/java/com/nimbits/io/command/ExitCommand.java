package com.nimbits.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.List;

public class ExitCommand extends AbstractCommand implements Command {

    private final static String USAGE = "exit the system";

    public ExitCommand(User user, Entity current, Server server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {
       listener.onMessage("bye!");
        System.exit(0);
    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
