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

package com.nimbits.server.user;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FeedType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.user.UserService;
import com.nimbits.server.email.EmailServiceFactory;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.feed.FeedServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;


public class UserServiceImpl extends RemoteServiceServlet implements
        UserService, UserServerService {
    private static final long serialVersionUID = 1L;
    //private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());



    @Override
    public User getHttpRequestUser(final HttpServletRequest req) throws NimbitsException {


        EmailAddress email;
        User user;
        String emailParam = null;
        String secret = null;
        HttpSession session = null;
        final com.google.appengine.api.users.UserService googleUserService = UserServiceFactory.getUserService();

        if (req != null) {
            emailParam = req.getParameter(Parameters.email.getText());
            secret = req.getParameter(Parameters.secret.getText());
            session = req.getSession();
        }


        if (emailParam != null && emailParam.equals(Const.TEST_ACCOUNT)) {
            user = UserTransactionFactory.getDAOInstance().getNimbitsUser(CommonFactoryLocator.getInstance().createEmailAddress(emailParam));

        } else {

            email = (!Utils.isEmptyString(emailParam)) ?
                    CommonFactoryLocator.getInstance().createEmailAddress(emailParam) : null;

            if (email == null && session != null && (session.getAttribute(Parameters.email.getText()) != null)) {
                email = (EmailAddress) session.getAttribute(Parameters.email.getText());
            }

            if (email == null && googleUserService.getCurrentUser() != null) {
                email = CommonFactoryLocator.getInstance().createEmailAddress(googleUserService.getCurrentUser().getEmail());
            }


            if (email != null) {


                user = UserTransactionFactory.getInstance().getNimbitsUser(email);
                if (user != null) {
                    //log.info("Found existing user");
                    // log.info("user has null email: " + String.valueOf(user.getEmail() == null));
                    // log.info(String.valueOf(user.getId()));
                    user.setRestricted(true);
                    // info.append("found user in datastore =").append(email.getValue()).append("|");
                    //did they provide a valid user secret?
                    if (!Utils.isEmptyString(secret) && !Utils.isEmptyString(user.getSecret()) && user.getSecret().equals(secret)) {
                        user.setRestricted(false);
                    }

                    //a secret was provided but it wasn't the user secret, maybe the global sever secret
//                    if (user.isRestricted() && !Utils.isEmptyString(secret)) {
//                        final String serverSecret = SettingsServiceFactory.getInstance().getServerSecret();
//                        if (!Utils.isEmptyString(serverSecret) && serverSecret.equals(secret)) {
//                            user.setRestricted(false);
//                        }
//                    }
                    //can we authenticate them with google auth?
                    //log.info("Is user authenticated with google account?" + (googleUserService.getCurrentUser() != null));
                    if (user.isRestricted()) {

                        if (googleUserService.getCurrentUser() != null
                                && googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(user.getEmail().getValue())) {
                            user.setRestricted(false);
                        }
                    }
                } else {
                    if (googleUserService.getCurrentUser() != null && googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(email.getValue())) {
                        user = UserTransactionFactory.getInstance().createNimbitsUser(email);
                    } else if (googleUserService.getCurrentUser() != null && !googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(email.getValue())) {
                        throw new NimbitsException("While the current user is authenticated, the email provided does not match " +
                                "the authenticated user, so the system is confused and cannot authenticate the request. " +
                                "Please report this error.");
                    } else if (googleUserService.getCurrentUser() == null) {
                        throw new NimbitsException(email.getValue() + " was provided but could not be found. Please log into nimbits with an account " +
                                " registered with google at least once.");
                    }


                }
            } else {
                throw new NimbitsException("There was no account connected to this request which requires authentication");
            }
        }

        if (user == null) {

            throw new NimbitsException("The user identity could not be found in the session, database, or via google authentication." +
                    " Please report this error to support@nimbits.com");
        }

        return user;

    }


    @Override
    public User getAppUserUsingGoogleAuth() throws NimbitsException {
        final com.google.appengine.api.users.UserService u = UserServiceFactory.getUserService();
        //u.getCurrentUser().

        User retObj = null;
        if (u.getCurrentUser() != null) {
            final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(u.getCurrentUser().getEmail());
            retObj = UserTransactionFactory.getInstance().getNimbitsUser(emailAddress);
        }

        return retObj;
    }

    @Override
    public String getSecret() throws NimbitsException {

        final String email = UserServiceFactory.getUserService().getCurrentUser().getEmail().toLowerCase();
        final EmailAddress internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
        final User u = UserTransactionFactory.getDAOInstance().getNimbitsUser(internetAddress);
        return u.getSecret();
    }

    @Override
    public User getUserByUUID(final String subscriberUUID) {
        return UserTransactionFactory.getInstance().getUserByUUID(subscriberUUID);
    }


    @Override
    public String updateSecret() throws NimbitsException {

        final String email = UserServiceFactory.getUserService().getCurrentUser().getEmail().toLowerCase();
        final EmailAddress internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
        final UUID secret = UUID.randomUUID();
        UserTransactionFactory.getInstance().updateSecret(internetAddress, secret);
        EmailServiceFactory.getInstance().sendEmail(internetAddress, "Your Nimbits Secret has been reset to: " + secret.toString(), "Reset Nimbits Secret");
        return secret.toString();
    }



    public void sendConnectionRequest(final EmailAddress email) throws NimbitsException {
        final User user = getAppUserUsingGoogleAuth();
        final Connection f = UserTransactionFactory.getInstance().makeConnectionRequest(user, email);


        if (f != null) {
            EmailServiceFactory.getInstance().sendEmail(email,  getConnectionInviteEmail(user.getEmail()));
            FeedServiceFactory.getInstance().postToFeed(user, "A connection request has been emailed to " +
                    email.getValue() + ". If they approve, you will see any data object of theirs that have " +
                    "their permission set to be viewable by the public or connections", FeedType.info);


        }


    }
    public static String getConnectionInviteEmail(final EmailAddress email) {
        return "<P STYLE=\"margin-bottom: 0in\"> " + email.getValue() +
                " wants to connect with you on <a href = \"http://www.nimbits.com\"> Nimbits! </A></BR></P><BR> \n" +
                "<P><a href = \"http://www.nimbits.com\">Nimbits</A> is a data logging service that you can use to record time series\n" +
                "data, such as sensor readings, GPS Data, stock prices or anything else into Data Points on the cloud.</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">Nimbits uses Google Accounts for\n" +
                "authentication. If you have a gmail account, you can sign into\n" +
                "Nimbits immediately  using that account. You can also register any\n" +
                "email address with google accounts and then sign in to Nimbits.  It\n" +
                "only takes a few seconds to register:\n" +
                "<A HREF=\"https://www.google.com/accounts/NewAccount\">https://www.google.com/accounts/NewAccount</A></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\"><A HREF=\"http://app.nimbits.com/\">Sign\n" +
                "into Nimbits</A> to approve this connection request.  <A HREF=\"http://www.nimbits.com/\">Go to \n" +
                "nimbits.com</A> to learn more.</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><BR><P STYLE=\"margin-bottom: 0in\"><STRONG>More about Nimbits Services</STRONG></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">Nimbits is a collection of software " +
                "designed for recording and working with time series data - such as " +
                "readings from a temperature probe, a stock price, or anything else " +
                "that changes over time - even textual and GPS data.  Nimbits allows " +
                "you to create online Data Points that provide a data channel into the " +
                "cloud.\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\">Nimbits Server, a data historian, is " +
                "available at <A HREF=\"http://www.nimbits.com/\">app.nimbits.com</A> " +
                "and provides a collection of web services, APIs and an interactive " +
                "portal enabling you to record data on a global cloud computing " +
                "infrastructure. You can also download and install your own instance " +
                "of a Nimbits Server, write your own software using Nimbits as a " +
                "powerful back end, or use our many free and open source downloads. " +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\">Built on cloud computing architecture,\n" +
                "and optimized to run on Google App Engine, you can run a Nimbits\n" +
                "Server with remarkable uptime and out of the box disaster recover\n" +
                "with zero upfront cost and a generous free quota. Then, only pay for\n" +
                "computing services you use with near limitless and instant\n" +
                "scalability when you need it. A typical 10 point Nimbits System costs\n" +
                "only pennies a week to run, and nothing at all when it's not in use.\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">As your data flows into a Nimbits Data\n" +
                "Point, values can be compressed, alarms can be triggered,\n" +
                "calculations can be performed and data can even be relayed to\n" +
                "facebook, Twitter or other connected systems.  You can chat with your\n" +
                "data over IM from anywhere, see and share your changing data values\n" +
                "in spreadsheets, diagrams and even on your phone with our free\n" +
                "android app. \n" +
                "</P>";
    }
    @Override
    public List<Connection> getPendingConnectionRequests(final EmailAddress email) {
        return UserTransactionFactory.getInstance().getPendingConnectionRequests(email);
    }

    @Override
    public List<User> getConnectionRequests(final List<String> connections) {
        return UserTransactionFactory.getInstance().getConnectionRequests(connections);
    }




    @Override
    public void connectionRequestReply(final EmailAddress targetEmail,
                                       final EmailAddress requesterEmail,
                                       final String uuid,
                                       final boolean accepted) throws NimbitsException {
        final User acceptor = getAppUserUsingGoogleAuth();

        final User requester = UserTransactionFactory.getInstance().getNimbitsUser(requesterEmail);

        final Entity rConnection = EntityModelFactory.createEntity(acceptor.getName(), "", EntityType.userConnection, ProtectionLevel.onlyMe, acceptor.getUuid(), requester.getUuid(), requester.getUuid());

        final Entity aConnection = EntityModelFactory.createEntity(requester.getName(), "", EntityType.userConnection, ProtectionLevel.onlyMe, requester.getUuid(), acceptor.getUuid(), acceptor.getUuid());

        EntityServiceFactory.getInstance().addUpdateEntity(acceptor, aConnection);
        EntityServiceFactory.getInstance().addUpdateEntity(requester,rConnection);

        UserTransactionFactory.getInstance().updateConnectionRequest(uuid, requester, acceptor, accepted);


    }


}
