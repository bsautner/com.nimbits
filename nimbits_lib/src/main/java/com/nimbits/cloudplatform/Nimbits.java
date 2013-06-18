package com.nimbits.cloudplatform;

import com.nimbits.cloudplatform.auth.GoogleAuthentication;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.location.Location;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.http.UrlContainer;
import com.nimbits.cloudplatform.transaction.Transaction;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 3:57 PM
 */
public class Nimbits {


    public static UrlContainer base;
    public static boolean isExternalStorageAvailable;
    public static User session;
    public static Cookie cookie;

    public static int availableMemory;
    public static File cacheDir;
    public static List<Entity> tree;
    public static Location location;
    public static SimpleValue<String> token;
    public static LoginListener listener;
    public static List<String> authKey;
    public static Entity currentEntity;

    static {
       token = SimpleValue.getEmptyInstance();
        authKey = Collections.emptyList();
    }

    public static Entity getParentEntity() {
        for (Entity e :tree ) {
            if (Nimbits.currentEntity.getParent().equals(e.getKey())) {
               return e;

            }
        }
       return session;
    }
    public static void setLoginListener(LoginListener aListener) {
        listener = aListener;
    }
    public interface LoginListener {
        void loginSuccess(User session);

        void loginFail(String reason);

    }


    public static  void login(final String instanceUrl, final String email, final String password) {
        new Thread(new Runnable() {
            public void run() {

                base = (UrlContainer.getInstance(instanceUrl));

                List<Cookie> cookies = GoogleAuthentication.getAuthCookies(base, SimpleValue.getInstance(email), SimpleValue.getInstance(password));

                if (!cookies.isEmpty()) {
                    cookie = (cookies.get(0));

                    List<User> sample;

                    sample = Transaction.getSession();
                    if (!sample.isEmpty()) {
                        User user = sample.get(0);
                        session = (user);
                        listener.loginSuccess(user);
                    } else {
                        listener.loginFail("You authenticated ok, but we couldn't get your session");
                    }


                } else {
                    listener.loginFail("The credentials you provided were rejected by Google Client Logon");
                }
            }
        }).start();


    }

    public static void loginWithKey(final String instanceUrl, final String email, final String key) {
        new Thread(new Runnable() {
            public void run() {

                base = (UrlContainer.getInstance(instanceUrl));


                List<User> sample;

                sample = Transaction.getSession(email, key);
                if (!sample.isEmpty()) {
                    User user = sample.get(0);
                    session = (user);
                    authKey = new ArrayList<String>(1);
                    authKey.add(key);
                    listener.loginSuccess(user);
                } else {
                    listener.loginFail("You authenticated ok, but we couldn't get your session");
                }


            }

        }).start();


    }






    public static SimpleValue<String> getToken() {
        return token == null ? SimpleValue.getEmptyInstance() : token;
    }

}
