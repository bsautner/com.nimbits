package com.nimbits.server.admin.system;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/27/12
 * Time: 4:33 PM
 */
public class SystemServiceFactory {

    private SystemServiceFactory() {
    }

    private static class SystemServiceHolder {
        static final SystemService instance = new SystemServiceImpl();

        private SystemServiceHolder() {
        }
    }

    public static SystemService getInstance() {
        return SystemServiceHolder.instance;

    }
}
