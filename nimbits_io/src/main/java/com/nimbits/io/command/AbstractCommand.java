package com.nimbits.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.List;

public abstract class AbstractCommand implements Command {

    protected final Server server;
    protected final User user;
    protected final Entity current;
    protected final List<Entity> tree;


    public AbstractCommand(User user, Entity current, Server server, List<Entity> tree) {
        this.server = server;
        this.user = user;
        this.current = current;
        this.tree = tree;

    }

    abstract public void doCommand(CommandListener listener, String[] args);

}
