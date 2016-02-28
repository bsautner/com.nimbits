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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TerminalCommand {

    ls(ListCommand.class, true),
    cd(ChangeDirectoryCommand.class, true),
    exit(ExitCommand.class, false),
    help(HelpCommand.class, false),
    create(CreateCommand.class, true),
    rm(RemoveCommand.class, true),
    seed(SeedRandomCommand.class, false);

    private Constructor constructor;
    private boolean usesTree;
    public final static Map<String, TerminalCommand> lookupMap = new HashMap<String, TerminalCommand>(TerminalCommand.values().length);

    static {
        for (TerminalCommand argument : TerminalCommand.values()) {
            lookupMap.put(argument.name(), argument);
        }
    }

    TerminalCommand(Class<? extends AbstractCommand> c, boolean usesTree) {
        this.usesTree = usesTree;
        try {
            constructor = c.getConstructor(User.class, Entity.class, Instance.class, List.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public AbstractCommand init(User user, Entity current, Instance server, List<Entity> tree) throws Exception {
        return (AbstractCommand) constructor.newInstance(user, current, server, tree);
    }

    public static TerminalCommand lookup(String value) {
        if (lookupMap.containsKey(value)) {
            return lookupMap.get(value);
        } else {
            return null;
        }
    }

    public boolean usesTree() {
        return usesTree;
    }
}
