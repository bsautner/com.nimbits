package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.Command;
import com.nimbits.Program;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;

public class ListCommand extends AbstractCommand implements Command {

    private final static String USAGE = "list child entities";

    public ListCommand(Server server) {
        super(server);
    }

    @Override
    public void doCommand(String[] args) {

        String contains = args.length > 1 ? args[1] : "";
        for (Entity entity : Program.tree) {
            if (entity.getParent().equals(Program.current.getKey())) {
                if (contains.equals("")) {
                    System.out.println(entity.getName());
                }
                else {
                    if (entity.getName().toString().contains(contains)) {
                        System.out.println(entity.getName());
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
