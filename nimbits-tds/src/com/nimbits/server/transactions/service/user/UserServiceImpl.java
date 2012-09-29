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

package com.nimbits.server.transactions.service.user;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.*;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.connection.ConnectionFactory;
import com.nimbits.client.model.connection.ConnectionRequest;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.user.UserService;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.admin.quota.QuotaManagerImpl;
import com.nimbits.server.api.openid.UserInfo;
import com.nimbits.server.communication.email.EmailServiceImpl;
import com.nimbits.server.transactions.memcache.user.UserCacheImpl;
import com.nimbits.server.transactions.service.entity.EntityServiceImpl;
import com.nimbits.server.transactions.service.feed.FeedImpl;
import com.nimbits.server.transactions.service.value.ValueServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

@Service("userService")
@Transactional
public class UserServiceImpl extends RemoteServiceServlet implements
        UserService, UserServerService {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());

    public static final String ACCOUNT_BALANCE_DESC = "Account balance - enable billing and find your account on nimbits.com to use services beyond the free quota";
    public static final String UNIT = "USD";
    public static final int EXPIRE = 730;
    public static final String QUOTA_EXCEEDED_NAME = "Quota Exceeded Notification";
    public static final String ACCOUNT_FUNDED_NAME = "Account Balance Increase Notification";
    public static final int MAX_REPEAT = 86400;
    public static final String ZERO_BALANCE_ALERT_NAME = "Account Balance Zero Notification";
    public static final String QUOTA_EXCEEDED_DESC = "This is an alert that notifies you when you've exceeded your set quota for the day.";
    public static final String ZERO_BALANCE_DESC = "This is an alert that notifies you when your account balance is at zero";
    public static final String ACCOUNT_FUNDED_DESC = "This is an alert that notifies you when your account is funded. You still need to log into nimbits and select Billing Options" +
            " from the Settings Menu. There, you can configure a daily budget and enable or disable billing";

    private EntityServiceImpl entityService;
    private FeedImpl feedService;
    private ValueServiceImpl valueService;
    private SettingsService settingsService;
    private EmailServiceImpl emailService;
    private UserCacheImpl userCache;
    private QuotaManagerImpl quotaManager;


    @Override
    public User getHttpRequestUser(final HttpServletRequest req) throws NimbitsException {


        String emailParam = null;
        HttpSession session = null;
        final com.google.appengine.api.users.UserService googleUserService = UserServiceFactory.getUserService();
        String accessKey = null;
        String uuid = null;
        UserInfo domainUser = null;
        if (req != null) {
            session = req.getSession();

            emailParam = req.getParameter(Parameters.email.getText());
            if (session != null) {
                domainUser = (UserInfo) req.getSession().getAttribute("user");
                if (domainUser != null) {

                    emailParam = domainUser.getEmail();

                }
            }

            uuid =  req.getParameter(Parameters.uuid.getText());

            accessKey = req.getParameter(Parameters.secret.getText());
            if (Utils.isEmptyString(accessKey)) {
                accessKey = req.getParameter(Parameters.key.getText());
            }
        }

        EmailAddress email = Utils.isEmptyString(emailParam) ? null : CommonFactoryLocator.getInstance().createEmailAddress(emailParam);

        if (email == null && session != null && session.getAttribute(Parameters.email.getText()) != null) {
            email = (EmailAddress) session.getAttribute(Parameters.email.getText());
        }

        try {
            if (googleUserService != null) {
                if (email == null && googleUserService.getCurrentUser() != null) {
                    email = CommonFactoryLocator.getInstance().createEmailAddress(googleUserService.getCurrentUser().getEmail());
                }
            }
        } catch (NullPointerException e) {
            email = null;
        }


        if (email == null && Utils.isEmptyString(uuid)) {
            throw new NimbitsException("There was no account connected to this request and no key or uuid, nothing to do.");
        }

        boolean anonRequest = false;

        if (! Utils.isEmptyString(uuid) && email==null) { //a request with just a uuid must be public
            List<Entity> anon = entityService.findEntityByKey(getAnonUser(),  uuid);
            anonRequest = true;
            if (! anon.isEmpty()) {

                Entity anonEntity = anon.get(0);
                if (anonEntity.getProtectionLevel().equals(ProtectionLevel.everyone)) {

                    email = CommonFactoryLocator.getInstance().createEmailAddress(anon.get(0).getOwner());
                }
                else {
                    throw new NimbitsException("The object you requested was found, but its protection level was set to high to access. Try " +
                            "adding an email address and access key to your request.");
                }

            }
        }

        User user = null;
        if (email != null) {



            final List<Entity> result = entityService.getEntityByKey(
                     getAdmin(), //avoid infinite recursion
                    email.getValue(), EntityType.user);



            if (result.isEmpty()) {
                if (googleUserService.getCurrentUser() != null && googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(email.getValue())) {
                    user = createUserRecord(email);
                    user.addAccessKey(authenticatedKey(user));
                }
                else if (domainUser != null) {
                    user = createUserRecord(email);
                    user.addAccessKey(authenticatedKey(user));
                }
                else if (googleUserService.getCurrentUser() != null && !googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(email.getValue())) {
                    throw new NimbitsException("While the current user is authenticated, the email provided does not match " +
                            "the authenticated user, so the system is confused and cannot authenticate the request. " +
                            "Please report this error.");
                } else if (googleUserService.getCurrentUser() == null) {
                    throw new NimbitsException(email.getValue() + " was provided but could not be found. Please log into nimbits with an account " +
                            " registered with google at least once.");
                }


            } else {
                user = (User) result.get(0);
                //
                if (! anonRequest) {
                    if (!Utils.isEmptyString(accessKey)) {
                        //all we have is an email of an existing user, let's see what they can do.
                        final Map<String, Entity> keys = entityService.getEntityMap(user, EntityType.accessKey, 1000);
                        for (final Entity k : keys.values()) {
                            if (((AccessKey)k).getCode().equals(accessKey)) {
                                user.addAccessKey((AccessKey) k);
                            }
                        }


                    }
                }
                if (user.isRestricted()) {

                    if (googleUserService.getCurrentUser() != null
                            && googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(user.getEmail().getValue())) {

                        user.addAccessKey(authenticatedKey(user)); //they are logged in
                    }
                }
                if (domainUser != null) {
                    user.addAccessKey(authenticatedKey(user)); //they are logged in from google apps
                }

            }
        }
        else {
            throw new NimbitsException("There was no account connected to this request");
        }

        return user;

    }

    @Override
    public User login(final String requestUri) throws NimbitsException {

        final User retObj;
        EmailAddress internetAddress = null;
        boolean isAdmin = false;
        UserInfo domainUser=null;
        if (this.getThreadLocalRequest() != null) {
            domainUser = (UserInfo) this.getThreadLocalRequest().getSession().getAttribute("user");
        }
        final com.google.appengine.api.users.UserService userService = UserServiceFactory.getUserService();

        if (domainUser != null) {
            log.info("domain user:" + domainUser.getEmail());
            internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(domainUser.getEmail());
        }
        else {

            final com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
            if (googleUser != null)   {
                isAdmin= userService.isUserAdmin();
                internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(googleUser.getEmail());
            }
        }

        if (internetAddress != null) {

            final List<Entity> list =   entityService
                    .getEntityByKey(
                             getAnonUser(), internetAddress.getValue(), EntityType.user);


            if (list.isEmpty()) {
                LogHelper.log(this.getClass(), "Created a new user");
                retObj =  createUserRecord(internetAddress);
                // sendUserCreatedFeed(u);
                // sendWelcomeFeed(u);
            }
            else {
                retObj = (com.nimbits.client.model.user.User) list.get(0);
            }

            retObj.setLoggedIn(true);

            retObj.setUserAdmin(isAdmin);

            retObj.setLogoutUrl(userService.createLogoutURL(requestUri));

            retObj.setLastLoggedIn(new Date());
            entityService.addUpdateEntity(retObj, retObj);
            retObj.addAccessKey(authenticatedKey(retObj));


            // A user has logged in through google auth - this creates the user

        }
        else {
            final EntityName name = CommonFactoryLocator.getInstance().createName("anon@nimbits.com", EntityType.user);
            final Entity e = EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe, "", "");
            retObj = UserModelFactory.createUserModel(e);
            retObj.setLoggedIn(false);
            retObj.setLoginUrl(userService.createLoginURL(requestUri));
        }
        return retObj;
    }

    @Override
    public Integer getQuota() throws NimbitsException {
        User user= getHttpRequestUser(this.getThreadLocalRequest());
        return quotaManager.getCount(user.getEmail());

    }

    @Override
    public List<Point> getAccountBalance() throws NimbitsException {
        User user= getHttpRequestUser(this.getThreadLocalRequest());
        Point accountBalance = getAccountBalancePoint(user);
        List<Value> valueSample =  valueService.getCurrentValue(accountBalance);
        if (valueSample.isEmpty()) {
            accountBalance.setValue(ValueFactory.createValueModel(0.0));
        }
        else {
        accountBalance.setValue(valueSample.get(0));
        }
        return Arrays.asList(accountBalance);



    }

    private Point getAccountBalancePoint(User user) throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> account = entityService.getEntityByName(user,
                name, EntityType.point);
        Point accountBalance;
        if (account.isEmpty()) {
            accountBalance =  createAccountBalancePoint(user);
        }
        else {
            accountBalance = (Point) account.get(0);
        }
        return accountBalance;
    }

    @Override
    public void updateBilling(User user, boolean billingEnabled, double maxQuota) throws NimbitsException {
         user.setBillingEnabled(billingEnabled);
         entityService.addUpdateEntity(user, user);

         Point accountBalance = getAccountBalancePoint(user);
         accountBalance.setDeltaAlarm(maxQuota);
         if (billingEnabled) {
             accountBalance.setDeltaAlarmOn(true);
             accountBalance.setDeltaAlarm(maxQuota);
             accountBalance.setLowAlarmOn(true);

         }
        else {
             accountBalance.setDeltaAlarmOn(false);
             accountBalance.setLowAlarmOn(false);

         }
        entityService.addUpdateEntity(user, accountBalance);
        addUpdateAccountBalanceSubscriptions(user, accountBalance);



    }

    @Override
    public List<User> getAllUsers(String s, int count) {
        return userCache.getAllUsers(s, count);
    }

    @Override
    public double processCoupon(String value) throws NimbitsException {
        if (value.equals("OHS2012")) {
            List<Point> sample = getAccountBalance();
            double dollarValue = 5.00;

            if (! sample.isEmpty()) {
                Point p = sample.get(0);
                if (p.getValue() != null && p.getValue().getDoubleValue() > 0.0) {
                    throw new NimbitsException("Sorry, the coupon you entered can only be used to fund a new account once.");

                }

            }
            User user = getHttpRequestUser(this.getThreadLocalRequest());
            updateBilling(user, true, 0.00);
            fundAccount(user, BigDecimal.valueOf(dollarValue));
            return dollarValue;

        }
        else {
            throw new NimbitsException("That wasn't a valid coupon code.");
        }



    }

    private void addUpdateBillingSubscription(final User user,
                                              final boolean billingEnabled,
                                              final Point accountBalance,
                                              final String entityName,
                                              final String desc,
                                              final SubscriptionType type,
                                              final int maxRepeat) throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(entityName, EntityType.subscription);

        List<Entity> sample = entityService.getEntityByName(user,
                name, EntityType.subscription);

        Subscription subscription;
        if (sample.isEmpty()) {
            createBillingSubscription(user, accountBalance, entityName, desc, type, maxRepeat);
        }
        else {
            subscription = (Subscription) sample.get(0);
            subscription.setEnabled(billingEnabled);
            entityService.addUpdateEntity(user, subscription);
        }
    }
    private Subscription createBillingSubscription(
            final User user,
            final Point accountBalancePoint,
            final String name,
            final String desc,
            final SubscriptionType type,
            final int maxRepeat) throws NimbitsException {
        EntityName entityName = CommonFactoryLocator.getInstance().createName(name, EntityType.subscription);
        Entity quotaEntity = EntityModelFactory.createEntity(entityName, desc, EntityType.subscription, ProtectionLevel.onlyMe,
                accountBalancePoint.getKey(), user.getKey());
        Subscription subscription = SubscriptionFactory.createSubscription(quotaEntity, accountBalancePoint.getKey(),
                type, SubscriptionNotifyMethod.email, maxRepeat, false,user.isBillingEnabled());
        return (Subscription) entityService.addUpdateEntity(user, subscription);
    }





    @Override
    public AccessKey authenticatedKey(final Entity user) throws NimbitsException {

        final EntityName name = CommonFactoryLocator.getInstance().createName("AUTHENTICATED_KEY", EntityType.accessKey);
        final Entity en = EntityModelFactory.createEntity(name, "",EntityType.accessKey, ProtectionLevel.onlyMe, user.getKey(), user.getKey());
        return AccessKeyFactory.createAccessKey(en, "AUTHENTICATED_KEY", user.getKey(), AuthLevel.admin);
    }

    @Override
    public com.nimbits.client.model.user.User createUserRecord(final EmailAddress internetAddress) throws NimbitsException {
        final EntityName name = CommonFactoryLocator.getInstance().createName(internetAddress.getValue(), EntityType.user);
        final Entity entity =  EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                name.getValue(), name.getValue(), name.getValue());

        final com.nimbits.client.model.user.User newUser = UserModelFactory.createUserModel(entity);
        // newUser.setSecret(UUID.randomUUID().toString());
        User user =  (User) entityService.addUpdateEntity(newUser);

        if (settingsService.getBooleanSetting(SettingType.billingEnabled)) {
            createAccountBalancePoint(user);
        }
        feedService.createFeedPoint(user);

        return user;


    }


    protected Point createAccountBalancePoint(final User user) throws NimbitsException {

        final EntityName name = CommonFactoryLocator.getInstance().createName(Const.ACCOUNT_BALANCE, EntityType.point);
        final Entity entity = EntityModelFactory.createEntity(name,  ACCOUNT_BALANCE_DESC, EntityType.point,
                ProtectionLevel.onlyMe, user.getKey(), user.getKey(), UUID.randomUUID().toString());


        final Point point = PointModelFactory.createPointModel(
                entity, 0.0, EXPIRE, UNIT, 0.0, false, false, false, 0, false, FilterType.floor, 0.0,
                false, PointType.accountBalance, 86400, false, 0.0);
        log.info("Creating account balance point");
        final Point accountBalance =  (Point) entityService.addUpdateEntity(user, point);
        addUpdateAccountBalanceSubscriptions(user, accountBalance);

        return accountBalance;
    }

    private void addUpdateAccountBalanceSubscriptions(final User user, final Point accountBalance) throws NimbitsException {
        addUpdateBillingSubscription(user, user.isBillingEnabled(), accountBalance, QUOTA_EXCEEDED_NAME, QUOTA_EXCEEDED_DESC, SubscriptionType.deltaAlert, MAX_REPEAT);
        addUpdateBillingSubscription(user, user.isBillingEnabled(), accountBalance, ZERO_BALANCE_ALERT_NAME, ZERO_BALANCE_DESC, SubscriptionType.low, MAX_REPEAT);
        addUpdateBillingSubscription(user, user.isBillingEnabled(), accountBalance, ACCOUNT_FUNDED_NAME, ACCOUNT_FUNDED_DESC, SubscriptionType.increase, 60);
    }


    @Override
    public void fundAccount(final User user, final BigDecimal amount) throws NimbitsException {

        final EntityName name = CommonFactoryLocator.getInstance().createName(Const.ACCOUNT_BALANCE, EntityType.point);
        final List<Entity> accountPointSample = entityService.getEntityByName(user, name, EntityType.point);
        final Point account;
        if (accountPointSample.isEmpty()) {
            account = createAccountBalancePoint(user);
        }
        else {
            account = (Point) accountPointSample.get(0);
        }
        List<Value> currentValueSample = valueService.getCurrentValue(account);
        if (currentValueSample.isEmpty()) {

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -2);
            Value value = ValueFactory.createValueModel(amount.doubleValue(), c.getTime() );


            valueService.recordValue(user, account, value);
        }
        else {
            Value currentValue = currentValueSample.get(0);
            Value value = ValueFactory.createValueModel(currentValue.getDoubleValue() + amount.doubleValue() );
            valueService.recordValue(user, account, value);
        }



    }

    @Override
    public User getAdmin() throws NimbitsException {
        final String adminStr = settingsService.getSetting(SettingType.admin);
        if (Utils.isEmptyString(adminStr)) {
            throw new NimbitsException("Server is missing admin setting!");
        }
        else {
            final User u = new UserModel();
            u.setName(CommonFactoryLocator.getInstance().createName(adminStr, EntityType.user));
            u.setKey(adminStr);
            u.addAccessKey(createAccessKey(u, AuthLevel.admin));
            u.setParent(adminStr);
            u.setUserAdmin(true);

            return u;
        }
    }

    private static AccessKey createAccessKey(final Entity u, final AuthLevel authLevel) throws NimbitsException {

        final Entity en = EntityModelFactory.createEntity(u.getName(), "", EntityType.accessKey, ProtectionLevel.onlyMe,
                u.getName().getValue(),u.getName().getValue());
        return AccessKeyFactory.createAccessKey(en, u.getName().getValue(), u.getName().getValue(),authLevel );

    }

    @Override
    public User getAnonUser()  {
        User u = new UserModel();
        try {
            String adminStr = Const.CONST_ANON_EMAIL;
            u.setName(CommonFactoryLocator.getInstance().createName(adminStr, EntityType.user));
        } catch (NimbitsException e) {
            return u;
        }


        return u;
    }

    @Override
    public User getAppUserUsingGoogleAuth() throws NimbitsException {
        final com.google.appengine.api.users.UserService u = UserServiceFactory.getUserService();
        //u.getCurrentUser().

        User retObj = null;
        if (u.getCurrentUser() != null) {
            final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(u.getCurrentUser().getEmail());
            List<Entity> result = entityService.getEntityByKey(getAnonUser(), emailAddress.getValue(), EntityType.user);
            if (! result.isEmpty()) {
                retObj = (User) result.get(0);
            }

        }

        return retObj;
    }

    @Override
    public User getUserByKey(final String key, AuthLevel authLevel) throws NimbitsException {
        List<Entity> result = entityService.getEntityByKey(getAnonUser(), key, EntityType.user);
        if (result.isEmpty()) {
            throw new NimbitsException(UserMessages.ERROR_USER_NOT_FOUND);
        } else {
            User retObj = (User) result.get(0);
            AccessKey k = createAccessKey(retObj, authLevel);
            retObj.addAccessKey(k);

            return retObj;
        }

    }

    @Override
    public void sendConnectionRequest(final EmailAddress email) throws NimbitsException {
        final User user = getAppUserUsingGoogleAuth();
        final ConnectionRequest f = userCache.makeConnectionRequest(user, email);


        if (f != null) {
            emailService.sendEmail(email, getConnectionInviteEmail(user.getEmail()));
            feedService.postToFeed(user, "A connection request has been emailed to " +
                    email.getValue() + ". If they approve, you will see any data object of theirs that have " +
                    "their permission set to be viewable by the public or connections", FeedType.info);


        }


    }

    public static String getConnectionInviteEmail(final CommonIdentifier email) {
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
    public List<ConnectionRequest> getPendingConnectionRequests(final EmailAddress email) throws NimbitsException {
        return userCache.getPendingConnectionRequests(email);
    }

    @Override
    public List<User> getConnectionRequests(final List<String> connections) throws NimbitsException {
        return userCache.getConnectionRequests(connections);
    }

    @Override
    public void connectionRequestReply(final EmailAddress targetEmail,
                                       final EmailAddress requesterEmail,
                                       final Long key,
                                       final boolean accepted) throws NimbitsException {
        final User acceptor = getAppUserUsingGoogleAuth();
        List<Entity> result = entityService.getEntityByKey(getAnonUser(), requesterEmail.getValue(), EntityType.user);

        if (result.isEmpty()) {
            throw new NimbitsException(UserMessages.ERROR_USER_NOT_FOUND);
        } else {
            final User requester = (User) result.get(0);
            EntityName a = CommonFactoryLocator.getInstance().createName(acceptor.getEmail().getValue(), EntityType.userConnection);
            EntityName r = CommonFactoryLocator.getInstance().createName(requester.getEmail().getValue(), EntityType.userConnection);
            final Entity rConnection = EntityModelFactory.createEntity(a, "", EntityType.userConnection, ProtectionLevel.onlyMe,  requester.getKey(), requester.getKey(), UUID.randomUUID().toString());
            final Entity aConnection = EntityModelFactory.createEntity(r, "", EntityType.userConnection, ProtectionLevel.onlyMe, acceptor.getKey(), acceptor.getKey(), UUID.randomUUID().toString());
            Connection ac = ConnectionFactory.createCreateConnection(aConnection);
            Connection rc = ConnectionFactory.createCreateConnection(rConnection);
            entityService.addUpdateEntity(acceptor, ac);
            entityService.addUpdateEntity(requester, rc);
            userCache.updateConnectionRequest(key, requester, acceptor, accepted);

        }






    }


    public void setEntityService(EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    public EntityServiceImpl getEntityService() {
        return entityService;
    }

    public void setFeedService(FeedImpl feedService) {
        this.feedService = feedService;
    }

    public FeedImpl getFeedService() {
        return feedService;
    }

    public void setValueService(ValueServiceImpl valueService) {
        this.valueService = valueService;
    }

    public ValueServiceImpl getValueService() {
        return valueService;
    }



    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }

    public void setEmailService(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    public EmailServiceImpl getEmailService() {
        return emailService;
    }

    public void setUserCache(UserCacheImpl userCache) {
        this.userCache = userCache;
    }

    public UserCacheImpl getUserCache() {
        return userCache;
    }

    public void setQuotaManager(QuotaManagerImpl quotaManager) {
        this.quotaManager = quotaManager;
    }

    public QuotaManagerImpl getQuotaManager() {
        return quotaManager;
    }
}
