package com.nimbits.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.List;

public class ChangeDirectoryCommand extends AbstractCommand implements Command {

    private final static String USAGE = "navigate to entity: cd <entity name>";

    public ChangeDirectoryCommand(User user, Entity current, Server server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {

            String name = args.length > 1 ? args[1] : "";

            switch (name) {
                case "~":
                    listener.setCurrent(user);
                    break;
                case "..":
                    for (Entity entity : tree) {
                        if (entity.getKey().equals(current.getParent())) {
                            listener.setCurrent(entity);
                            return;
                        }

                    }
                    listener.setCurrent(user);


                    break;
                default:
                    for (Entity entity : tree) {
                        if (entity.getParent().equals(current.getKey()) && entity.getName().getValue().equals(name)) {

                            listener.setCurrent(entity);
                            return;


                        }

                    }
                   listener.onMessage("entity not found");
                    break;
            }


    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
