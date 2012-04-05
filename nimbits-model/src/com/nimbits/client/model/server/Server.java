package com.nimbits.client.model.server;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;

import java.io.*;
import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:47 PM
 */
public interface Server  extends Serializable {
    int getIdServer();

    String getBaseUrl();

    EmailAddress getOwnerEmail() throws NimbitsException;

    String getServerVersion();

    Date getTs();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
