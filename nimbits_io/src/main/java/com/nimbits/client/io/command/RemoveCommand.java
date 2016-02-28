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

public class RemoveCommand extends AbstractCommand implements Command {

    private final static String USAGE = "delete the current entity: rm <entity name> [-R]";

    public RemoveCommand(User user, Entity current, Instance server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {
        boolean recursive = false;

        if (args.length < 2) {
            listener.onMessage(getUsage());

        } else {
            Entity entity = getEntity(args[1]);
            if (entity == null) {
                listener.onMessage(args[1] + " not found");
            } else {

                for (String s : args) {
                    if (s.equals("-R")) {
                        recursive = true;
                        break;
                    }
                }
                boolean hc = hasChildren(entity);

                if (hc && !recursive) {

                    listener.onMessage("entity has children. Use -R to delete it and all children.");
                } else {
                    tree.remove(entity);
                    //helper.deleteEntity(entity);
                    listener.onTreeUpdated(tree);

                }

            }

        }

    }

    private boolean hasChildren(Entity entity) {
        for (Entity e : tree) {
            if (e.getParent().equals(entity.getKey())) {

                return true;

            }
        }
        return false;

    }

    private Entity getEntity(String name) {
        for (Entity entity : tree) {
            if (entity.getName().getValue().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public String getUsage() {
        return USAGE;
    }
}
