package com.nimbits;

import com.nimbits.client.model.server.Server;

public abstract class AbstractCommand implements Command {

    protected final Server server;



    public AbstractCommand(Server server) {
        this.server = server;

    }

    abstract public void doCommand(String[] args);
}
