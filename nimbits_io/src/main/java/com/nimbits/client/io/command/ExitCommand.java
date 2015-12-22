package com.nimbits.client.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.user.User;

import java.util.List;

public class ExitCommand extends AbstractCommand implements Command {

    private final static String USAGE = "exit the system";

    public ExitCommand(User user, Entity current, Instance server, List<Entity> tree) {
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
