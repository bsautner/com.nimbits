/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package integration;/*
 * Copyright (c) 2011. Nimbits Inc. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

import com.nimbits.client.NimbitsClient;
import com.nimbits.client.NimbitsClientFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.user.NimbitsUser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/11
 * Time: 10:42 AM
 */
class ClientHelper {
    // private final static String hostURL = "http://nimbits1.appspot.com";

    //private final static String hostURL = "http://127.0.0.1:8888";
    // private final static String hostURL = "http://nimbits-qa.appspot.com";
    //   private final static String key = "cd5ecf67-f22b-4180-807e-b700c8ec98bc";


    private static NimbitsClient instance = null;
    private static NimbitsClient instance2 = null;
    // private final static String hostURL2 = "http://nimbits1.appspot.com";
    //  private final static String hostURLSpecific = "http://147.nimbits1.appspot.com";
    //  private static final String accountPath = "/mnt/raid/nimbits/test";
    private  static String email;

    private  static String password;

    //  private final static String appscale =  "http://127.0.0.1:8888";
    public  static String url;

    public static NimbitsClient client() throws NimbitsException {
         email =  "test@example.com";
        url ="http://localhost:8082";



      //  email =  "bsautner@gmail.com";
      //  url ="http://nimbits1.appspot.com";

        password = "key";

        //  private final static String appscale =  "http://127.0.0.1:8888";



        if (instance == null) {
            //  String email = loadFile(accountPath + "/a1", 0);
            //  String password = loadFile(accountPath + "/a1", 1);
            EmailAddress em = CommonFactory.createEmailAddress(email);
            NimbitsUser g = new NimbitsUser(em, password);

            try {
                instance = NimbitsClientFactory.getInstance(g, url);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return instance;

    }


    public static Point createSeedPoint(EntityName name) throws NimbitsException {


        Point point = client().addPoint(name);
        Random r = new Random();

        if (point != null) {
            for (int i = 0; i < 10; i++) {

                client().recordValue(name, r.nextDouble(), new Date());

            }
        }
        return point;
    }


//    public static NimbitsClient meOnProd() {
//        if (instance == null) {
//            String email = loadFile(accountPath + "/a3", 0);
//            String password = loadFile(accountPath + "/a3", 1);
//            EmailAddress em = CommonFactory.createEmailAddress(email);
//            NimbitsUser g = new NimbitsUser(em, password);
//
//            try {
//                instance = NimbitsClientFactory.getInstance(g, hostURL2);
//            } catch (Exception e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//        return instance;
//
//    }


//    public static NimbitsClient clientSupportOnProd() {
//        if (instance2 == null) {
//            String email = loadFile(accountPath + "/a2", 0);
//            String password = loadFile(accountPath + "/a2", 1);
//            EmailAddress em = (EmailAddress) CommonFactory.createEmailAddress(email);
//            GoogleUser g = new GoogleUser(em, password);
//            System.out.println(email);
//            try {
//                instance2 = NimbitsClientFactory.getInstance(g, hostURL);
//            } catch (Exception e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//        return instance2;
//
//    }
//
//    public static NimbitsClient specificVersion() {
//        if (instance2 == null) {
//            String email = loadFile(accountPath + "/a2", 0);
//            String password = loadFile(accountPath + "/a2", 1);
//            EmailAddress em = CommonFactory.createEmailAddress(email);
//            GoogleUser g = new GoogleUser(em, password);
//            System.out.println(email);
//            try {
//                instance2 = NimbitsClientFactory.getInstance(g, hostURLSpecific);
//            } catch (Exception e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//        return instance2;
//
//    }
//
//    public static NimbitsClient authOnQA() {
//        if (instance2 == null) {
//            String email = loadFile(accountPath + "/a2", 0);
//            String password = loadFile(accountPath + "/a2", 1);
//            EmailAddress em = CommonFactory.createEmailAddress(email);
//            GoogleUser g = new GoogleUser(em, password);
//            System.out.println(email);
//            try {
//                instance2 = NimbitsClientFactory.getInstance(g, hostURL);
//            } catch (Exception e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//        return instance2;
//
//    }

    private static String loadFile(String fileName, int i) {

        java.lang.StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        String str;
        try {
            in = new BufferedReader(new FileReader(fileName));
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return sb.toString().split(",")[i];  //To change body of implemented methods use File | Settings | File Templates.
    }

}
