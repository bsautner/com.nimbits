package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.Command;
import com.nimbits.client.model.server.Server;

public class ExitCommand extends AbstractCommand implements Command {

    private final static String USAGE = "exit the system";

    public ExitCommand(Server server) {
        super(server);
    }

    @Override
    public void doCommand(String[] args) {
        System.out.println("bye!");
        System.exit(0);
    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
