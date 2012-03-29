package com.nimbits.server.quota;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.user.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:27 PM
 */
public interface Quota {

    void incrementCounter() throws NimbitsException;
    void resetCounter() throws NimbitsException;
    int getCount() throws NimbitsException;

}
