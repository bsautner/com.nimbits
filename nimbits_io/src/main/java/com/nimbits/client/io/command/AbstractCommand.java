package com.nimbits.client.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.user.User;

import java.util.List;

public abstract class AbstractCommand implements Command {

    protected final Instance server;
    protected final User user;
    protected final Entity current;
    protected final List<Entity> tree;


    public AbstractCommand(User user, Entity current, Instance server, List<Entity> tree) {
        this.server = server;
        this.user = user;
        this.current = current;
        this.tree = tree;

    }

    abstract public void doCommand(CommandListener listener, String[] args);

}
