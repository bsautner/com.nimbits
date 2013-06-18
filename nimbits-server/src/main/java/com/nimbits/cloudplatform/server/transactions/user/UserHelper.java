package com.nimbits.cloudplatform.server.transactions.user;

import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: Benjamin Sautner
 * Date: 1/1/13
 * Time: 6:50 PM
 */
public class UserHelper {

    public static final ThreadLocal req = new ThreadLocal();

    public static void init(HttpServletRequest s){
        req.set(s);
    }
    public static HttpServletRequest get(){
        return (HttpServletRequest)req.get();
    }

    public static User getUser()  {

       try {
            return UserTransaction.getHttpRequestUser(get());
        } catch (Exception e) {
           LogHelper.logException(UserHelper.class, e);
            return UserTransaction.getAnonUser();
        }

    }
}
