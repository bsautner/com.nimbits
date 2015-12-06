package com.nimbits.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.List;

public class SeedRandomCommand extends AbstractCommand implements Command {

    private final static String USAGE = "Seed the current data point with random data: seed <count>";

    public SeedRandomCommand(User user, Entity current, Server server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {
//        if (current.getEntityType().recordsData()) {
//            if (args.length != 2) {
//                listener.onMessage("try: \"seed 10\" to seed 10 random values");
//            } else {
//                int count = Integer.parseInt(args[1]);
//                for (int i = 0; i < count; i++) {
//                    Random r = new Random();
//                    ValueHelper valueHelper = HelperFactory.getValueHelper(server);
//
//
//                    valueHelper.recordValue(current.getName().getValue(), r.nextDouble() * 100);
//                    listener.onMessage("Recorded Value: ");
//
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        } else {
//            listener.onMessage("The current entity type: " + current.getEntityType().name() + " is not something that records data");
//        }
    }


    @Override
    public String getUsage() {
        return USAGE;
    }
}
