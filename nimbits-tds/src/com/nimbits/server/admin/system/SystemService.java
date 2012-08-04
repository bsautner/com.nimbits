package com.nimbits.server.admin.system;

import com.nimbits.client.exception.NimbitsException;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/27/12
 * Time: 4:33 PM
 */
public interface SystemService {

    void updateSystemPoint(String pointName, double value, boolean incrementAsCounter) throws NimbitsException;

}
