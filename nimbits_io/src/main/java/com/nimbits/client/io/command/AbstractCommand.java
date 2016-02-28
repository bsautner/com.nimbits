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

public abstract class AbstractCommand implements Command {

    protected final Instance server;
    protected final User user;
    protected final Entity current;
    protected final List<Entity> tree;


    public AbstractCommand(User user, Entity current, Instance server, List<Entity> tree) {
        this.server = server;
        this.user = user;
        this.current = current;
        this.tree = tree;

    }

    abstract public void doCommand(CommandListener listener, String[] args);

}
