package com.nimbits.server.core;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 10:31 AM
 */
public class CoreFactory {

    // private Core instance;

    public static Core getInstance() {

        return new CoreImpl();

    }

}
