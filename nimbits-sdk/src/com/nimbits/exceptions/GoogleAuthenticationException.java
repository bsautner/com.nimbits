/*
 * Copyright (c) 2011. Tonic Solutions, LLC. All Rights Reservered. This Code is distributed under GPL V3 without any warrenty.
 */

package com.nimbits.exceptions;

import com.nimbits.client.model.Const;

public class GoogleAuthenticationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;

    private String baseURL;
    private String message;

    public GoogleAuthenticationException() {
    }

    public GoogleAuthenticationException(String m) {
        message = m;

    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }


    @Override
    public String getMessage() {
        return message;
    }


}
