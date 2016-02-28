/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.io.command;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.user.User;

import java.util.List;

public class SeedRandomCommand extends AbstractCommand implements Command {

    private final static String USAGE = "Seed the current data point with random data: seed <count>";

    public SeedRandomCommand(User user, Entity current, Instance server, List<Entity> tree) {
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
