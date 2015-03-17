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

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.AccessToken;

import com.nimbits.client.model.user.User;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.PointHelper;
import com.nimbits.io.helper.UserHelper;

public class CreatePointWithAPIKey {

    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("support@nimbits.com");

    //a running jetty server with nimbits installed (using root.war)
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost:8081");

    //you can create this server object with an API KEY you configured your server with to make authentication easy

    private static final AccessToken API_KEY = AccessToken.getInstance("API_KEY_DEFAULT");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL, EMAIL_ADDRESS, API_KEY);
    public static void main(String[] args) {
        String name = "test2";
        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER);
        EntityHelper entityHelper = HelperFactory.getEntityHelper(SERVER);
        PointHelper pointHelper = HelperFactory.getPointHelper(SERVER);


        User user = sessionHelper.getSession();
        System.out.println("hello " + user.getEmail());


        Category category = entityHelper.createFolder(user, name);

        if (! pointHelper.pointExists(name)) {
            Point point = pointHelper.createPoint(name, "test desc");
            System.out.println("created point " + point.getUUID());

        }
        else {
            Point point = pointHelper.getPoint(name);
            System.out.println("found point " + point.getUUID());
        }




    }


}
