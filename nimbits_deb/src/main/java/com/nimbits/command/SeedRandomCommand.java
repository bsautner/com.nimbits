package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.Command;
import com.nimbits.Program;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.ValueHelper;

import java.util.Random;

public class SeedRandomCommand extends AbstractCommand implements Command {

    private final static String USAGE = "Seed the current data point with random data: seed <count>";

    public SeedRandomCommand(Server server) {
        super(server);
    }

    @Override
    public void doCommand(String[] args) {
        if (Program.current.getEntityType().recordsData()) {
            if (args.length != 2) {
                System.out.print("try: \"seed 10\" to seed 10 random values");
            }
            else {
                int count = Integer.parseInt(args[1]);
                for (int i = 0; i < count; i++) {
                    Random r = new Random();
                    ValueHelper valueHelper = HelperFactory.getValueHelper(server, Program.user.getEmail(), null);


                    Value value = valueHelper.recordValue(Program.current.getName().getValue(), r.nextDouble() * 100);
                    System.out.println("Recorded Value: " + value.getValueWithData());

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        else {
            System.out.println("The current entity type: " + Program.current.getEntityType().name() + " is not something that records data");
        }
    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
