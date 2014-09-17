package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.Command;
import com.nimbits.Program;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;

import java.io.IOException;

public class ChangeDirectoryCommand extends AbstractCommand implements Command {

    private final static String USAGE = "navigate to entity: cd entityname";

    public ChangeDirectoryCommand(Server server) {
        super(server);
    }

    @Override
    public void doCommand(String[] args) {
        try {
            String name = args.length > 1 ? args[1] : "";

            switch (name) {
                case "~":
                    Program.setCurrent(Program.user);
                    break;
                case "..":
                    for (Entity entity : Program.tree) {
                        if (entity.getKey().equals(Program.current.getParent())) {
                            Program.setCurrent(entity);
                            return;
                        }

                    }
                    Program.setCurrent(Program.user);


                    break;
                default:
                    for (Entity entity : Program.tree) {
                        if (entity.getParent().equals(Program.current.getKey()) && entity.getName().getValue().equals(name)) {

                            Program.setCurrent(entity);
                            return;


                        }

                    }
                    System.out.println("entity not found");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
