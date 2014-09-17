package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.Command;
import com.nimbits.client.model.server.Server;

public class HelpCommand extends AbstractCommand implements Command {

    private final static String USAGE = "list available commands";

    public HelpCommand(Server server) {
        super(server);
    }

    @Override
    public void doCommand(String[] args) {
        for (TerminalCommand command : TerminalCommand.values()) {
            try {
                System.out.println(command.name() + "\t\t\t\t" + command.init(server).getUsage());
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
