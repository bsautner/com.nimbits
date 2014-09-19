package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.Command;
import com.nimbits.Program;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;

public class RemoveCommand extends AbstractCommand implements Command {

    private final static String USAGE = "delete the current entity: rm <entity name> [-R]";

    public RemoveCommand(Server server) {
        super(server);
    }

    @Override
    public void doCommand(String[] args) {
           boolean recursive = false;
           EntityHelper helper = HelperFactory.getEntityHelper(server, Program.user.getEmail(), null);
           if (args.length < 2) {
               System.out.println(getUsage());

           }
        else {
               Entity entity = getEntity(args[1]);
               if (entity == null) {
                   System.out.print(args[1] + " not found");
               }
               else {

                   for (String s : args) {
                       if (s.equals("-R")) {
                           recursive = true;
                           break;
                       }
                   }
                   boolean hc = hasChildren(entity);

                   if (hc && !recursive) {

                       System.out.println("entity has children. Use -R to delete it and all children.");
                   }
                   else  {
                       Program.tree.remove(entity);
                       helper.deleteEntity(entity);

                   }

               }

           }

    }

    private boolean hasChildren(Entity entity) {
        for (Entity e : Program.tree) {
            if (e.getParent().equals(entity.getKey())) {

                return true;

            }
        }
        return false;

    }
    private Entity getEntity(String name) {
        for (Entity entity : Program.tree) {
            if (entity.getName().getValue().equals(name)){
                return entity;
            }
        }
        return  null;
    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
