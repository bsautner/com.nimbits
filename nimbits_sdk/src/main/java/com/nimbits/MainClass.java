/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits;

import com.nimbits.cloudplatform.client.NimbitsClient;
import com.nimbits.cloudplatform.client.NimbitsClientFactory;
import com.nimbits.cloudplatform.client.constants.Path;
import com.nimbits.cloudplatform.client.constants.Words;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.exception.NimbitsException;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.location.Location;
import com.nimbits.cloudplatform.client.model.location.LocationFactory;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.console.KeyFile;
import com.nimbits.mqtt.Listen;
import com.nimbits.user.GoogleUser;
import com.nimbits.user.NimbitsUser;
import com.nimbits.xmpp.XMPPClient;
import com.nimbits.xmpp.XMPPClientFactory;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.XMPPException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/8/11
 * Time: 2:42 PM
 */
public class MainClass {
    private static XMPPClient xClient;
    private static NimbitsClient client;

//    private NimbitsClient createClient(String email, String key, String host, String password) throws NimbitsException {
//        final EmailAddress emailAddress = CommonFactory.createEmailAddress(email);
//        final NimbitsClient client = createClient(host, email, key, password);
//        try {
//            final boolean loggedIn = checkLoggedIn(client, false);
//            if (!loggedIn) {
//                throw new NimbitsException("Access Denied");
//            }
//        } catch (IOException e) {
//            throw new NimbitsException(e);
//        }
//    }


    public static void main(final String[] args) throws IOException, XMPPException, NimbitsException {
        final HashMap<String, String> argsMap = new HashMap<String, String>();

        if (args == null || args.length == 0) {
            printUsage();
            return;
        } else {
            processArgs(args, argsMap);
        }

        if (argsMap.containsKey(Parameters.i.getText())) {
            String[] fileArgs = KeyFile.processKeyFile(argsMap);
            processArgs(fileArgs, argsMap);
        }

        final boolean verbose = argsMap.containsKey(Parameters.verbose.getText());
        final boolean listen = argsMap.containsKey(Parameters.listen.getText());
        final String host = argsMap.containsKey(Parameters.host.getText()) ? argsMap.get(Parameters.host.getText()) : Path.PATH_NIMBITS_PUBLIC_SERVER;
        final String emailParam = argsMap.containsKey(Parameters.email.getText()) ? argsMap.get(Parameters.email.getText()) : null;
        final String key = argsMap.containsKey(Parameters.key.getText()) ? argsMap.get(Parameters.key.getText()) : null;
        final String appId = argsMap.containsKey(Parameters.appid.getText()) ? argsMap.get(Parameters.appid.getText()) : null;
        final String password = argsMap.containsKey(Parameters.password.getText()) ? argsMap.get(Parameters.password.getText()) : null;

        final String protocol = argsMap.containsKey(Parameters.protocol.getText()) ? argsMap.get(Parameters.protocol.getText()) :null;

        if (StringUtils.isEmpty(emailParam)) {
            throw new NimbitsException("you must specify an account i.e. email=test@example.com");
        }

       if (StringUtils.isNotEmpty(host) && StringUtils.isNotEmpty(emailParam) & StringUtils.isNotEmpty(key)) {
          createClient(host, emailParam, key, password);
       }
//
//        if (!loggedIn) {
//            out(true, "Access Denied.");
//            return;
//        }


            if (argsMap.containsKey(Parameters.action.getText())) {
                Action action = Action.valueOf(argsMap.get(Parameters.action.getText()));

                switch (action) {
                    case read:
                    case readValue:
                    case readGps:
                    case readJson:
                    case readNote:
                        readValue(argsMap, action);
                        break;
                    case record:
                    case recordValue:
                        recordValue(argsMap, verbose);
                        break;
                    case listen:
                        listen(appId, protocol, emailParam);
                        break;


                     default:
                        printUsage();
                }
            } else if (argsMap.containsKey(Parameters.genkey.getText()) && argsMap.containsKey(Parameters.out.getText())) {

                out(true, KeyFile.genKey(argsMap));

            }



        out(true, "exiting");


    }

    private static void listen(String appId, String protocol, String email ) throws NimbitsException {

            if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(protocol)) {
                out(true, "In order to connect to the xmpp/mqtt listener, please supply your app engine app id (i.e -appid=Nimbits1 -protocol=mqtt)");
            }

            else  {
                if (protocol != null && protocol.equals("xmpp")) {


                    xClient = XMPPClientFactory.getInstance(client, appId);

                    try {
                        boolean connected = xClient.connect(appId);
                        if (connected) {
                            out(true, "Connected to " + appId + "over xmpp");
                            interact();
                        }
                    } catch (NimbitsException e) {
                        out(true, e.getMessage());
                    }
                }
                else if (protocol != null && protocol.equals("mqtt")) {
                        String topic = appId + "/#";

                        Listen listener = new Listen(email);
                        listener.subscribe(topic);
                }
            }

    }

    private static void interact() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = br.readLine();
            if (!input.equals(Words.WORD_EXIT)) {
                out(true, input);
                xClient.sendMessage(input);
                interact();
            }


        } catch (IOException e) {
            out(true, e.getMessage());
        }

    }

    private static void readValue(final Map<String, String> argsMap, Action action) throws NimbitsException {
        final EntityName pointName = CommonFactory.createName(argsMap.get(Parameters.point.getText()), EntityType.point);
        final Value v = client.getCurrentRecordedValue(pointName);

        switch (action) {
            case read:
            case readValue:
                out(true, String.valueOf(v.getDoubleValue()));
                break;
            case readGps:
                out(true, v.getLocation().toString());
                break;
            case readNote:
                out(true, v.getNote());
                break;
            case readJson:
                out(true, GsonFactory.getInstance().toJson(v));
                break;
        }
    }

    private static Value buildValue(final Map<String, String> argsMap) {
        final double d = argsMap.containsKey(Parameters.value.getText()) ? Double.valueOf(argsMap.get(Parameters.value.getText())) : 0.0;
        final String note = argsMap.containsKey(Parameters.note.getText()) ? argsMap.get(Parameters.note.getText()) : null;
        final double lat = argsMap.containsKey(Parameters.lat.getText()) ? Double.valueOf(argsMap.get(Parameters.lat.getText())) : 0.0;
        final double lng = argsMap.containsKey(Parameters.lng.getText()) ? Double.valueOf(argsMap.get(Parameters.lng.getText())) : 0.0;
        Location location = LocationFactory.createLocation(lat, lng);
        return null;//ValueFactory.createValueModel(location, d, new Date(), note, ValueFactory.createValueData(""), AlertType.OK);

    }

    private static void recordValue(final Map<String, String> argsMap, final boolean verbose) throws IOException, NimbitsException {
        out(verbose, "Recording values");

        final Value v = buildValue(argsMap);
        final EntityName pointName = CommonFactory.createName(argsMap.get(Parameters.point), EntityType.point);
        final Value result = client.recordValue(pointName, v);
        if (result == null) {
            out(verbose, "An error occurred recording your data");
        } else {
            out(verbose, result.getDoubleValue() + " recorded to " + pointName);
        }
    }

    private static boolean checkLoggedIn(final NimbitsClient client, final boolean verbose) throws IOException, NimbitsException {
        boolean loggedIn = false;
        if (client != null) {
            out(verbose, "Authenticating...");
            loggedIn = client.isLoggedIn();
            if (loggedIn) {
                out(verbose, "Success");
            } else {
                out(verbose, "Authentication Failure");
            }
        }
        return loggedIn;
    }

    private static void createClient(final String host, final String email, final String key, final String password) throws NimbitsException {

        EmailAddress emailAddress = CommonFactory.createEmailAddress(email);
        if (StringUtils.isNotEmpty(key) && (emailAddress) != null) {
            NimbitsUser n = new NimbitsUser(emailAddress, key);
            client = NimbitsClientFactory.getInstance(n, host);
        } else if (StringUtils.isNotEmpty(password) && email != null) {
            GoogleUser g = new GoogleUser(emailAddress, password);
            try {
                client = NimbitsClientFactory.getInstance(g, host);
            } catch (Exception e) {
                out(true, e.getMessage());
            }
        }

    }

    private static void processArgs(final String[] args, final Map<String, String> argsMap) {
        for (String s : args) {

            String[] a = s.split("=");
            if (a.length == 2) {
                argsMap.put(a[0].replaceFirst("-", "").toLowerCase(), a[1].trim());
            } else {
                argsMap.put(s.replaceFirst("-", "").toLowerCase(), "");
            }


        }
    }

    private static void out(boolean verbose, final String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    private static void printUsage() {
        out(true, "Unexpected parameters. Please refer to the SDK Manual on www.nimbits.com.");
    }

}