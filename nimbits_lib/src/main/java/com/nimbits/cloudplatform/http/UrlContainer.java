package com.nimbits.cloudplatform.http;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 2:34 PM
 */
public class UrlContainer {

    private String url;


    public static UrlContainer getInstance(final String url) {
        final UrlContainer instance = new UrlContainer();
        instance.setUrl(url);
        return instance;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }

    public static UrlContainer combine(final UrlContainer base, final UrlContainer... containers) {
        StringBuilder sb = new StringBuilder(base.getUrl());
        for (UrlContainer c : containers) {
            sb.append(c);

        }
        return UrlContainer.getInstance(sb.toString());


    }

    public String getSSLUrl() {
        return url.replace("http://", "https://");
    }
}
