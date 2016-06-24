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

package com.nimbits.server.process.cron;

import com.nimbits.server.communication.mail.EmailService;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsageResetCron extends HttpServlet {

    private static String USAGE_TRACKING = "usage tracking";


    @Autowired
    private SettingsService settingsService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ValueService valueService;

    @Autowired
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    @SuppressWarnings("unchecked")
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

//        String email = settingsService.getSetting(ServerSetting.admin);
//
//        List<User> admin = userService.getUserByKey(email, AuthLevel.admin);
//
//        if (! admin.isEmpty()) {
//
//
//            EntityName folderName = CommonFactory.createName(USAGE_TRACKING, EntityType.category);
//            User adminUser = admin.get(0);
//
//            List<Entity> folders = entityService.getEntityByName(adminUser, folderName, EntityType.category);
//
//            if (! folders.isEmpty()) {
//
//
//                List<Entity> entities = entityService.getChildren(adminUser, folders);
//                for (Entity entity : entities) {
//
//                    if (entity.getEntityType().equals(EntityType.point)) {
//                        Value currentSample = valueService.getCurrentValue(entity);
//                        if (currentSample != null) {
//                            if (currentSample.getDoubleValue() > 100000) {
//                                emailService.sendEmail(CommonFactory.createEmailAddress("test@example.com"),
//                                       "<h4>Greetings from the Nimbits Public Cloud!</h4>\n" +
//                                               "\n" +
//                                               "<p>We wanted to let you know about a few things:\n" +
//                                               "\n<ul>" +
//                                               "<li>  We needed a way to log how much people are using the free Nimbits cloud. " +
//                                               " Good thing we have just the tool for that, Nimbits!  " +
//                                               "We configured a Nimbits server on our end to trigger this email when traffic from an account gets high.</li> \n" +
//                                               "\n" +
//                                               "<li>  Nimbits is getting pretty popular these days and it's costing us a lot to run the public cloud which is there to let new users get to " +
//                                               "know the system and try it out.  If you can believe it, we've been logging data for 8 years with over 99% uptime.\n" +
//                                               "</li>\n" +
//                                               "<li>  Requests that exceed our free quota may result in a 503 error in the future.  \n" +
//                                               "</li>\n" +
//                                               "How you can help:\n" +
//                                               "</li>\n" +
//                                               "<li>  Please try and limit your use of the public cloud.  Posting lots of data to the cloud is expensive, but you can put other nimbits servers on your network to act as a buffer. " +
//                                               " Nimbits even runs on a raspberry pi.  Many users buffer data locally and only post important changes once a day to the series api.  " +
//                                               "Putting data on the cloud means it's replicated across multiple data centers in real time, and it's not always practical to log something" +
//                                               " like a second by second sensor reading in that way.\n" +
//                                               "</li>\n" +
//                                               "<li>   Build a nimbits server to use on your hardware or cloud of choice:  http://nimbits.com/download.jsp\n" +
//                                               "</li>\n" +
//                                               "<li>  Let us build a cloud for you.  http://nimbits.com/registercloud.jsp  You'll only pay a setup fee and a monthly maintenance fee + the cost of resources consumed.  Scale to any size!  Proceeds keep Nimbits Free and Open Source.  \n" +
//                                               "</li>" +
//                                               "</ul>\n" +
//                                               "<p>We really appreciate you using our software and want to help you get value from it as well as keep the vision of an open source, " +
//                                               "distributed platform for the Internet of Things alive.\n" +
//                                               "</p>\n" +
//                                               "<p>Please feel free to reply to this email if you have any questions or concerns.</p>\n" +
//                                               "\n" +
//                                               "<p>- The Nimbits Team</p>\n" +
//                                               "",
//                                        "Nimbits Public Cloud Usage Alert");
//                            }
////                            Value zero = ValueFactory.createValue(value.getDoubleValue() * -1);
////                            try {
////                               // valueService.recordValue(adminUser, (Point) entity, zero, true);
////                            } catch (ValueException e) {
////                                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
////                                logger.error(e.getMessage());
////                            }
//                        }
//                    }
//
//                }
//
//            }


        //      }

    }

}
