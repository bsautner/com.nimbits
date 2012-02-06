/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

import com.nimbits.client.NimbitsClient;
import com.nimbits.client.NimbitsClientFactory;
import com.nimbits.client.enums.Action;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.console.KeyFile;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.user.GoogleUser;
import com.nimbits.user.NimbitsUser;
import com.nimbits.xmpp.XMPPClient;
import com.nimbits.xmpp.XMPPClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.XMPPException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
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

    public static void main(final String[] args) throws IOException, XMPPException, NimbitsException {
        final HashMap<String, String> argsMap = new HashMap<String, String>();

        if (args == null || args.length == 0) {
            printUsage();
            return;
        } else {
            processArgs(args, argsMap);
        }

        if (argsMap.containsKey(Const.PARAM_I)) {
            String[] fileArgs = KeyFile.processKeyFile(argsMap);
            processArgs(fileArgs, argsMap);
        }

        final boolean verbose = argsMap.containsKey(Const.PARAM_VERBOSE);
        final boolean listen = argsMap.containsKey(Const.PARAM_LISTEN);
        final String host = argsMap.containsKey(Const.PARAM_HOST) ? argsMap.get(Const.PARAM_HOST) : Const.PATH_NIMBITS_PUBLIC_SERVER;
        final String emailParam = argsMap.containsKey(Const.PARAM_EMAIL) ? argsMap.get(Const.PARAM_EMAIL) : null;
        final String key = argsMap.containsKey(Const.PARAM_KEY) ? argsMap.get(Const.PARAM_KEY) : null;
        final String appId = argsMap.containsKey(Const.PARAM_APP_ID) ? argsMap.get(Const.PARAM_APP_ID) : null;
        final String password = argsMap.containsKey(Const.PARAM_PASSWORD) ? argsMap.get(Const.PARAM_PASSWORD) : null;
        final EmailAddress email = CommonFactoryLocator.getInstance().createEmailAddress(emailParam);
        final NimbitsClient client = createClient(host, email, key, password);

        final boolean loggedIn = checkLoggedIn(client, verbose);

        if (!loggedIn) {
            out(true, "Access Denied.");
            return;
        }

        if (listen) {
            if (StringUtils.isEmpty(appId)) {
                out(true, "In order to connect to the xmpp listener, please supply your app engine app id (i.e -appid=Nimbits1)");
            } else {
                xClient = XMPPClientFactory.getInstance(client, appId);

                try {
                    boolean connected = xClient.connect();
                    if (connected) {
                        out(true, "Connected to " + appId + "over xmpp");
                        interact();
                    }
                } catch (NimbitsException e) {
                    out(true, e.getMessage());
                }
            }
        } else {
            if (argsMap.containsKey(Const.PARAM_ACTION) && loggedIn) {
                Action action = Action.valueOf(argsMap.get(Const.PARAM_ACTION));

                switch (action) {
                    case read:
                    case readValue:
                    case readGps:
                    case readJson:
                    case readNote:
                        readValue(client, argsMap, action);
                        break;
                    case record:
                    case recordValue:
                        recordValue(client, argsMap, verbose);
                        break;
                    default:
                        printUsage();
                }
            } else if (argsMap.containsKey(Const.PARAM_GENKEY) && argsMap.containsKey(Const.PARAM_OUT)) {

                out(true, KeyFile.genKey(argsMap));

            }
        }


        out(true, "exiting");


    }

    private static void interact() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = br.readLine();
            if (!input.equals(Const.WORD_EXIT)) {
                out(true, input);
                xClient.sendMessage(input);
                interact();
            }


        } catch (IOException e) {
            out(true, e.getMessage());
        }

    }

    private static void readValue(final NimbitsClient client, final Map<String, String> argsMap, Action action) {
        final PointName pointName = CommonFactoryLocator.getInstance().createPointName(argsMap.get(Const.PARAM_POINT));
        final Value v = client.getCurrentRecordedValue(pointName);

        switch (action) {
            case read:
            case readValue:
                out(true, String.valueOf(v.getNumberValue()));
                break;
            case readGps:
                out(true, v.getLatitude() + "," + v.getLongitude());
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
        final double d = argsMap.containsKey(Const.PARAM_VALUE) ? Double.valueOf(argsMap.get(Const.PARAM_VALUE)) : 0.0;
        final String note = argsMap.containsKey(Const.PARAM_NOTE) ? argsMap.get(Const.PARAM_NOTE) : null;
        final double lat = argsMap.containsKey(Const.PARAM_LAT) ? Double.valueOf(argsMap.get(Const.PARAM_LAT)) : 0.0;
        final double lng = argsMap.containsKey(Const.PARAM_LNG) ? Double.valueOf(argsMap.get(Const.PARAM_LNG)) : 0.0;

        return ValueModelFactory.createValueModel(lat, lng, d, new Date(), 0, note, "");

    }

    private static void recordValue(final NimbitsClient client, final Map<String, String> argsMap, final boolean verbose) throws IOException {
        out(verbose, "Recording values");

        final Value v = buildValue(argsMap);
        final PointName pointName = CommonFactoryLocator.getInstance().createPointName(argsMap.get(Const.PARAM_POINT));
        final Value result = client.recordValue(pointName, v);
        if (result == null) {
            out(verbose, "An error occurred recording your data");
        } else {
            out(verbose, result.getNumberValue() + " recorded to " + pointName);
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

    private static NimbitsClient createClient(final String host, final EmailAddress email, final String key, final String password) {
        NimbitsClient client = null;
        if (StringUtils.isNotEmpty(key) && (email) != null) {
            NimbitsUser n = new NimbitsUser(email, key);
            client = NimbitsClientFactory.getInstance(n, host);
        } else if (StringUtils.isNotEmpty(password) && email != null) {
            GoogleUser g = new GoogleUser(email, password);
            try {
                client = NimbitsClientFactory.getInstance(g, host);
            } catch (Exception e) {
                out(true, e.getMessage());
            }
        }
        return client;
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