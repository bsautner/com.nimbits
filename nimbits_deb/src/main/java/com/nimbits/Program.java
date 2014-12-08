/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
 */

package com.nimbits;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.ApiKey;
import com.nimbits.client.model.server.apikey.ApiKeyFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.io.command.CommandListener;
import com.nimbits.io.command.TerminalCommand;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.UserHelper;
import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.SimpleCompletor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Program   {

    public static EmailAddress EMAIL_ADDRESS;
    private static UrlContainer INSTANCE_URL;
    private static ApiKey API_KEY;
    private static Server SERVER;
    public static User user;
    public static Entity current;
    public static List<Entity> tree = Collections.emptyList();

    private static List<SimpleCompletor> completorList = new LinkedList<>();
    private static MyReader currentReader;

    private static class MyReader extends ConsoleReader {
        private boolean closed;
        public MyReader() throws IOException {
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }

        public boolean isClosed() {
            return closed;
        }
    }

    public static void main(String[] args) throws Exception {



        loadDefaults();
        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER, EMAIL_ADDRESS, null);
        user = sessionHelper.getSession();
        current = user;
        new Thread(new TreeLoader()).run();

        createReader();



    }

    private static void startListening() throws IOException {
        String line;
        TerminalCommand terminalCommand;
        PrintWriter out = new PrintWriter(System.out);


        String prompt = SERVER.getUrl() + "/" + current.getName() + ":";
        currentReader.setDefaultPrompt(prompt);

        CommandListener listener = new CommandListener() {
            @Override
            public void onMessage(String message) {
                System.out.println(message);
            }

            @Override
            public void setCurrent(Entity newCurrent) {
                current = newCurrent;
            }

            @Override
            public void onTreeUpdated(List<Entity> newTree) {
                tree = newTree;
            }
        };


        while ((line = currentReader.readLine(prompt)) != null && ! currentReader.isClosed()) {

            try {

                String[] options = line.split(" ");
                String mainArg = options.length > 0 ? options[0].trim() : "";
                terminalCommand = TerminalCommand.lookup(mainArg);
                if (terminalCommand != null) {
                    terminalCommand.init(user, current, SERVER, tree).doCommand(listener, options);
                }
                else {
                    if (mainArg.trim().length() < 0) {
                        System.out.println("command \"" + mainArg + "\" not found. try \"help\"");
                    }
                }

            }  catch (Exception e) {
                e.printStackTrace();
            }

            out.flush();

        }
    }


    private static class TreeLoader implements Runnable {

        @Override
        public void run() {

            EntityHelper helper = HelperFactory.getEntityHelper(SERVER, Program.EMAIL_ADDRESS);
            Program.tree = helper.getTree();

            try {
                createReader();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private static  void createReader() throws IOException {
        if (currentReader != null) {
            currentReader.setClosed(true);
        }

        currentReader  = new MyReader();

        List<String> dir = new ArrayList<>();
        for (Entity e : Program.tree) {
            if (e.getParent().equals(current.getKey())) {
                dir.add(e.getName().getValue());
            }

        }
        String[] l = new String[dir.size()];
        dir.toArray(l);

        for (Object o : currentReader.getCompletors()) {
            currentReader.removeCompletor((Completor) o);
        }
        completorList.clear();
        String[] commandsList = new String[TerminalCommand.values().length];
        TerminalCommand.lookupMap.keySet().toArray(commandsList);


        completorList.add(new SimpleCompletor(commandsList));
        completorList.add(new SimpleCompletor(l));

        currentReader.addCompletor(new ArgumentCompletor(completorList));


        SimpleCompletor entityCompletor = new SimpleCompletor(commandsList);
        currentReader.addCompletor(entityCompletor);
        startListening();
    }

    private static void loadDefaults() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("/etc/default/nimbits"))) {

            String line = br.readLine();
            processDefault(line);
            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    processDefault(line);
                }

            }


        }
        SERVER =  ServerFactory.getInstance(INSTANCE_URL, API_KEY);
    }

    private static void processDefault(String value) {

        String[] s = value.split("=");
        switch (s[0]) {

            case "EMAIL":
                EMAIL_ADDRESS = CommonFactory.createEmailAddress(s[1]);
                break;
            case "APIKEY" :
                API_KEY = ApiKeyFactory.createApiKey(s[1]);
                break;
            case "INSTANCE" :
                INSTANCE_URL =  UrlContainer.getInstance(s[1]);

                break;

        }

    }

}
