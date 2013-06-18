package com.nimbits.cloudplatform.reciever;

/**
 * Author: Benjamin Sautner
 * Date: 1/20/13
 * Time: 11:11 AM
 */
public class Config {
    public static final String SERVER_BASE_URL = "http://jumpnote.appspot.com";
    public static final String SERVER_RPC_URL = SERVER_BASE_URL + "/jumpnoterpc";
    public static final String SERVER_AUTH_URL_TEMPLATE = SERVER_BASE_URL + "/_ah/login?auth=%s";

    /**
     * Enabling this is will turn on sync UI icons that notify the user of pending and active sync
     * status. This is useful for debugging, but generally not recommended for real applications,
     * as sync on Android is intended to be unobtrusive. Users can get the same sync status info
     * by going to Settings > Accounts & Sync.
     */
    public static final boolean ENABLE_SYNC_UI = false;

    @SuppressWarnings("unchecked")
    public static String makeLogTag(Class cls) {
        String tag = "JumpNote_" + cls.getSimpleName();
        return (tag.length() > 23) ? tag.substring(0, 23) : tag;
    }
}