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

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;

import java.util.List;

public class CreateCommand extends AbstractCommand implements Command {

    private final static String USAGE = "create a new entity: create <entity type> <entity name> e.g create point foobar";

    public CreateCommand(User user, Entity current, Instance server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {

        if (args.length != 3) {
            System.out.println(USAGE);
        } else {
            try {
                String type = args[1];
                String name = args[2];
                EntityType entityType = EntityType.valueOf(type);
                if (entityType.equals(EntityType.point)) {

                    Point point = new PointModel.Builder().name(name).parent(current.getId()).create();// helper.createPoint(name, entityType, current);
                    tree.add(point);
                    listener.onTreeUpdated(tree);
                    listener.setCurrent(current);

                } else {
                    listener.onMessage("you can only create a type point for now.");
                }
            } catch (Exception ex) {
                listener.onMessage(ex.getMessage());
            }

        }


    }


    @Override
    public String getUsage() {
        return USAGE;
    }
}
