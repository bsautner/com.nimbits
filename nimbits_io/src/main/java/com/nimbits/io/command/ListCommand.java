package com.nimbits.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.List;
import java.util.logging.Logger;

public class ListCommand extends AbstractCommand implements Command {

    private final static String USAGE = "list child entities";
    private final static Logger logger = Logger.getLogger(ListCommand.class.getName());

    public ListCommand(User user, Entity current, Server server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {

        String contains = args.length > 1 ? args[1] : "";
        for (Entity entity : tree) {
            if (entity.getParent().equals(current.getKey())) {
                if (contains.equals("")) {
                    listener.onMessage(entity.getName().getValue());
                } else {
                    if (entity.getName().toString().contains(contains)) {
                        listener.onMessage(entity.getName().getValue());
                    }
                }
            }
        }

    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
