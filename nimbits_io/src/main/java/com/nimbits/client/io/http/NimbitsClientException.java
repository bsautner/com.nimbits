package com.nimbits.client.io.http;

public class NimbitsClientException extends RuntimeException {



    public NimbitsClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public NimbitsClientException(String message) {
        super(message);
    }
}
