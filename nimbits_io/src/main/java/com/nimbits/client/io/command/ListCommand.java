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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListCommand extends AbstractCommand implements Command {

    private final static String USAGE = "list child entities";
    private final static Logger logger = LoggerFactory.getLogger(ListCommand.class.getName());

    public ListCommand(User user, Entity current, Instance server, List<Entity> tree) {
        super(user, current, server, tree);
    }

    @Override
    public void doCommand(CommandListener listener, String[] args) {

        String contains = args.length > 1 ? args[1] : "";
        for (Entity entity : tree) {
            if (entity.getParent().equals(current.getId())) {
                if (contains.equals("")) {
                    listener.onMessage(entity.getName().getValue());
                } else {
                    if (entity.getName().toString().contains(contains)) {
                        listener.onMessage(entity.getName().getValue());
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
