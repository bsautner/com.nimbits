package com.nimbits;


@Deprecated
public class Nimbits extends com.nimbits.client.io.Nimbits{
    private Nimbits(String email, String token, String instance) {
        super(email, token, instance);
    }

    public static class Builder extends com.nimbits.client.io.Nimbits.Builder {

    }
}
