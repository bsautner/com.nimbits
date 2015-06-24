package com.nimbits.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.List;

public class HelpCommand extends AbstractCommand implements Command {

    private final static String USAGE = "list available commands";

    public HelpCommand(User user, Entity current, Server server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {
        for (TerminalCommand command : TerminalCommand.values()) {
            try {
                listener.onMessage(command.name() + "\t\t\t\t" + command.init(user, current, server, tree).getUsage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String getUsage() {
        return USAGE;
    }
}
