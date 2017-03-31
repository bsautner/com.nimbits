package com.nimbits.client.ex;

public class NimbitsClientException extends RuntimeException {

    private final int code;
    private final Throwable cause;

    public NimbitsClientException(Throwable cause) {
        super(cause);
        this.code = 500;
        this.cause = cause;
    }

    public int getCode() {
        return code;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
