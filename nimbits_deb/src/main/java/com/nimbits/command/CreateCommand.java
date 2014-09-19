package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.Command;
import com.nimbits.Program;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;

public class CreateCommand extends AbstractCommand implements Command {

    private final static String USAGE = "create a new entity: create <entity type> <entity name> e.g create point foobar";

    public CreateCommand(Server server) {
        super(server);
    }

    @Override
    public void doCommand(String[] args) {

        if (args.length != 3) {
            System.out.println(USAGE);
        }
        else {
            try {
                String type = args[1];
                String name = args[2];
                EntityType entityType = EntityType.valueOf(type);
                if (entityType.equals(EntityType.point)) {
                    EntityHelper helper = HelperFactory.getEntityHelper(server, Program.user.getEmail(), null);
                    Point point = helper.createPoint(name, entityType, Program.current);
                    Program.tree.add(point);
                    Program.setCurrent(Program.current);

                }
                else {
                    System.out.println("you can only create a type point for now.");
                }
            }
            catch (Exception ex) {
               System.out.println(ex.getMessage());
            }

        }


    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
