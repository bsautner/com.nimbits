package com.nimbits.command;

import com.nimbits.AbstractCommand;
import com.nimbits.client.model.server.Server;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public enum TerminalCommand {

    ls(ListCommand.class), cd(ChangeDirectoryCommand.class), exit(ExitCommand.class), help(HelpCommand.class);

    private Constructor constructor;
    public final static Map<String, TerminalCommand> lookupMap = new HashMap<>(TerminalCommand.values().length);

    static {
        for (TerminalCommand argument : TerminalCommand.values()) {
            lookupMap.put(argument.name(), argument);
        }
    }

    TerminalCommand(Class<? extends AbstractCommand> c) {
        try {
            constructor = c.getConstructor(Server.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }



    public AbstractCommand init(Server server) throws Exception {
        return (AbstractCommand) constructor.newInstance(server);
    }

    public static TerminalCommand lookup(String value) {
        if (lookupMap.containsKey(value)) {
            return lookupMap.get(value);
        }
        else {
            return null;
        }
    }


}
